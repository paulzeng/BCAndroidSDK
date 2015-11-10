package com.beintoo.wrappers;

import android.content.Context;
import android.content.pm.PackageManager;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class EarnBedollarsWrapper {

    public enum MissionActionTypeEnum {
        // in app mission
        BRANDED, REWARDED, BEPOWERED, PREREGISTRATION, ENTRYBONUS, GIVE_BEDOLLARS,
        // sponsored missions
        WALKIN, WALKIN_TIME, SCAN, MULTIPLESCAN, COLLECTIVE, CARDSPRING, ONLINE, TAKEAPICTURE, COMMISSION_JUNCTION;

        public String toString(){
            if(this == WALKIN)
                return "WALKIN";
            if(this == WALKIN_TIME)
                return "WALKIN_TIME";
            else if(this == SCAN)
                return "SCAN";
            else if(this == ONLINE)
                return "ONLINE";
            else if(this == MULTIPLESCAN)
                return "MULTIPLESCAN";
            else if(this == COLLECTIVE)
                return "COLLECTIVE";
            else if(this == ENTRYBONUS)
                return "ENTRYBONUS";
            else if(this == REWARDED)
                return "REWARDED";
            else if(this == GIVE_BEDOLLARS)
                return "GIVE_BEDOLLARS";
            else if(this == PREREGISTRATION)
                return "PREREGISTRATION";
            else if(this == CARDSPRING)
                return "CARDSPRING";
            else if(this == BEPOWERED)
                return "BEPOWERED";
            else if(this == COMMISSION_JUNCTION)
                return "COMMISSION_JUNCTION";
            else if(this == TAKEAPICTURE)
                return "TAKEAPICTURE";
            else
                return null;
        }
    }

    public static List<MissionActionTypeEnum> getBalanceTypes() {
        ArrayList<MissionActionTypeEnum> balanceTypes = new ArrayList<MissionActionTypeEnum>();

        balanceTypes.add(MissionActionTypeEnum.BRANDED);
        balanceTypes.add(MissionActionTypeEnum.REWARDED);
        balanceTypes.add(MissionActionTypeEnum.BEPOWERED);
        balanceTypes.add(MissionActionTypeEnum.PREREGISTRATION);
        balanceTypes.add(MissionActionTypeEnum.ENTRYBONUS);
        balanceTypes.add(MissionActionTypeEnum.GIVE_BEDOLLARS);

        return balanceTypes;
    }

    public static List<String> getSubTypesAvailable(Context context) {
        ArrayList<String> available = new ArrayList<String>();

        available.add(MissionActionTypeEnum.WALKIN.toString());
        available.add(MissionActionTypeEnum.WALKIN_TIME.toString());

        boolean hasCamera = true;
        try {
            hasCamera = context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(hasCamera) {
            available.add(EarnBedollarsWrapper.MissionActionTypeEnum.SCAN.toString());
            available.add(EarnBedollarsWrapper.MissionActionTypeEnum.MULTIPLESCAN.toString());
        }

        available.add(MissionActionTypeEnum.ONLINE.toString());
        available.add(MissionActionTypeEnum.COLLECTIVE.toString());
        available.add(MissionActionTypeEnum.CARDSPRING.toString());
        available.add(MissionActionTypeEnum.TAKEAPICTURE.toString());

        return available;
    }

    BrandWrapper brands;
    List<MissionActionTypeEnum> subtype;

    public BrandWrapper getBrands() {
        return brands;
    }

    public void setBrands(BrandWrapper brands) {
        this.brands = brands;
    }

    public List<MissionActionTypeEnum> getSubtype() {
        return subtype;
    }

    public void setSubtype(List<MissionActionTypeEnum> subtype) {
        this.subtype = subtype;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
