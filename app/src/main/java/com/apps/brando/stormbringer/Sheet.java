package com.apps.brando.stormbringer;


import android.graphics.Bitmap;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.ArrayList;

public class Sheet implements Serializable {

    @Exclude Bitmap image;

    String data_key = "";
    String owner = "";

    boolean portrait = false;
    String character_notes = "";
    String character_background = "";
    String character_name = "no name";
    String character_birthplace = "";
    String character_age = "";
    String character_sex = "";
    String character_family = "";
    String character_title = "";
    String character_appearence = "";
    String magic_in_memory = "";
    String grimoire = "";
    String inventory = "";
    String campaign = "no campaign";
    int chaos_points = 0;
    int balance_points = 0;
    int law_points=0;
    //ArrayList<String> melee_weapons, ranged_weapons;
    ArrayList<Integer> skills, stats;



    public Sheet(){
        skills = new ArrayList<>();
        stats = new ArrayList<>(7);
        for(int i = 0; i<7; i++){
            stats.add(10);
        }
        image = null;
    }

    public Sheet(Sheet that){
        if(that.image != null){
            image = that.image.copy(that.image.getConfig(), true);
        }else {
            image = null;
        }
        data_key = new String(that.data_key);
        owner = new String(that.owner);
        portrait = that.portrait;
        character_notes = new String(that.character_notes);
        character_background = new String(that.character_background);
        character_name = new String(that.character_name);
        character_birthplace = new String(that.character_birthplace);
        character_age = new String(that.character_age);
        character_sex = new String(that.character_sex);
        character_family = new String(that.character_family);
        character_title = new String(that.character_title);
        character_appearence = new String(that.character_appearence);
        magic_in_memory = new String(that.magic_in_memory);
        grimoire = new String(that.grimoire);
        inventory = new String(that.inventory);
        campaign = new String(that.campaign);
        chaos_points = that.chaos_points;
        balance_points = that.balance_points;
        law_points = that.law_points;
        skills = new ArrayList<>(that.skills);
        stats = new ArrayList<>(that.stats);
    }







    //Getters for Firebase

    public String getData_key() {
        return data_key;
    }

    public String getOwner() {
        return owner;
    }

    public boolean getPortrait() {
        return portrait;
    }

    public String getCharacter_notes() {
        return character_notes;
    }

    public String getCharacter_background() {
        return character_background;
    }

    public String getCharacter_name() {
        return character_name;
    }

    public String getCharacter_birthplace() {
        return character_birthplace;
    }

    public String getCharacter_age() {
        return character_age;
    }

    public String getCharacter_sex() {
        return character_sex;
    }

    public String getCharacter_family() {
        return character_family;
    }

    public String getCharacter_title() {
        return character_title;
    }

    public String getCharacter_appearence() {
        return character_appearence;
    }

    public String getMagic_in_memory() {
        return magic_in_memory;
    }

    public String getGrimoire() {
        return grimoire;
    }

    public String getInventory() {
        return inventory;
    }

    public String getCampaign() {
        return campaign;
    }

    public int getChaos_points() {
        return chaos_points;
    }

    public int getBalance_points() {
        return balance_points;
    }

    public int getLaw_points() {
        return law_points;
    }

    public ArrayList<Integer> getSkills() {
        return skills;
    }

    public ArrayList<Integer> getStats() {
        return stats;
    }

}
