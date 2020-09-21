package com.apps.brando.stormbringer;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class BackgroundFragment extends Fragment implements Editable, EditModeListener{

    Sheet sheet;
    TextInputEditText bgtext, notes;
    SheetActivity activity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = (SheetActivity) getActivity();
        return inflater.inflate(R.layout.background_fragment, container, false);
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sheet = activity.temp_sheet;
        bgtext = view.findViewById(R.id.background_field);
        notes = view.findViewById(R.id.notes_field);
        updateValues();
        setEditMode(activity.edit_mode);
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
        bgtext.setEnabled(set);
        notes.setEnabled(set);
        if(!set){
            sheet.character_background = bgtext.getText().toString();
            sheet.character_notes = notes.getText().toString();
        }

    }
    private void updateValues(){
        bgtext.setText(sheet.character_background);
        notes.setText(sheet.character_notes);
    }

    @Override
    public void onEditModeChanged(boolean edit_mode) {
        setEditMode(edit_mode);
    }

    @Override
    public void undoChanges() {
        sheet = activity.temp_sheet;
        updateValues();

    }
}