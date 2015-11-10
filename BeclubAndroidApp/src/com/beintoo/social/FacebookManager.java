//package com.beintoo.social;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.location.Location;
//import android.os.Bundle;
//import android.support.v4.app.FragmentActivity;
//import android.widget.Toast;
//
//import com.beintoo.activities.MemberEntryActivity;
//import com.beintoo.activities.MemberSignupActivity;
//import com.beintoo.activities.PostSignupActivity;
//import com.beintoo.asynctasks.AsyncTasks;
//import com.beintoo.asynctasks.FacebookConnectTask;
//import com.beintoo.beclubapp.BeClub;
//import com.beintoo.beclubapp.BeclubActivityChooser;
//import com.beintoo.beclubapp.MainActivity;
//import com.beintoo.beclubapp.R;
//import com.beintoo.dialogs.DeviceBannedDialog;
//import com.beintoo.utils.Analytics;
//import com.beintoo.utils.BeLocationManager;
//import com.beintoo.utils.DataStore;
//import com.beintoo.utils.DebugUtility;
//import com.beintoo.utils.DeviceId;
//import com.beintoo.wrappers.MemberBean;
//import com.beintoo.wrappers.MemberSignupBeClubBean;
//import com.facebook.FacebookException;
//import com.facebook.FacebookOperationCanceledException;
//import com.facebook.HttpMethod;
//import com.facebook.Request;
//import com.facebook.Response;
//import com.facebook.Session;
//import com.facebook.SessionState;
//import com.facebook.UiLifecycleHelper;
//import com.facebook.model.GraphObject;
//import com.facebook.widget.FacebookDialog;
//import com.facebook.widget.WebDialog;
//import com.google.gson.Gson;
//import com.mobileapptracker.MobileAppTracker;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Collection;
//import java.util.Date;
//import java.util.List;
//
//
//public class FacebookManager {
//
//    private UiLifecycleHelper mUiHelper;
//    private static FacebookManager instance;
//    private FragmentActivity mActivity;
//
//    public interface OnFacebookListener {
//        public void onPhotoPosted(String postID, String imageID);
//    }
//
//    public static FacebookManager getIstance() {
//        if (instance == null)
//            synchronized (BeClub.class) {
//                if (instance == null)
//                    instance = new FacebookManager();
//            }
//        return instance;
//    }
//
//    public static void facebookLoginWithoutBeClubRegistration(final FragmentActivity activity) {
//        final List<String> permissions = new ArrayList<String>();
//        permissions.add("email");
//        permissions.add("user_birthday");
//        permissions.add("user_location");
//
//        Session.openActiveSession(activity, true, new Session.StatusCallback() {
//            @Override
//            public void call(Session session, SessionState sessionState, Exception e) {
//                if (session.isOpened()) {
//                    List<String> oldPermissions = session.getPermissions();
//                    DebugUtility.showLog("OLD PERMISSIONS " + new Gson().toJson(oldPermissions));
//                    DebugUtility.showLog("NEW PERMISSIONS " + new Gson().toJson(permissions));
//                    if (!isSubsetOf(permissions, oldPermissions)) {
//                        DebugUtility.showLog("GETTING NEW PERMISSIONS");
//                        Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(activity, permissions);
//                        session.requestNewReadPermissions(newPermissionsRequest);
//                    } else {
//                        Session.setActiveSession(session);
//                    }
//                }
//            }
//        });
//    }
//
//    public static void facebookLogin(final FragmentActivity activity, final List<String> permissions, final boolean skipConnect) throws Exception {
//        facebookLogout(activity);
//        Session.openActiveSession(activity, true, new Session.StatusCallback() {
//            @Override
//            public void call(Session session, SessionState state, Exception exception) {
//                if (session.isOpened()) {
//                    final MemberBean member = new MemberBean();
//                    member.setFbtoken(session.getAccessToken());
//                    DebugUtility.showLog("is opnene " + session.isOpened());
//                    DebugUtility.showLog("state ope " + state.isOpened());
//                    DebugUtility.showLog("token " + session.getAccessToken());
//
//
//                    session.addCallback(new Session.StatusCallback() {
//                        public void call(final Session session, SessionState state, Exception exception) {
//                            // If there is an exception...
//                            if (exception != null) {
//                                // Handle fail case here.
//                                DebugUtility.showLog("STATE --->" + state);
//                                return;
//                            }
//
//                            // If token is just updated...
//                            if (state == SessionState.OPENED_TOKEN_UPDATED) {
//                                new FacebookConnectTask(activity, buildMemberSignupBean(activity, member), new FacebookConnectTask.OnFacebookLoginListener() {
//                                    @Override
//                                    public void onFacebookLoginNewMember(String email) {
//                                        Intent intent = new Intent(activity, PostSignupActivity.class);
//                                        intent.putExtra("email", email);
//                                        activity.startActivity(intent);
//                                        activity.finish();
//                                        if (BeclubActivityChooser.memberEntryActivity != null) {
//                                            BeclubActivityChooser.memberEntryActivity.finish();
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onFacebookLoginSucceed() {
//                                        DebugUtility.showLog("FB CONNECT OK");
//
//                                        if (activity instanceof MemberEntryActivity) { // i'm on the first activity just close it
//                                            activity.finish();
//                                        } else if (BeclubActivityChooser.memberEntryActivity != null && (activity instanceof MemberSignupActivity)) { // i'm on the signup
//                                            activity.finish();
//                                            BeclubActivityChooser.memberEntryActivity.finish();
//                                        }
//                                        activity.startActivity(new Intent(activity, MainActivity.class));
//                                    }
//
//                                    @Override
//                                    public void onFacebookLoginMissingInfo(MemberBean result) {
//                                        DebugUtility.showLog("FB CONNECT MISSING PARAMS");
//
//                                        Intent intent = new Intent(activity, MemberSignupActivity.class);
//                                        intent.putExtra("member", result.toString());
//                                        activity.startActivity(intent);
//                                    }
//
//                                    @Override
//                                    public void onFacebookLoginFailed(AsyncTasks.Result statusResult) {
//                                        switch (statusResult) {
//                                            case BANNED:
//                                                new DeviceBannedDialog(activity, false).show(activity.getSupportFragmentManager(), "banned-device");
//                                                break;
//                                            case NETWORK_ERROR:
//                                                Toast.makeText(activity, activity.getString(R.string.connection_error), Toast.LENGTH_LONG).show();
//                                                break;
//                                            default:
//                                                Toast.makeText(activity, "Unable to login with Facebook", Toast.LENGTH_LONG).show();
//                                        }
//                                    }
//                                }).execute();
//                                // Handle success case here.
//                                return;
//                            }
//                        }
//                    });
//
//                    List<String> oldPermissions = session.getPermissions();
//                    DebugUtility.showLog("OLD PERMISSIONS " + new Gson().toJson(oldPermissions));
//                    DebugUtility.showLog("NEW PERMISSIONS " + new Gson().toJson(permissions));
//                    if (!isSubsetOf(permissions, oldPermissions)) {
//                        DebugUtility.showLog("GETTING NEW PERMISSIONS");
//                        Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(activity, permissions);
//                        session.requestNewReadPermissions(newPermissionsRequest);
//                    } else {
//                        if (!skipConnect)
//                            new FacebookConnectTask(activity, buildMemberSignupBean(activity, member), new FacebookConnectTask.OnFacebookLoginListener() {
//                                @Override
//                                public void onFacebookLoginNewMember(String email) {
//                                    Intent intent = new Intent(activity, PostSignupActivity.class);
//                                    intent.putExtra("email", email);
//                                    activity.startActivity(intent);
//                                    activity.finish();
//                                    if (BeclubActivityChooser.memberEntryActivity != null) {
//                                        BeclubActivityChooser.memberEntryActivity.finish();
//                                    }
//                                }
//
//                                @Override
//                                public void onFacebookLoginSucceed() {
//                                    DebugUtility.showLog("FB CONNECT OK");
//
//                                    if (activity instanceof MemberEntryActivity) { // i'm on the first activity just close it
//                                        activity.finish();
//                                    } else if (BeclubActivityChooser.memberEntryActivity != null && (activity instanceof MemberSignupActivity)) { // i'm on the signup
//                                        activity.finish();
//                                        BeclubActivityChooser.memberEntryActivity.finish();
//                                    }
//                                    activity.startActivity(new Intent(activity, MainActivity.class));
//                                }
//
//                                @Override
//                                public void onFacebookLoginMissingInfo(MemberBean result) {
//                                    DebugUtility.showLog("FB CONNECT MISSING PARAMS");
//
//                                    Intent intent = new Intent(activity, MemberSignupActivity.class);
//                                    intent.putExtra("member", result.toString());
//                                    activity.startActivity(intent);
//                                }
//
//                                @Override
//                                public void onFacebookLoginFailed(AsyncTasks.Result statusResult) {
//                                    switch (statusResult) {
//                                        case BANNED:
//                                            new DeviceBannedDialog(activity, false).show(activity.getSupportFragmentManager(), "banned-device");
//                                            break;
//                                        case NETWORK_ERROR:
//                                            Toast.makeText(activity, activity.getString(R.string.connection_error), Toast.LENGTH_LONG).show();
//                                            break;
//                                        default:
//                                            Toast.makeText(activity, "Unable to login with Facebook", Toast.LENGTH_LONG).show();
//                                    }
//                                }
//                            }).execute();
//                    }
//
//                    Session.setActiveSession(session);
//                }
//            }
//        });
//    }
//
//    private static MemberBean buildMemberSignupBean(Context context, MemberBean memberBean) {
//        memberBean.setAndroidid(DeviceId.getAndroidId(context));
//        memberBean.setImei(DeviceId.getImei(context));
//        memberBean.setMacaddress(DeviceId.getMACAddress(context));
//        memberBean.setLocale(DeviceId.getLocale().getDisplayLanguage());
//
//        Location location = BeLocationManager.getInstance(context).getLastKnowLocation(context);
//        Double latitude = null, longitude = null;
//        Double altitude = null;
//        Float haccuracy = null;
//        if (location != null) {
//            latitude = location.getLatitude();
//            longitude = location.getLongitude();
//            altitude = location.getAltitude();
//            haccuracy = location.getAccuracy();
//        }
//
//        if (latitude != null && longitude != null) {
//            memberBean.setLatitude(latitude);
//            memberBean.setLongitude(longitude);
//            memberBean.setAltitude(altitude);
//            memberBean.setHaccuracy(haccuracy);
//        }
//
//        memberBean.setMatid(MobileAppTracker.getInstance().getMatId());
//        memberBean.setImagebig(null);
//        memberBean.setImagesmall(null);
//
//        return memberBean;
//    }
//
//
//    public void postOnFacebook(final FragmentActivity activity, Bundle savedInstanceState, final String name, final String caption, final String description, final String link, final String picture) {
//        mActivity = activity;
//        mUiHelper = new UiLifecycleHelper(activity, new Session.StatusCallback() {
//            @Override
//            public void call(Session session, SessionState sessionState, Exception e) {
//
//            }
//        });
//        //mUiHelper.onSaveInstanceState(savedInstanceState);
//
//        if (FacebookDialog.canPresentShareDialog(activity, FacebookDialog.ShareDialogFeature.SHARE_DIALOG)) {
//
//            FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(activity)
//                    .setName(name).setCaption(caption).setDescription(description)
//                    .setLink(link).setPicture(picture)
//                    .setFriends(Arrays.asList(new String[]{"559656249"}))
//                    .build();
//            mUiHelper.trackPendingDialogCall(shareDialog.present());
//        }
//    }
//
//    public void facebookUiHelperActivityResultHandler(int requestCode, int resultCode, Intent data) {
//        try {
//            mUiHelper.onActivityResult(requestCode, resultCode, data, new FacebookDialog.Callback() {
//                @Override
//                public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
//                    DebugUtility.showLog("FB POST ERROR");
//                }
//
//                @Override
//                public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
//                    if (mActivity != null)
//                        Toast.makeText(mActivity, mActivity.getString(R.string.social_toast_shared), Toast.LENGTH_SHORT).show();
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    /*
//    public void postOnFacebookApi(final String name, final String caption, final String description, final String link, final String picture){
//        this.postOnFacebookApi(null, name, caption, description, link, picture);
//    }
//    */
//
//    public void postOnFacebookApi(final Activity activity, final String userId, final String missionId, final String name, final String caption, final String description, final String link, final String picture) {
//        Session session = Session.getActiveSession();
//        try {
//            if (session != null && session.isOpened()) {
//                Bundle params = new Bundle();
//                params.putString("name", name);
//                params.putString("caption", caption);
//                params.putString("description", description);
//                params.putString("link", link);
//                params.putString("picture", picture);
//                params.putString("to", userId);
//
//                WebDialog feedDialog = (new WebDialog.FeedDialogBuilder(activity, session, params)).setOnCompleteListener(new WebDialog.OnCompleteListener() {
//                    @Override
//                    public void onComplete(Bundle bundle, FacebookException e) {
//                        if ((e != null && e instanceof FacebookOperationCanceledException) || (bundle != null && bundle.getString("post_id") == null)) {
//                            Analytics.trackEvent(activity, "EARN-BEDOLLARS-COLLECTIVE-FRIENDS-PICKER-FACEBOOK", "action", "CollectiveMissionInviteFriendsPickerSendInvitationCancelTappedFB", 1L);
//                            return;
//                        }
//                        Toast.makeText(activity, activity.getString(R.string.collective_mission_activity_invite_sent), Toast.LENGTH_SHORT).show();
//                        Analytics.trackEvent(activity, "EARN-BEDOLLARS-COLLECTIVE-FRIENDS-PICKER-FACEBOOK", "action", "CollectiveMissionInviteFriendsPickerSendInvitationSendTappedFB", 1L);
//
//                        // add the invited user to the list
//                        List<String> invitedFriends = DataStore.getStringList(activity, "collective_invited_friends_" + missionId);
//                        invitedFriends.add(userId);
//                        DataStore.saveStringList(activity, "collective_invited_friends_" + missionId, invitedFriends);
//                    }
//                }).build();
//                feedDialog.show();
//            } else {
//                Session.openActiveSession(mActivity, true, new Session.StatusCallback() {
//                    @Override
//                    public void call(Session session, SessionState sessionState, Exception e) {
//                        if (session.isOpened()) {
//                            postOnFacebookApi(activity, userId, missionId, name, caption, description, link, picture);
//                        }
//                        DebugUtility.showLog("SESSION STATE " + sessionState);
//                    }
//                });
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            DebugUtility.showLog("Error posting on Facebook!");
//        }
//    }
//
//
//    public void shareOnFriendFeedWithWebDialog(final String userId, final String name, final String caption, final String description, final String link, final String picture) {
//        Bundle params = new Bundle();
//        params.putString("name", "Facebook SDK for Android");
//        params.putString("caption", "Build great social apps and get more installs.");
//        params.putString("description", "The Facebook SDK for Android makes it easier and faster to develop Facebook integrated Android apps.");
//        params.putString("link", "https://developers.facebook.com/android");
//        params.putString("picture", "https://raw.github.com/fbsamples/ios-3.x-howtos/master/Images/iossdk_logo.png");
//        params.putString("message", "https://raw.github.com/fbsamples/ios-3.x-howtos/master/Images/iossdk_logo.png");
//        params.putString("to", userId);
//
//        WebDialog feedDialog = (new WebDialog.FeedDialogBuilder(mActivity, Session.getActiveSession(), params))
//                .setOnCompleteListener(new WebDialog.OnCompleteListener() {
//                    @Override
//                    public void onComplete(Bundle values, FacebookException error) {
//                        if (error == null) {
//                            // When the story is posted, echo the success
//                            // and the post Id.
//                            final String postId = values.getString("post_id");
//                            if (postId != null) {
//                                Toast.makeText(mActivity,
//                                        "Posted story, id: " + postId,
//                                        Toast.LENGTH_SHORT).show();
//                            } else {
//                                // User clicked the Cancel button
//                                Toast.makeText(mActivity.getApplicationContext(),
//                                        "Publish cancelled",
//                                        Toast.LENGTH_SHORT).show();
//                            }
//                        } else if (error instanceof FacebookOperationCanceledException) {
//                            // User clicked the "x" button
//                            Toast.makeText(mActivity.getApplicationContext(),
//                                    "Publish cancelled",
//                                    Toast.LENGTH_SHORT).show();
//                        } else {
//                            // Generic, ex: network error
//                            Toast.makeText(mActivity.getApplicationContext(),
//                                    "Error posting story",
//                                    Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//                })
//                .build();
//        feedDialog.show();
//    }
//
//    public void checkSessionAndShare(Activity activity, final String userId, final String name, final String caption, final String description, final String link, final String picture) {
//        mActivity = (FragmentActivity) activity;
//        Session session = Session.getActiveSession();
//        try {
//            if (session != null && session.isOpened()) {
//                // Check for publish permissions
//                List<String> permissions = session.getPermissions();
//                List<String> newPermissions = Arrays.asList("publish_actions");
//                if (!isSubsetOf(newPermissions, permissions)) {
//                    //pendingPublishReauthorization = true;
//                    Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(mActivity, newPermissions);
//                    session.requestNewPublishPermissions(newPermissionsRequest);
//                    return;
//                }
//
//                shareOnFriendFeedWithWebDialog(userId, name, caption, description, link, picture);
//            } else {
//                Session.openActiveSession(mActivity, true, new Session.StatusCallback() {
//                    @Override
//                    public void call(Session session, SessionState sessionState, Exception e) {
//                        if (session.isOpened()) {
//                            shareOnFriendFeedWithWebDialog(userId, name, caption, description, link, picture);
//                        }
//                        DebugUtility.showLog("SESSION STATE " + sessionState);
//                    }
//                });
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static void getFriends(final Activity activity, final Request.GraphUserListCallback callback) {
//        Session session = Session.getActiveSession();
//        try {
//            if (session != null && session.isOpened()) {
//
//                // Check for publish permissions
//                List<String> permissions = session.getPermissions();
//                List<String> newPermissions = Arrays.asList("read_friendlists");
//                if (!isSubsetOf(newPermissions, permissions)) {
//                    //pendingPublishReauthorization = true;
//                    Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(activity, newPermissions);
//                    session.requestNewPublishPermissions(newPermissionsRequest);
//                }
//
//
//                DebugUtility.showLog("SESSION " + session);
//
//                Request friendRequest = Request.newMyFriendsRequest(session, callback);
//                Bundle params = new Bundle();
//                params.putString("fields", "id, name, picture");
//                friendRequest.setParameters(params);
//                friendRequest.executeAsync();
//            } else {
//                Session.openActiveSession(activity, true, new Session.StatusCallback() {
//                    @Override
//                    public void call(Session session, SessionState sessionState, Exception e) {
//                        if (session.isOpened()) {
//                            getFriends(activity, callback);
//                        }
//                        DebugUtility.showLog("SESSION STATE " + sessionState);
//                    }
//                });
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * Private methods
//     */
//
//    public static void facebookLogout(Context context) {
//        Session session = Session.getActiveSession();
//        if (session != null) {
//            if (!session.isClosed()) {
//                session.closeAndClearTokenInformation();
//            }
//        } else {
//
//            session = new Session(context);
//            Session.setActiveSession(session);
//
//            session.closeAndClearTokenInformation();
//        }
//
//    }
//
//    private static boolean isSubsetOf(Collection<String> subset, Collection<String> superset) {
//        for (String string : subset) {
//            if (!superset.contains(string)) {
//                return false;
//            }
//        }
//        return true;
//    }
//
//    public static void publishPhoto(final FragmentActivity context, final String message, final Bitmap photo, final OnFacebookListener listener) {
//        Session.openActiveSession(context, true, new Session.StatusCallback() {
//            @Override
//            public void call(Session session, SessionState sessionState, Exception e) {
//                if (!session.getPermissions().contains("publish_stream")) {
//                    ArrayList<String> permissions = new ArrayList<String>();
//                    permissions.add("publish_stream");
//
//                    Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(context, permissions);
//                    Session.getActiveSession().requestNewPublishPermissions(newPermissionsRequest);
//                    return;
//                } else {
//                    Session.getActiveSession().removeCallback(this);
//                    Bundle param = new Bundle();
//
//                    param.putString("name", message);
//                    param.putParcelable("picture", photo);
//
//                    new Request(Session.getActiveSession(), "me/photos", param, HttpMethod.POST, new Request.Callback() {
//                        @Override
//                        public void onCompleted(Response response) {
//                            GraphObject graphObject = response.getGraphObject();
//                            if (graphObject != null) {
//                                JSONObject graphResponse = graphObject.getInnerJSONObject();
//
//                                String postId = null;
//                                String imageId = null;
//                                try {
//                                    imageId = graphResponse.getString("id");
//                                    postId = graphResponse.getString("post_id");
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//
//                                if (listener != null) {
//                                    listener.onPhotoPosted(postId, imageId);
//                                }
//                            }
//                        }
//                    }).executeAsync();
//                }
//
//
//            }
//        });
//    }
// }
