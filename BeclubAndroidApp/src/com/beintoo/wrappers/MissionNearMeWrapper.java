package com.beintoo.wrappers;

import com.google.gson.Gson;

/**
 * Created by gbider on ic_image_placeholder_b/05/14.
 */
public class MissionNearMeWrapper {

    private BuyWrapper rewards;
    private String gotoview;
    private boolean insideplace;
    private MissionWrapper missions;
    private String nextcall;

    public BuyWrapper getRewards() {
        return rewards;
    }

    public void setRewards(BuyWrapper rewards) {
        this.rewards = rewards;
    }

    public MissionWrapper getMissions() {
        return missions;
    }

    public void setMissions(MissionWrapper missions) {
        this.missions = missions;
    }

    public String getNextcall() {
        return nextcall;
    }

    public void setNextcall(String nextcall) {
        this.nextcall = nextcall;
    }

    public String getGotoview() {
        return gotoview;
    }

    public void setGotoview(String gotoview) {
        this.gotoview = gotoview;
    }

    public boolean isInsideplace() {
        return insideplace;
    }

    public void setInsideplace(boolean insideplace) {
        this.insideplace = insideplace;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
