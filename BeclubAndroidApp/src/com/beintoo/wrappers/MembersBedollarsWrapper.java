package com.beintoo.wrappers;


public class MembersBedollarsWrapper {
    private String memberid;
    private String date;
    private Double value;
    private String sourceappid;
    private String sourcebrandid;
    private String sourcemissionid;
    private String reason;
    private String rewardsid;
    private String transactionid;
    private MissionWrapper mission;
    private BuyWrapper rewards;
    private AppWrapper app;

    public String getMemberid() {
        return memberid;
    }

    public void setMemberid(String memberid) {
        this.memberid = memberid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getSourceappid() {
        return sourceappid;
    }

    public void setSourceappid(String sourceappid) {
        this.sourceappid = sourceappid;
    }

    public String getSourcebrandid() {
        return sourcebrandid;
    }

    public void setSourcebrandid(String sourcebrandid) {
        this.sourcebrandid = sourcebrandid;
    }

    public String getSourcemissionid() {
        return sourcemissionid;
    }

    public void setSourcemissionid(String sourcemissionid) {
        this.sourcemissionid = sourcemissionid;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getRewardsid() {
        return rewardsid;
    }

    public void setRewardsid(String rewardsid) {
        this.rewardsid = rewardsid;
    }

    public String getTransactionid() {
        return transactionid;
    }

    public void setTransactionid(String transactionid) {
        this.transactionid = transactionid;
    }

    public MissionWrapper getMission() {
        return mission;
    }

    public void setMission(MissionWrapper mission) {
        this.mission = mission;
    }

    public BuyWrapper getReward() {
        return rewards;
    }

    public void setReward(BuyWrapper reward) {
        this.rewards = reward;
    }

    public AppWrapper getApp() {
        return app;
    }

    public void setApp(AppWrapper app) {
        this.app = app;
    }
}
