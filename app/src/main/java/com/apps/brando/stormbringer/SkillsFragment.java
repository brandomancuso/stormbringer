package com.apps.brando.stormbringer;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class SkillsFragment extends Fragment implements Editable, EditModeListener{

    String[] skill_names = null;
    boolean[] skill_checks = null;
    Resources res;
    ListView list;
    SkillsAdapter adapter;
    Sheet sheet;
    SheetActivity activity;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = (SheetActivity) getActivity();
        return inflater.inflate(R.layout.skills_fragment_layout, container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sheet = activity.temp_sheet;
        res = getResources();
        skill_names = res.getStringArray(R.array.names);
        if(sheet.skills.isEmpty()){
            int[] tmp = res.getIntArray(R.array.base_values);
            for(int i = 0; i<tmp.length; i++){
                sheet.skills.add(tmp[i]);
            }
        }
        skill_checks = new boolean[skill_names.length];
        for (boolean c : skill_checks)
            c = false;
        list = view.findViewById(R.id.skill_list);
        adapter = new SkillsAdapter(activity, skill_checks, sheet);
        list.setAdapter(adapter);
        setEditMode(activity.edit_mode);
        activity.addEditModeListener(this);


    }

    @Override
    public void onDestroyView() {
        activity.removeEditModeListener(this);
        setEditMode(false);
        super.onDestroyView();
    }

    public void setEditMode(boolean set){
        adapter.setEditMode(set);
        list.invalidateViews();

    }


    @Override
    public void onEditModeChanged(boolean edit_mode) {
        setEditMode(edit_mode);
    }
    @Override
    public void undoChanges() {
        sheet = activity.temp_sheet;
        adapter = new SkillsAdapter(activity, skill_checks, sheet);
        list.setAdapter(adapter);
    }
}
