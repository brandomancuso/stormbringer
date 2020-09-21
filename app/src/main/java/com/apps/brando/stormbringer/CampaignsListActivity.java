package com.apps.brando.stormbringer;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

import java.util.ArrayList;

public class CampaignsListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, Toolbar.OnMenuItemClickListener, AdapterView.OnItemLongClickListener {

    CampaignsAdapter adapter;
    DatabaseReference usr_campaigns_ref;
    DatabaseReference campaigns_ref;
    String uid;
    ArrayList<Campaign> campaigns = new ArrayList<>();
    ArrayList<String> campaigns_ids = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.campaign_sheet_list_layout);

        //Setting up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_custom);
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("My Campaigns");

        //Setting up list view
        ListView list = findViewById(R.id.camp_sheet_listview);
        adapter = new CampaignsAdapter(this, campaigns);
        list.setAdapter(adapter);
        list.setOnItemClickListener(this);
        list.setOnItemLongClickListener(this);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            //Getting data
            uid = FirebaseAuth.getInstance().getUid();
            campaigns_ref = FirebaseDatabase.getInstance().getReference().child("campaigns");
            usr_campaigns_ref = FirebaseDatabase.getInstance().getReference().child("users").child(uid)
                    .child("campaigns");
            usr_campaigns_ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot data) {
                    for(DataSnapshot child: data.getChildren()){
                        String s = child.getValue(String.class);
                        addCampaignId(s);
                    }
                    notifyChanges();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {            }
            });

            //Menage invitation
            FirebaseDynamicLinks.getInstance().getDynamicLink(getIntent())
                    .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                        @Override
                        public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                            Uri deeplink = null;
                            if(pendingDynamicLinkData != null){
                                deeplink = pendingDynamicLinkData.getLink();
                            }
                            if(deeplink != null){
                                //add campaign and notify the user
                                Toast.makeText(CampaignsListActivity.this, "Searching for the campaign...", Toast.LENGTH_SHORT).show();
                                final String key = deeplink.getLastPathSegment();
                                campaigns_ref.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        String name = dataSnapshot.child("campaign_name").getValue(String.class);
                                        String dm_id = dataSnapshot.child("dm_id").getValue(String.class);
                                        if(dm_id.equals(uid)){
                                            Toast.makeText(CampaignsListActivity.this, "You cannot join your own campaign...", Toast.LENGTH_SHORT).show();
                                        }else{
                                            usr_campaigns_ref.child(name).setValue(key);
                                            Toast.makeText(CampaignsListActivity.this, "You have been added to campaign: " + name, Toast.LENGTH_SHORT).show();
                                            campaigns_ref.child(key).child("sheets").child(uid).setValue("no sheet");
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }
                        }
                    })
                    .addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CampaignsListActivity.this, "Error: Sorry, something has gone wrong", Toast.LENGTH_SHORT).show();

                        }
                    });






        }else{
            Intent login = new Intent(CampaignsListActivity.this, LogInActivity.class);
            login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(login);
            finish();
        }
        }

    /**
     * Initialize the contents of the Activity's standard options menu.  You
     * should place your menu items in to <var>menu</var>.
     * <p>
     * <p>This is only called once, the first time the options menu is
     * displayed.  To update the menu every time it is displayed, see
     * {@link #onPrepareOptionsMenu}.
     * <p>
     * <p>The default implementation populates the menu with standard system
     * menu items.  These are placed in the {@link Menu#CATEGORY_SYSTEM} group so that
     * they will be correctly ordered with application-defined menu items.
     * Deriving classes should always call through to the base implementation.
     * <p>
     * <p>You can safely hold on to <var>menu</var> (and any items created
     * from it), making modifications to it as desired, until the next
     * time onCreateOptionsMenu() is called.
     * <p>
     * <p>When you add items to the menu, you can implement the Activity's
     * {@link #onOptionsItemSelected} method to handle them there.
     *
     * @param menu The options menu in which you place your items.
     * @return You must return true for the menu to be displayed;
     * if you return false it will not be shown.
     * @see #onPrepareOptionsMenu
     * @see #onOptionsItemSelected
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.campaign_list_menu, menu);
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Campaign c = (Campaign)adapter.getItem(position);
        Intent sheets = new Intent(CampaignsListActivity.this, CampaignSheetsActivity.class);
        sheets.putExtra("Campaign", c);
        startActivity(sheets);

    }

    private void addCampaignId(String id) {
        if(!campaigns_ids.contains(id)){
            campaigns_ids.add(id);
        }
    }
    private void addCampaign(Campaign campaign){
        for(Campaign c: campaigns){
            if(c.campaign_id.equals(campaign.campaign_id)){
                campaigns.remove(c);
                break;
            }
        }
        campaigns.add(campaign);
        adapter.notifyDataSetChanged();
    }

    private void notifyChanges(){
        ArrayList<String> current_ids = new ArrayList<>();
        for(Campaign c: campaigns){
            current_ids.add(c.campaign_id);
        }
        for(String s: campaigns_ids){
            if(!current_ids.contains(s)){
                campaigns_ref.child(s).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot data) {
                        Campaign campaign = data.getValue(Campaign.class);
                        addCampaign(campaign);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }
    }

    /**
     * This method will be invoked when a menu item is clicked if the item itself did
     * not already handle the event.
     *
     * @param item {@link MenuItem} that was clicked
     * @return <code>true</code> if the event was handled, <code>false</code> otherwise.
     */
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch(item.getItemId()){
            case R.id.join_campaign:
                View join_view = LayoutInflater.from(CampaignsListActivity.this).inflate(R.layout.join_campaign_dialog, null);
                final TextInputEditText code = join_view.findViewById(R.id.join_campaign_key_field);
                AlertDialog.Builder jbuilder = new AlertDialog.Builder(CampaignsListActivity.this);
                jbuilder.setTitle("Join campaign");
                jbuilder.setView(join_view);
                jbuilder.setPositiveButton("Join", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String key = code.getText().toString();
                        campaigns_ref.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    String name = dataSnapshot.child("campaign_name").getValue(String.class);
                                    usr_campaigns_ref.child(name).setValue(key);
                                    campaigns_ref.child(key).child("sheets").child(uid).setValue("no sheet");
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });
                jbuilder.setNegativeButton("Cancel", null);
                jbuilder.create().show();
                return true;
            case R.id.create_new_campaign:
                final Campaign campaign = new Campaign();
                campaign.dm_id = uid;
                final View dial_view = LayoutInflater.from(CampaignsListActivity.this).inflate(R.layout.new_campaign_dialog, null);
                DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText name = dial_view.findViewById(R.id.campaign_name_edit_text);
                        switch(which){
                            case DialogInterface.BUTTON_POSITIVE:
                                String campaign_name = name.getText().toString();
                                if(campaign_name.equals("")){
                                    Toast.makeText(CampaignsListActivity.this, "Please enter a campaign name", Toast.LENGTH_SHORT).show();
                                }else{
                                    campaign.campaign_name = campaign_name;
                                    if(!campaign.campaign_name.equals("")){
                                        String key = campaigns_ref.push().getKey();
                                        campaign.campaign_id = key;
                                        campaigns_ref.child(key).setValue(campaign);
                                        usr_campaigns_ref.child(campaign.campaign_name).setValue(key);
                                    }
                                }
                                break;
                            default:
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(CampaignsListActivity.this);
                builder.setTitle("Create new campaign");
                builder.setView(dial_view);
                builder.setPositiveButton("Create", listener);
                builder.setNegativeButton("Abort", null);
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            default:
                return false;
        }
    }

    /**
     * Callback method to be invoked when an item in this view has been
     * clicked and held.
     * <p>
     * Implementers can call getItemAtPosition(position) if they need to access
     * the data associated with the selected item.
     *
     * @param parent   The AbsListView where the click happened
     * @param view     The view within the AbsListView that was clicked
     * @param position The position of the view in the list
     * @param id       The row id of the item that was clicked
     * @return true if the callback consumed the long click, false otherwise
     */
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        final Campaign c = (Campaign)adapter.getItem(position);
        if(c.dm_id.equals(uid)){
            campaigns_ref.child(c.campaign_id).child("sheets").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot data) {
                    for(DataSnapshot sheet: data.getChildren()){
                        String player = sheet.getKey();
                        String sheet_key = sheet.getValue(String.class);
                        DatabaseReference tmp_ref = FirebaseDatabase.getInstance().getReference().child("users").child(player);
                        //rimuovi campagna da lista campagne user
                        tmp_ref.child("campaigns").child(c.campaign_name).removeValue();
                        if(!sheet_key.equals("no sheet")){
                            //rimuovi campagna da scheda
                            tmp_ref.child("sheets").child(sheet_key).child("campaign").setValue("no campaign");
                        }

                    }
                    //rimuovi campagna da lista campagne dm
                    usr_campaigns_ref.child(c.campaign_name).removeValue();
                    //rimuovi campagna da db
                    campaigns_ref.child(c.campaign_id).removeValue();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            removeCampaign(c);


        }else{
            campaigns_ref.child(c.campaign_id).child("sheets").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String key = dataSnapshot.getValue(String.class);
                    //rimuovi campagna da scheda
                    if(!key.equals("no sheet")){
                        FirebaseDatabase.getInstance().getReference().child("users").child(uid)
                                .child("sheets").child(key).child("campaign").setValue("no campaign");
                    }
                    //rimuovi campagna da user
                    usr_campaigns_ref.child(c.campaign_name).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            //rimuovi scheda da campagna
                            campaigns_ref.child(c.campaign_id).child("sheets").child(uid).removeValue();
                        }
                    });

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            removeCampaign(c);

        }
        return true;
    }

    private void removeCampaign(Campaign c){
        for(String s: campaigns_ids){
            if(s.equals(c.campaign_id)){
                campaigns_ids.remove(s);
                break;
            }
        }
        for(Campaign a: campaigns){
            if(a.campaign_id.equals(c.campaign_id)){
                campaigns.remove(a);
                break;
            }
        }
        adapter.notifyDataSetChanged();
    }
}
