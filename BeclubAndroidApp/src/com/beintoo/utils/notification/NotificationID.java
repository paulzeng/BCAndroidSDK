package com.beintoo.utils.notification;

public enum NotificationID {

    WAKE_UP_GIMBAL_SDK(899),
    PUSH_NOTIFICATION(900),
    NEW_FEATURES_UPDATE_APP(901),
    DONT_FORGET_BRAND_MISSION_14_DAYS(902),
    DONT_FORGET_BRAND_MISSION_28_DAYS(903),
    PURCHASE_REGISTERED_WHEN_PURCHASE_COMPLETE(905),
    BEDOLLARS_EARNED_WHEN_PENDING_PERIOD_PASSES(906),
    MISSION_NEARBY(907);

    private int mId;

    private NotificationID(int id) {
        this.mId = id;
    }

    public int getId() {
        return mId;
    }
}
