package com.apps.brando.stormbringer;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

import java.util.ArrayList;
import java.util.Collection;

public class CampaignSheetsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, android.support.v7.widget.Toolbar.OnMenuItemClickListener {

    SheetsAdapter adapter;
    ListView list;
    ArrayList<Sheet> sheets = new ArrayList<>();
    DatabaseReference root_ref = FirebaseDatabase.getInstance().getReference();
    String uid = FirebaseAuth.getInstance().getUid();
    Campaign campaign;
    boolean dungeon_master, playerHasSheet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campaign_sheets);
        //setting up toolbar
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar_custom);
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //setting up list view
        list = findViewById(R.id.campaign_sheets_list);
        adapter = new SheetsAdapter(CampaignSheetsActivity.this, sheets);
        list.setAdapter(adapter);
        list.setOnItemClickListener(this);
        campaign = (Campaign) getIntent().getExtras().get("Campaign");
        dungeon_master = campaign.dm_id.equals(uid);
        init();

        getSupportActionBar().setTitle(campaign.campaign_name);
    }

    private void findSheet(String uid){
        root_ref.child("users").child(uid).child("sheets").child(campaign.sheets.get(uid)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Sheet s = dataSnapshot.getValue(Sheet.class);
                addSheet(s);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void addSheet(Sheet s){
        for(Sheet a: sheets){
            if(a.data_key.equals(s.data_key)){
                sheets.remove(a);
                break;
            }
        }
        sheets.add(s);
        adapter.notifyDataSetChanged();
    }

    /**
     * Callback method to be invoked when an item in this AdapterView has
     * been clicked.
     * <p>
     * Implementers can call getItemAtPosition(position) if they need
     * to access the data associated with the selected item.
     *
     * @param parent   The AdapterView where the click happened.
     * @param view     The view within the AdapterView that was clicked (this
     *                 will be a view provided by the adapter)
     * @param position The position of the view in the adapter.
     * @param id       The row id of the item that was clicked.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Sheet sheet = adapter.getItem(position);
        openSheet(sheet);
    }

    /**
     * Prepare the Screen's standard options menu to be displayed.  This is
     * called right before the menu is shown, every time it is shown.  You can
     * use this method to efficiently enable/disable items or otherwise
     * dynamically modify the contents.
     * <p>
     * <p>The default implementation updates the system menu items based on the
     * activity's state.  Deriving classes should always call through to the
     * base class implementation.
     *
     * @param menu The options menu as last shown or first initialized by
     *             onCreateOptionsMenu().
     * @return You must return true for the menu to be displayed;
     * if you return false it will not be shown.
     * @see #onCreateOptionsMenu
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if(dungeon_master){
            inflater.inflate(R.menu.campaign_menu_dm, menu);
            return true;
        }else{
            if(!playerHasSheet){
                inflater.inflate(R.menu.campaign_menu_player, menu);
                return true;
            }
            return false;
        }
    }

    /**
     * Called when a menu item has been invoked.  This is the first code
     * that is executed; if it returns true, no other callbacks will be
     * executed.
     *
     * @param item The menu item that was invoked.
     * @return Return true to consume this click and prevent others from
     * executing.
     */
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch(item.getItemId()){
            case R.id.invite:
                View dial_view = LayoutInflater.from(CampaignSheetsActivity.this).inflate(R.layout.invite_campaign_dialog, null);
                TextView code = dial_view.findViewById(R.id.campaign_key_display);
                final Button share = dial_view.findViewById(R.id.share_on_wapp);
                code.setText(campaign.campaign_id);
                AlertDialog.Builder builder = new AlertDialog.Builder(CampaignSheetsActivity.this);
                builder.setTitle("Invite your friends");
                builder.setView(dial_view);
                builder.setPositiveButton("Ok", null);
                final AlertDialog dialog = builder.create();
                share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        share.setEnabled(false);
                        //Toast.makeText(CampaignSheetsActivity.this, "Not implemented yet", Toast.LENGTH_SHORT).show();
                        Task<ShortDynamicLink> link = FirebaseDynamicLinks.getInstance().createDynamicLink()
                                .setLink(Uri.parse("http://stormbringer.app/" + campaign.campaign_id))
                                .setDynamicLinkDomain("y458p.app.goo.gl")
                                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                                .buildShortDynamicLink()
                                .addOnCompleteListener(new OnCompleteListener<ShortDynamicLink>() {
                                    @Override
                                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                                        if(task.isSuccessful()){
                                            Uri uri = task.getResult().getShortLink();
                                            Intent send = new Intent(Intent.ACTION_SEND);
                                            send.putExtra(Intent.EXTRA_TEXT, uri.toString());
                                            send.setType("text/plain");
                                            startActivity(Intent.createChooser(send, "Choose one:"));
                                            dialog.dismiss();
                                        }
                                        else{
                                            Toast.makeText(CampaignSheetsActivity.this, "E' la fine", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                    }
                });
                dialog.show();
                return true;
            case R.id.new_campaign_sheet:
                Sheet s = new Sheet();
                s.owner = uid;
                s.campaign = campaign.campaign_name;
                s.data_key = root_ref.child("users").child(uid).child("sheets").push().getKey();
                root_ref.child("users").child(uid).child("sheets").child(s.data_key).setValue(s);
                campaign.sheets.put(uid, s.data_key);
                root_ref.child("campaigns").child(campaign.campaign_id).child("sheets").setValue(campaign.sheets);
                sheets.add(s);
                adapter.notifyDataSetChanged();
                playerHasSheet = true;
                invalidateOptionsMenu();
                openSheet(s);
                return true;
            case R.id.import_sheet:
                importSheet();
                return true;
            default:
                return false;
        }
    }
    private void openSheet(Sheet s){
        Intent sheetActivity = new Intent(CampaignSheetsActivity.this, SheetActivity.class);
        sheetActivity.putExtra("data", s);
        startActivity(sheetActivity);
    }

    private void importSheet(){
        root_ref.child("users").child(uid).child("sheets").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot data) {
                ArrayList<Sheet> sheets = new ArrayList<>();
                for(DataSnapshot sheet : data.getChildren()){
                    Sheet s = sheet.getValue(Sheet.class);
                    if(s.campaign.equals("no campaign")){
                        sheets.add(s);
                    }
                }
                root_ref.child("users").child(uid).child("sheets").removeEventListener(this);
                selectSheetFrom(sheets);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    /* Problem: go to CampaignListActivity, it doesn't update the data.
     */

    private void selectSheetFrom(ArrayList<Sheet> sheets){
        View dial_view = LayoutInflater.from(CampaignSheetsActivity.this).inflate(R.layout.import_sheet_dialog, null);
        final ListView tmp_list = dial_view.findViewById(R.id.import_list);
        final SheetsAdapter tmp_adapter = new SheetsAdapter(CampaignSheetsActivity.this, sheets);
        tmp_list.setAdapter(tmp_adapter);
        AlertDialog.Builder builder = new AlertDialog.Builder(CampaignSheetsActivity.this);
        builder.setTitle("Select a sheet");
        builder.setView(dial_view);
        builder.setNegativeButton("Cancel", null);
        final AlertDialog dialog = builder.create();
        dialog.setCancelable(true);
        AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tmp_list.setEnabled(false);
                Sheet s = tmp_adapter.getItem(position);
                root_ref.child("users").child(s.owner).child("sheets").child(s.data_key).child("campaign").setValue(campaign.campaign_name);
                campaign.sheets.put(uid, s.data_key);
                root_ref.child("campaigns").child(campaign.campaign_id).child("sheets").setValue(campaign.sheets);
                findSheet(uid);
                playerHasSheet = true;
                dialog.dismiss();
            }
        };
        tmp_list.setOnItemClickListener(listener);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if(playerHasSheet) invalidateOptionsMenu();
            }
        });
        dialog.show();

    }

    private void init(){
        Collection<String> keys = campaign.sheets.keySet();
        if(keys.contains(uid)){
            String s = campaign.sheets.get(uid);
            if(!s.equals("no sheet")){
                playerHasSheet = true;
            }else{
                playerHasSheet = false;
            }
        }
        if(dungeon_master){
            for(String k : keys){
                if(!campaign.sheets.get(k).equals("no sheet")){
                    findSheet(k);
                }
            }
        }
        else{
            if(playerHasSheet){
                findSheet(uid);
            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        init();
    }
}
