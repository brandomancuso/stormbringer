package com.apps.brando.stormbringer;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MagicFragment extends Fragment implements Editable, EditModeListener{
    Sheet sheet;
    TextInputEditText magic, grimoire;
    SheetActivity activity;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = (SheetActivity) getActivity();
        return inflater.inflate(R.layout.magic_fragment, container, false);
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sheet = activity.temp_sheet;
        magic = view.findViewById(R.id.magic_in_memory_field);
        grimoire = view.findViewById(R.id.grimoire_field);
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

    private void updateValues(){
        magic.setText(sheet.magic_in_memory);
        grimoire.setText(sheet.grimoire);
    }


    @Override
    public void setEditMode(boolean set) {
        magic.setEnabled(set);
        grimoire.setEnabled(set);
        if(!set){
            sheet.magic_in_memory = magic.getText().toString();
            sheet.grimoire = grimoire.getText().toString();
        }
    }

    @Override
    public void onEditModeChanged(boolean edit_mode) {
        setEditMode(edit_mode);
    }
    @Override
    public void undoChanges() {
        sheet= activity.temp_sheet;
        updateValues();

    }
}
