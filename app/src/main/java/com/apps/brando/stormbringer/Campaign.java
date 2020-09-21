package com.apps.brando.stormbringer;

import java.io.Serializable;
import java.util.HashMap;

public class Campaign implements Serializable{

    String dm_id, campaign_id, campaign_name;
    HashMap<String, String> sheets;

    public Campaign(){
        dm_id = "";
        campaign_id = "";
        campaign_name = "";
        sheets = new HashMap<>();
    }

    public String getDm_id() {
        return dm_id;
    }

    public String getCampaign_id() {
        return campaign_id;
    }

    public String getCampaign_name() {
        return campaign_name;
    }

    public HashMap<String, String> getSheets() {
        return sheets;
    }

}
