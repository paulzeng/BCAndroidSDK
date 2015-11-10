package com.beintoo.wrappers;

import com.google.gson.Gson;

public class MissionWrapper {

    private MissionDataWrapper mission;
    private String placesid;
    private Boolean isActive;
    private Integer minutestoredo = 0;
    private boolean isEnabled = true; // android internal use
    private boolean isChecked = true; //  android internal use
    private TimingWrapper timing;
    private RewardWrapper reward;
    private String brandname;
    private PaginatedList<OnlineProductBean> items;

    public PaginatedList<OnlineProductBean> getItems() {
        return items;
    }

    public void setItems(PaginatedList<OnlineProductBean> items) {
        this.items = items;
    }

    public String getBrandname() {
        return brandname;
    }

    public void setBrandname(String brandname) {
        this.brandname = brandname;
    }

    public MissionDataWrapper getMission() {
        return mission;
    }

    public void setMission(MissionDataWrapper mission) {
        this.mission = mission;
    }

    public String getPlacesid() {
        return placesid;
    }

    public void setPlacesid(String placesid) {
        this.placesid = placesid;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Integer getMinutestoredo() {
        return minutestoredo;
    }

    public void setMinutestoredo(Integer minutestoredo) {
        this.minutestoredo = minutestoredo;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public TimingWrapper getTiming() {
        return timing;
    }

    public void setTiming(TimingWrapper timing) {
        this.timing = timing;
    }

    public RewardWrapper getReward() {
        return reward;
    }

    public void setReward(RewardWrapper reward) {
        this.reward = reward;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
