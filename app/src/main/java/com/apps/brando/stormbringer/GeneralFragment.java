package com.apps.brando.stormbringer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import static android.app.Activity.RESULT_OK;

public class GeneralFragment extends Fragment implements Editable, EditModeListener, View.OnClickListener{

    Sheet sheet;
    static final int IMG_MEMORY = 1024*1024*5;
    SheetActivity activity;
    EditText _name, _age, _sex, _birthplace, _family, _title, _chaos, _law, _balance;
    TextInputEditText _descr;
    ImageView mod_image;
    ImageView img_view;
    boolean init = true;

    private static final int PICK_IMAGE = 1;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = (SheetActivity) getActivity();
        return inflater.inflate(R.layout.general_fragment, container, false);

    }



    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        sheet = activity.temp_sheet;
        _name = v.findViewById(R.id.character_name);
        _age = v.findViewById(R.id.character_age);
        _sex = v.findViewById(R.id.character_sex);
        _birthplace = v.findViewById(R.id.character_birthplace);
        _family = v.findViewById(R.id.character_family);
        _title = v.findViewById(R.id.character_title);
        _descr = v.findViewById(R.id.character_descr);
        _chaos = v.findViewById(R.id.chaos_points);
        _law = v.findViewById(R.id.law_points);
        _balance = v.findViewById(R.id.balance_points);
        mod_image = v.findViewById(R.id.edit_image_button);
        img_view = v.findViewById(R.id.portrait_image);
        mod_image.setOnClickListener(this);
        updateValues();
        setEditMode(activity.edit_mode);
        init = false;
        activity.addEditModeListener(this);


    }

    @Override
    public void onDestroyView() {
        activity.removeEditModeListener(this);
        setEditMode(false);
        super.onDestroyView();
    }


    @Override
    public void setEditMode(boolean set) {
        _name.setEnabled(set); _age.setEnabled(set); _sex.setEnabled(set); _birthplace.setEnabled(set);
        _family.setEnabled(set); _title.setEnabled(set); _chaos.setEnabled(set); _law.setEnabled(set);
        _balance.setEnabled(set); _descr.setEnabled(set);
        if(set){
            mod_image.setVisibility(View.VISIBLE);
        }else{
            mod_image.setVisibility(View.INVISIBLE);
        }

        if(!set && !activity.undo_flag && !init){
            writeValues();
        }

    }

    @Override
    public void undoChanges() {
        sheet = activity.temp_sheet;
        updateValues();
    }

    @Override
    public void onEditModeChanged(boolean edit_mode) {
        setEditMode(edit_mode);
    }

    private void updateValues(){
        _name.setText(sheet.character_name);
        _age.setText(sheet.character_age);
        _sex.setText(sheet.character_sex);
        _birthplace.setText(sheet.character_birthplace);
        _family.setText(sheet.character_family);
        _title.setText(sheet.character_title);
        _descr.setText(sheet.character_appearence);
        _chaos.setText(String.valueOf(sheet.chaos_points));
        _balance.setText(String.valueOf(sheet.balance_points));
        _law.setText(String.valueOf(sheet.law_points));
        if(sheet.portrait){
            if(sheet.image == null){
                downloadImage();
            }else{
                setPortrait(sheet.image);
            }

        }
    }

    private void writeValues(){
        sheet.character_name = _name.getText().toString();
        sheet.character_age = _age.getText().toString();
        sheet.character_sex = _sex.getText().toString();
        sheet.character_birthplace = _birthplace.getText().toString();
        sheet.character_family = _family.getText().toString();
        sheet.character_title = _title.getText().toString();
        sheet.chaos_points = Integer.parseInt(_chaos.getText().toString());
        sheet.balance_points = Integer.parseInt(_balance.getText().toString());
        sheet.law_points = Integer.parseInt(_law.getText().toString());
        sheet.character_appearence = _descr.getText().toString();
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        Intent img_picker = new Intent(Intent.ACTION_GET_CONTENT);
        img_picker.setType("image/*");
        startActivityForResult(Intent.createChooser(img_picker, "Select an image:"), PICK_IMAGE);
    }

    /**
     * Receive the result from a previous call to
     * {@link #startActivityForResult(Intent, int)}.  This follows the
     * related Activity API as described there in
     * {@link Activity#onActivityResult(int, int, Intent)}.
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode  The integer result code returned by the child activity
     *                    through its setResult().
     * @param data        An Intent, which can return result data to the caller
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case PICK_IMAGE:
                if(resultCode == RESULT_OK){
                    Uri uri = data.getData();
                    try{
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), uri);
                        if(bitmap.getAllocationByteCount() <= IMG_MEMORY){
                            setPortrait(bitmap);
                            sheet.image = bitmap;
                            sheet.portrait = true;
                        }else{
                            Toast.makeText(activity,"Image max size 5Mb", Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
        }
    }

    private void setPortrait(Bitmap b){
        img_view.setScaleType(ImageView.ScaleType.CENTER_CROP);
        img_view.setImageBitmap(b);
    }

    private void downloadImage(){
        StorageReference img_ref = FirebaseStorage.getInstance().getReference().child(sheet.data_key + ".jpg");
        img_ref.getBytes(IMG_MEMORY).addOnCompleteListener(new OnCompleteListener<byte[]>() {
            @Override
            public void onComplete(@NonNull Task<byte[]> task) {
                if(task.isSuccessful()){
                    Bitmap b = BitmapFactory.decodeByteArray(task.getResult(), 0 , task.getResult().length);
                    setPortrait(b);
                    sheet.image = b;
                }
            }
        });
    }
}
