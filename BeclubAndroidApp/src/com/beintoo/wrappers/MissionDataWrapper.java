package com.beintoo.wrappers;


public class MissionDataWrapper {

    public enum MissionType{
        INAPP, SPONSORED;

        public String toString(){
            if(this == INAPP)
                return "INAPP";
            else if(this == SPONSORED)
                return "SPONSORED";
            else return null;
        }
    }

    private String id;
    private String brandsid;
    private String name;
    private String description;
    private String imagesmall;
    private String imagebig;
    private String meta;
    private Double latitude;
    private Double longitude;
    private String missionsid;
    private String placesid;
    private Boolean isactive;
    private MissionType type;
    private ActionCheckWrapper actioncheck;
    private RewardWrapper reward;
    private Double cappingminutes;
    private Double bedollars;
    // CARDSPRING
    private String headline1;
    private String headline2;
    private String bedollarsHeadline1;
    private String disclaimer;
    private String schedule;
    private String closuredate;
    private String tos;

    public String getTos() {
        return tos;
    }

    public void setTos(String tos) {
        this.tos = tos;
    }

    public String getClosuredate() {
        return closuredate;
    }

    public void setClosuredate(String closuredate) {
        this.closuredate = closuredate;
    }

    public String getHeadline1() {
        return headline1;
    }

    public void setHeadline1(String headline1) {
        this.headline1 = headline1;
    }

    public String getHeadline2() {
        return headline2;
    }

    public void setHeadline2(String headline2) {
        this.headline2 = headline2;
    }

    public String getBedollarsHeadline1() {
        return bedollarsHeadline1;
    }

    public void setBedollarsHeadline1(String bedollarsHeadline1) {
        this.bedollarsHeadline1 = bedollarsHeadline1;
    }

    public String getDisclaimer() {
        return disclaimer;
    }

    public void setDisclaimer(String disclaimer) {
        this.disclaimer = disclaimer;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBrandsid() {
        return brandsid;
    }

    public void setBrandsid(String brandsid) {
        this.brandsid = brandsid;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getMissionsid() {
        return missionsid;
    }

    public void setMissionsid(String missionsid) {
        this.missionsid = missionsid;
    }

    public String getPlacesid() {
        return placesid;
    }

    public void setPlacesid(String placesid) {
        this.placesid = placesid;
    }

    public Boolean getActive() {
        return isactive;
    }

    public void setActive(Boolean active) {
        isactive = active;
    }

    public ActionCheckWrapper getActioncheck() {
        return actioncheck;
    }

    public void setActioncheck(ActionCheckWrapper actioncheck) {
        this.actioncheck = actioncheck;
    }

    public RewardWrapper getReward() {
        return reward;
    }

    public void setReward(RewardWrapper reward) {
        this.reward = reward;
    }

    public Double getBedollars() {
        return bedollars;
    }

    public void setBedollars(Double bedollars) {
        this.bedollars = bedollars;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImagesmall() {
        return imagesmall;
    }

    public void setImagesmall(String imagesmall) {
        this.imagesmall = imagesmall;
    }

    public String getImagebig() {
        return imagebig;
    }

    public void setImagebig(String imagebig) {
        this.imagebig = imagebig;
    }

    public String getMeta() {
        return meta;
    }

    public void setMeta(String meta) {
        this.meta = meta;
    }

    public Double getCappinghours() {
        return cappingminutes;
    }

    public void setCappinghours(Double cappinghours) {
        this.cappingminutes = cappinghours;
    }

    public Boolean getIsactive() {
        return isactive;
    }

    public void setIsactive(Boolean isactive) {
        this.isactive = isactive;
    }

    public MissionType getType() {
        return type;
    }

    public void setType(MissionType type) {
        this.type = type;
    }

    public Double getCappingminutes() {
        return cappingminutes;
    }

    public void setCappingminutes(Double cappingminutes) {
        this.cappingminutes = cappingminutes;
    }
}
