package com.beintoo.asynctasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.os.AsyncTask;

import com.beintoo.api.AuthResource;
import com.beintoo.api.BeRestAdapter;
import com.beintoo.api.IMemberResource;
import com.beintoo.utils.MemberAuthStore;
import com.beintoo.utils.ui.CircleImageView;
import com.beintoo.utils.ui.ResizeImage;
import com.beintoo.wrappers.ImageUploadWrapper;
import com.beintoo.wrappers.MeBean;
import com.beintoo.wrappers.MemberBean;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

import retrofit.RestAdapter;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

public class SettingsProfilePictureUploadTask extends AsyncTask<File, Void, ImageUploadWrapper> {

    public interface OnProfileUpdateListener {
        public void onProfileUpdate(String image);
        public void onProfileUpdateFailed();
    }

    private Context mContext;
    private CircleImageView mImageView;
    private OnProfileUpdateListener mListener;
    private ProgressDialog mProgressDialog;

    public SettingsProfilePictureUploadTask(Context context, OnProfileUpdateListener listener) {
        mContext = context;
        mListener = listener;
        mProgressDialog = ProgressDialog.show(context, "Update", "Please wait...", true, false);
    }

    @Override
    protected ImageUploadWrapper doInBackground(File... files) {
        try {
            ExifInterface exif = new ExifInterface(files[0].getAbsolutePath());
            int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int rotationInDegrees = ResizeImage.exifToDegrees(rotation);
            Bitmap resized = ResizeImage.decodeFile(files[0], 512, 512, false);//.getResizedBitmap(BitmapFactory.decodeFile(files[0].getAbsolutePath()), 512, 512);
            // ResizeImage.ScaleDownBitmap(BitmapFactory.decodeFile(files[0].getAbsolutePath()), 512, false); keeps aspect ratio

            File f = new File(mContext.getCacheDir(), "upload");
            f.createNewFile();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            resized.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            byte[] bitmapdata = bos.toByteArray();
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bitmapdata);

            RestAdapter restAdapter = BeRestAdapter.getMemberTokenRestAdapter();
            IMemberResource memberService = restAdapter.create(IMemberResource.class);

            TypedFile typedFile = new TypedFile("image/jpeg", f);
            ImageUploadWrapper result = memberService.uploadMemberProfileImage(MemberAuthStore.getAuth(mContext).getToken(), MemberAuthStore.getMember(mContext).getId(), typedFile);

            if (result != null && result.getImagebig() != null && result.getImagesmall() != null) {
                MemberBean memberBean = new MemberBean();
                memberBean.setImagesmall(result.getImagesmall());
                memberBean.setImagebig(result.getImagebig());

                MemberBean me = memberService.updateMember(MemberAuthStore.getAuth(mContext).getToken(), MemberAuthStore.getMember(mContext).getId(), memberBean);
                if (me.getId() != null) {
                    AuthResource.IAuthResource authService = restAdapter.create(AuthResource.IAuthResource.class);
                    Response response = authService.me(MemberAuthStore.getAuth(mContext).getToken());
                    MeBean meBean = new Gson().fromJson(new InputStreamReader(response.getBody().in()), MeBean.class);
                    if (meBean.getMember() != null) {
                        MemberAuthStore.setMember(mContext, meBean.getMember());
                    }
                }
            }

            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(ImageUploadWrapper result) {
        super.onPostExecute(result);

        if(result != null) {
            if (mListener != null) {
                mListener.onProfileUpdate(result.getImagesmall());
            }
        } else {
            if (mListener != null) {
                mListener.onProfileUpdateFailed();
            }
        }
        try {
            mProgressDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
