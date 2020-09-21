package com.apps.brando.stormbringer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;

public class SkillsAdapter extends BaseAdapter {

    String[] names;
    private boolean[] checks;
    private LayoutInflater inflater;
    private boolean editMode;
    private Sheet sheet;
    private ArrayList<Integer> values;

    public SkillsAdapter(Context context, boolean[] checks, Sheet sheet){
        this.names = context.getResources().getStringArray(R.array.names);
        this.checks = checks;
        this.sheet = sheet;
        values = sheet.skills;
        inflater = LayoutInflater.from(context);
        editMode = false;



    }

    @Override
    public int getCount() {
        return values.size();
    }

    @Override
    public Object getItem(int position) {
        return values.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView == null)
            convertView = inflater.inflate(R.layout.skill_view_layout, parent, false);
        String name = names[position];
        boolean checked = checks[position];
        CheckBox checkbox = convertView.findViewById(R.id.skill_check_box);
        checkbox.setChecked(checked);
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checks[position] = isChecked;
            }
        });
        TextView skill_name = convertView.findViewById(R.id.skill_tag);
        skill_name.setText(name);
        final TextView val = convertView.findViewById(R.id.skill_value);
        updateValue(position, val, 0);
        Button[] buttons = new Button[4];
        buttons[0] = convertView.findViewById(R.id.skill_incr);
        buttons[1] = convertView.findViewById(R.id.skill_incr10);
        buttons[2] = convertView.findViewById(R.id.skill_decr);
        buttons[3] = convertView.findViewById(R.id.skill_decr10);
        View.OnClickListener buttonsHandler = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.skill_incr:
                        updateValue(position, val, 1);
                        break;
                    case R.id.skill_incr10:
                        updateValue(position, val, 10);
                        break;
                    case R.id.skill_decr:
                        updateValue(position, val, -1);
                        break;
                    case R.id.skill_decr10:
                        updateValue(position, val, -10);
                        break;
                }
            }
        };
        for(Button b: buttons){
            b.setOnClickListener(buttonsHandler);
        }
        if(editMode){
            for(Button b: buttons){
                b.setVisibility(View.VISIBLE);
            }
        }else{
            for(Button b: buttons) {
                b.setVisibility(View.INVISIBLE);
            }
        }
        return convertView;
    }

    private void updateValue(int position, TextView tv, int value){
        Integer tmp = values.get(position);
        values.set(position, tmp + value);
        tv.setText(String.valueOf(values.get(position)));
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }
}
