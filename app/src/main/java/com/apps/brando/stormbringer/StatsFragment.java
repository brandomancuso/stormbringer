package com.apps.brando.stormbringer;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class StatsFragment extends Fragment implements Editable, EditModeListener{

    StatView[] stats;
    Sheet sheet;
    TextView ideatxt ,lucktxt, dexteritytxt, charismatxt, bonustxt, hit_points, magic_points;
    TextInputEditText equip;
    SheetActivity activity;

    int INT_INDEX = 3, POW_INDEX = 4, DEX_INDEX = 5, APP_INDEX = 6,STR_INDEX = 0, SIZ_INDEX = 2, CON_INDEX = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = (SheetActivity) getActivity();
        return inflater.inflate(R.layout.stats_fragment,container,false);
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        //stats block
        stats = new StatView[7];
        stats[0] = view.findViewById(R.id.STR);
        stats[1] = view.findViewById(R.id.CON);
        stats[2] = view.findViewById(R.id.SIZ);
        stats[3] = view.findViewById(R.id.INT);
        stats[4] = view.findViewById(R.id.POW);
        stats[5] = view.findViewById(R.id.DEX);
        stats[6] = view.findViewById(R.id.APP);
        hit_points = view.findViewById(R.id.hit_points);
        magic_points = view.findViewById(R.id.magic_points);
        //rolls block
        ideatxt = view.findViewById(R.id.idea_roll);
        lucktxt = view.findViewById(R.id.luck_roll);
        dexteritytxt = view.findViewById(R.id.dexterity_roll);
        charismatxt = view.findViewById(R.id.charisma_roll);
        bonustxt = view.findViewById(R.id.dmg_bonus);
        //equipment block
        equip = view.findViewById(R.id.equipment_field);
        //set values
        sheet = activity.temp_sheet;
        setValues(sheet.stats);
        setEditMode(activity.edit_mode);
        activity.addEditModeListener(this);

    }

    public void setEditMode(boolean set){
        if(set){
            for(StatView stat : stats){
                stat.enableEditMode();
            }
        }
        else{
            for(StatView stat : stats) {
                stat.disableEditMode();
            }
            sheet.stats = getValues();
            sheet.inventory = equip.getText().toString();
            updateRolls();
        }
        equip.setEnabled(set);
    }

    public void setValues(ArrayList<Integer> vals){
        equip.setText(sheet.inventory);
        if(vals.size() != 7){
            Toast.makeText(getContext(), "error: wrong array lenght", Toast.LENGTH_SHORT).show();
            return;
        }

        for(int i =0; i<7; i++){
            stats[i].setValue(vals.get(i));
        }
        updateRolls();

    }

    private ArrayList<Integer> getValues(){
        ArrayList<Integer> values = new ArrayList<>();
        for(int i = 0; i<7;i++){
            values.add(stats[i].getValue());
        }
        return values;
    }

    private static String calcBonusDamage(int str, int siz){
        int bonus_val;
        String bonus_dice;
        int tmp = str + siz;
        int sum = tmp;
        if(sum <= 16) bonus_val = -1;
        else if(sum <= 24) bonus_val = 0;
        else if(sum <= 40) bonus_val = 1;
        else {
            sum = sum - 41;
            sum = sum / 16;
            bonus_val = sum + 2;
        }

        if(tmp < 13 || tmp > 32) bonus_dice = "d6";
        else if(tmp > 16 && tmp < 25) bonus_dice = "";
        else bonus_dice = "d4";

        return String.valueOf(bonus_val) + bonus_dice;
    }

    private void updateRolls(){
        ideatxt.setText(String.valueOf(sheet.stats.get(INT_INDEX) * 5));
        lucktxt.setText(String.valueOf(sheet.stats.get(POW_INDEX) * 5));
        dexteritytxt.setText(String.valueOf(sheet.stats.get(DEX_INDEX) * 5));
        charismatxt.setText(String.valueOf(sheet.stats.get(APP_INDEX) * 5));
        bonustxt.setText(calcBonusDamage(sheet.stats.get(STR_INDEX), sheet.stats.get(SIZ_INDEX)));
        hit_points.setText(String.valueOf((sheet.stats.get(CON_INDEX) + sheet.stats.get(SIZ_INDEX))/2));
        magic_points.setText(String.valueOf(sheet.stats.get(POW_INDEX)));
    }

    /**
     * Called when the view previously created by {@link #onCreateView} has
     * been detached from the fragment.  The next time the fragment needs
     * to be displayed, a new view will be created.  This is called
     * after {@link #onStop()} and before {@link #onDestroy()}.  It is called
     * <em>regardless</em> of whether {@link #onCreateView} returned a
     * non-null view.  Internally it is called after the view's state has
     * been saved but before it has been removed from its parent.
     */
    @Override
    public void onDestroyView() {
        activity.removeEditModeListener(this);
        setEditMode(false);
        super.onDestroyView();
    }

    @Override
    public void onEditModeChanged(boolean edit_mode) {
        setEditMode(edit_mode);
    }
    @Override
    public void undoChanges() {
        sheet = activity.temp_sheet;
        setValues(sheet.stats);
    }
}
