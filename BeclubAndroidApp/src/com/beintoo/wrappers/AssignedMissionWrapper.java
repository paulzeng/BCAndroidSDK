package com.beintoo.wrappers;

import com.google.gson.Gson;

import java.util.List;

public class AssignedMissionWrapper extends ResponseWrapper {
    private String id;
    private String bememberid;
    private String creationdate;
    private transient String missionsid;
    private Double bedollars;
    private String appsid;
    private String imagebig;
    private String imagesmall;
    private String description;
    private String rewardsid;
    private RewardWrapper rewards;
    private BrandWrapper brand;
    //private EarnBedollarsWrapper.MissionActionTypeEnum type;
    private EarnBedollarsWrapper.MissionActionTypeEnum subtype;
    private String debuginfo;
    private String expiredate;
    private BuyWrapper membersrewards;
    private String membersrewardsid;

    // collective mission
    private MissionWrapper missionswrapper;
    private Integer alreadyregistered = 0;
    private Integer partecipants = 0;
    private String maturl;
    private boolean assigned;
    private String sharedsentence;

    //picture mission
    private PictureAssignedMission assignedMissions;
    private String pictureurl;
    private Boolean winner;
    private Integer position;
    private List<String> winnersPictures;

    public Boolean getWinner() {
        return winner;
    }

    public void setWinner(Boolean winner) {
        this.winner = winner;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public List<String> getWinnersPictures() {
        return winnersPictures;
    }

    public void setWinnersPictures(List<String> winnersPictures) {
        this.winnersPictures = winnersPictures;
    }

    public PictureAssignedMission getAssignedMissions() {
        return assignedMissions;
    }

    public void setAssignedMissions(PictureAssignedMission assignedMissions) {
        this.assignedMissions = assignedMissions;
    }

    public String getPictureurl() {
        return pictureurl;
    }

    public void setPictureurl(String pictureurl) {
        this.pictureurl = pictureurl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBememberid() {
        return bememberid;
    }

    public void setBememberid(String bememberid) {
        this.bememberid = bememberid;
    }

    public String getCreationdate() {
        return creationdate;
    }

    public void setCreationdate(String creationdate) {
        this.creationdate = creationdate;
    }

    public String getMissionsid() {
        return missionsid;
    }

    public void setMissionsid(String missionsid) {
        this.missionsid = missionsid;
    }

    public Double getBedollars() {
        return bedollars;
    }

    public void setBedollars(Double bedollars) {
        this.bedollars = bedollars;
    }

    public String getAppsid() {
        return appsid;
    }

    public void setAppsid(String appsid) {
        this.appsid = appsid;
    }

    public String getImagebig() {
        return imagebig;
    }

    public void setImagebig(String imagebig) {
        this.imagebig = imagebig;
    }

    public String getImagesmall() {
        return imagesmall;
    }

    public void setImagesmall(String imagesmall) {
        this.imagesmall = imagesmall;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRewardsid() {
        return rewardsid;
    }

    public void setRewardsid(String rewardsid) {
        this.rewardsid = rewardsid;
    }

    public EarnBedollarsWrapper.MissionActionTypeEnum getSubtype() {
        return subtype;
    }

    public void setSubtype(EarnBedollarsWrapper.MissionActionTypeEnum subtype) {
        this.subtype = subtype;
    }

    public String getDebuginfo() {
        return debuginfo;
    }

    public void setDebuginfo(String debuginfo) {
        this.debuginfo = debuginfo;
    }

    public String getExpiredate() {
        return expiredate;
    }

    public void setExpiredate(String expiredate) {
        this.expiredate = expiredate;
    }

    public RewardWrapper getReward() {
        return rewards;
    }

    public void setReward(RewardWrapper reward) {
        this.rewards = reward;
    }

    public BuyWrapper getMembersrewards() {
        return membersrewards;
    }

    public void setMembersrewards(BuyWrapper membersrewards) {
        this.membersrewards = membersrewards;
    }

    public String getMembersrewardsid() {
        return membersrewardsid;
    }

    public void setMembersrewardsid(String membersrewardsid) {
        this.membersrewardsid = membersrewardsid;
    }

    public RewardWrapper getRewards() {
        return rewards;
    }

    public void setRewards(RewardWrapper rewards) {
        this.rewards = rewards;
    }

    public MissionWrapper getMissionswrapper() {
        return missionswrapper;
    }

    public void setMissionswrapper(MissionWrapper missionswrapper) {
        this.missionswrapper = missionswrapper;
    }

    public Integer getAlreadyregistered() {
        return alreadyregistered;
    }

    public void setAlreadyregistered(Integer alreadyregistered) {
        this.alreadyregistered = alreadyregistered;
    }

    public Integer getPartecipants() {
        return partecipants;
    }

    public void setPartecipants(Integer partecipants) {
        this.partecipants = partecipants;
    }

    public String getMaturl() {
        return maturl;
    }

    public void setMaturl(String maturl) {
        this.maturl = maturl;
    }

    public BrandWrapper getBrand() {
        return brand;
    }

    public void setBrand(BrandWrapper brand) {
        this.brand = brand;
    }

    public boolean isAssigned() {
        return assigned;
    }

    public void setAssigned(boolean assigned) {
        this.assigned = assigned;
    }

    public String getSharedsentence() {
        return sharedsentence;
    }

    public void setSharedsentence(String sharedsentence) {
        this.sharedsentence = sharedsentence;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
