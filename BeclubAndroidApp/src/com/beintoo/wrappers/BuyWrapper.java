package com.beintoo.wrappers;


import com.google.gson.Gson;

public class  BuyWrapper {
    private String membersrewardsid;
    private RewardWrapper reward;
    private CodeWrapper code;
    private Double memberbalance;
    private String status;
    public boolean emptyRow; // hack for internal use when the wallet is empty

    public String getMembersRewardsId() {
        return membersrewardsid;
    }

    public void setMembersRewardsId(String id) {
        this.membersrewardsid = id;
    }

    public RewardWrapper getReward() {
        return reward;
    }

    public void setReward(RewardWrapper reward) {
        this.reward = reward;
    }

    public CodeWrapper getCode() {
        return code;
    }

    public void setCode(CodeWrapper code) {
        this.code = code;
    }

    public Double getMemberbalance() {
        return memberbalance;
    }

    public void setMemberbalance(Double memberbalance) {
        this.memberbalance = memberbalance;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
