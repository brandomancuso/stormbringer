package com.apps.brando.stormbringer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SheetsListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener,
        Toolbar.OnMenuItemClickListener{

    private FirebaseAuth auth;
    ArrayList<Sheet> sheets;
    SheetsAdapter adapter;
    DatabaseReference sheets_ref;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.campaign_sheet_list_layout);
        auth = FirebaseAuth.getInstance();
        sheets = new ArrayList<>();

        //Setting up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_custom);
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("My Sheets");

        //Setting up ListView
        ListView list = findViewById(R.id.camp_sheet_listview);
        adapter = new SheetsAdapter(this, sheets);
        list.setAdapter(adapter);
        list.setOnItemClickListener(this);
        list.setOnItemLongClickListener(this);

        //Setting up data
        String uid = auth.getUid();
        sheets_ref = FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("sheets");
        sheets_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot sheet: dataSnapshot.getChildren()){
                    Sheet s = sheet.getValue(Sheet.class);
                    addSheet(s);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }

    private void addSheet(Sheet s) {
        for(Sheet a: sheets){
            if(a.data_key.equals(s.data_key)){
                sheets.remove(a);
                break;
            }
        }
        sheets.add(s);
    }

    private void deleteSheet(Sheet s){
        if(!s.campaign.equals("no campaign") && s.campaign != null){
            removeSheetFromCampaign(s.campaign);
        }
        sheets_ref.child(s.data_key).removeValue();
        for (Sheet a: sheets) {
            if(a.data_key.equals(s.data_key)){
                sheets.remove(a);
                break;
            }
        }
    }

    private void removeSheetFromCampaign(String campaign_name){
        final String uid = auth.getUid();
        DatabaseReference cmp_ref = FirebaseDatabase.getInstance().getReference().child("users")
                .child(uid).child("campaigns").child(campaign_name);
        cmp_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String key = dataSnapshot.getValue(String.class);
                FirebaseDatabase.getInstance().getReference().child("campaigns").child(key)
                        .child("sheets").child(uid).removeValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Sheet sheet = adapter.getItem(position);
        Intent sheetActivity = new Intent(SheetsListActivity.this, SheetActivity.class);
        sheetActivity.putExtra("data", sheet);
        startActivity(sheetActivity);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sheet_list_menu, menu);
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch(item.getItemId()){
            case R.id.create_new_sheet:
                Sheet newSheet = new Sheet();
                //set sheet owner
                newSheet.owner = auth.getUid();
                //register new sheet on firebase
                String key = sheets_ref.push().getKey();
                newSheet.data_key = key;
                sheets_ref.child(key).setValue(newSheet);
                //start sheet activity
                Intent openSheet = new Intent(SheetsListActivity.this, SheetActivity.class);
                openSheet.putExtra("data", newSheet);
                startActivity(openSheet);
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
        Sheet sheet = adapter.getItem(position);
        deleteSheet(sheet);
        return true;
    }
}
