package com.apps.brando.stormbringer;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class SheetActivity extends AppCompatActivity implements Toolbar.OnMenuItemClickListener{

    private Sheet sheet;
    Sheet temp_sheet;
    Intent data;
    Toolbar toolbar;
    ViewPager pager;
    FragmentAdapter adapter;
    boolean edit_mode = false;
    boolean undo_flag = false;
    ArrayList<EditModeListener> listeners;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sheet);

        //setting up toolbar
        toolbar = findViewById(R.id.toolbar_custom);
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Sheet");

        //getting data
        data = getIntent();
        sheet = (Sheet) data.getSerializableExtra("data");
        temp_sheet = new Sheet(sheet);
        listeners = new ArrayList<>();

        TabLayout slider = findViewById(R.id.slider);
        pager = findViewById(R.id.sheet_pager);
        adapter = new FragmentAdapter(this, getSupportFragmentManager());
        pager.setAdapter(adapter);
        slider.setupWithViewPager(pager);

        


    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if (edit_mode){
            inflater.inflate(R.menu.sheet_menu_2, menu);
        }else{
            inflater.inflate(R.menu.sheet_menu_1,menu);
        }
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_edit_icon:
                edit_mode = true;
                notifyListeners();
                invalidateOptionsMenu();
                return true;
            case R.id.menu_save_icon:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Saving changes");
                builder.setMessage("Do you really want to save changes?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        edit_mode = false;
                        notifyListeners();
                        invalidateOptionsMenu();
                        sheet = new Sheet(temp_sheet);
                        pushChanges();
                    }
                });
                builder.setNegativeButton("No", null);
                builder.create().show();
                return true;
            case R.id.menu_undo_icon:
                AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
                builder2.setTitle("Undo changes");
                builder2.setMessage("Do you really want to undo changes?");
                builder2.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        edit_mode = false;
                        undo_flag = true;
                        temp_sheet = new Sheet(sheet);
                        updateListeners();
                        notifyListeners();
                        undo_flag = false;
                        invalidateOptionsMenu();
                    }
                });
                builder2.setNegativeButton("No", null);
                builder2.create().show();
                return true;
            default:
                return false;
        }
    }

    private void pushChanges() {
        String uid = sheet.owner;
        if(uid != null){
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(uid)
                    .child("sheets").child(sheet.data_key);
            ref.setValue(sheet);
            if(sheet.image != null){
                uploadPortrait(sheet.image);
            }
        }
    }

    private void uploadPortrait(Bitmap b){
        StorageReference storage_ref = FirebaseStorage.getInstance().getReference();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        b.getAllocationByteCount();
        b.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        storage_ref.child(sheet.data_key + ".jpg").putBytes(data).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    sheet.portrait = true;
                }
            }
        });
    }

    private void notifyListeners(){
        for(EditModeListener e : listeners){
            e.onEditModeChanged(edit_mode);
        }
    }

    private void updateListeners(){
        for(EditModeListener e : listeners){
            e.undoChanges();
        }
    }

    public void addEditModeListener(EditModeListener l){
        listeners.add(l);
    }

    public void removeEditModeListener(EditModeListener l){
        listeners.remove(l);
    }

    /**
     * This method is called whenever the user chooses to navigate Up within your application's
     * activity hierarchy from the action bar.
     * <p>
     * <p>If a parent was specified in the manifest for this activity or an activity-alias to it,
     * default Up navigation will be handled automatically. See
     * {@link #getSupportParentActivityIntent()} for how to specify the parent. If any activity
     * along the parent chain requires extra Intent arguments, the Activity subclass
     * should override the method {@link #onPrepareSupportNavigateUpTaskStack(TaskStackBuilder)}
     * to supply those arguments.</p>
     * <p>
     * <p>See <a href="{@docRoot}guide/topics/fundamentals/tasks-and-back-stack.html">Tasks and
     * Back Stack</a> from the developer guide and
     * <a href="{@docRoot}design/patterns/navigation.html">Navigation</a> from the design guide
     * for more information about navigating within your app.</p>
     * <p>
     * <p>See the {@link TaskStackBuilder} class and the Activity methods
     * {@link #getSupportParentActivityIntent()}, {@link #supportShouldUpRecreateTask(Intent)}, and
     * {@link #supportNavigateUpTo(Intent)} for help implementing custom Up navigation.</p>
     *
     * @return true if Up navigation completed successfully and this Activity was finished,
     * false otherwise.
     */
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
