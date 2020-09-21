package com.apps.brando.stormbringer;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, Toolbar.OnMenuItemClickListener {

    private FirebaseAuth auth;
    private DatabaseReference mDatabase;

    Button campaigns_btn;
    Button sheets_btn;
    TextView access_confirmed;
    LinearLayout access_requested;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Setting up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_custom);
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(this);

        //Setting up buttons
        campaigns_btn = findViewById(R.id.manage_campaigns_btn);
        campaigns_btn.setOnClickListener(this);
        sheets_btn = findViewById(R.id.manage_sheets_btn);
        sheets_btn.setOnClickListener(this);

        //managing authentication
        access_requested = findViewById(R.id.access_request);
        access_confirmed = findViewById(R.id.access_confirmed);

        auth = FirebaseAuth.getInstance();
        FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                checkUserState(user);

            }
        };
        auth.addAuthStateListener(authStateListener);




    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.manage_campaigns_btn:
                Intent campaignList = new Intent(MainActivity.this, CampaignsListActivity.class);
                startActivity(campaignList);
                break;
            case R.id.manage_sheets_btn:
                Intent sheetList = new Intent(MainActivity.this, SheetsListActivity.class);
                startActivity(sheetList);
                break;
        }
    }

    private void setEnabledMode(boolean set){
        campaigns_btn.setEnabled(set);
        sheets_btn.setEnabled(set);
        if(set){
            access_confirmed.setVisibility(View.VISIBLE);
            access_requested.setVisibility(View.GONE);
        } else {
            access_confirmed.setVisibility(View.GONE);
            access_requested.setVisibility(View.VISIBLE);
            TextView login = findViewById(R.id.login_text);
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent login = new Intent(MainActivity.this, LogInActivity.class);
                    startActivity(login);
                }
            });
            TextView signup = findViewById(R.id.signup_text);
            signup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent signup = new Intent(MainActivity.this, SignUpActivity.class);
                    startActivity(signup);
                }
            });
        }
    }

    private void checkUserState(FirebaseUser user){
        if(user != null){
            setEnabledMode(true);
        } else{
            setEnabledMode(false);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.log_out:
                auth.signOut();
                invalidateOptionsMenu();
                return true;
            default:
                return false;


        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(auth.getCurrentUser() != null) getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        else invalidateOptionsMenu();
        return true;
    }

}
