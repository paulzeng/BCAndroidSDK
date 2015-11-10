package com.beintoo.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.beintoo.beclubapp.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;

/**
 * Created by Giulio Bider on 23/09/14.
 * Copyright (c) 2014 Beintoo. All rights reserved.
 */
public class PictureItemFragment extends Fragment {

    private String mPictureUrl;
    private int mPosition;
    private int mWidth;
    private int mHeight;

    public PictureItemFragment(String pictureUrl, int position) {
        this.mPictureUrl = pictureUrl;
        this.mPosition = position;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.picture_item_fragment, container, false);

        ImageView ivPosition = (ImageView) view.findViewById(R.id.iv_position);
        if(mPosition == 0) {
            ivPosition.setImageResource(R.drawable.ic_picture_first_place);
        } else if(mPosition == 1) {
            ivPosition.setImageResource(R.drawable.ic_picture_second_place);
        } else {
            ivPosition.setImageResource(R.drawable.ic_picture_third_place);
        }

        ImageView ivPhoto = (ImageView) view.findViewById(R.id.iv_photo);

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_image_placeholder_a)
                .cacheOnDisk(true)
                .displayer(new FadeInBitmapDisplayer(250))
                .preProcessor(new BitmapProcessor() {
                    @Override
                    public Bitmap process(Bitmap bitmap) {
                        mWidth = getResources().getDisplayMetrics().widthPixels;
                        mHeight = (mWidth * bitmap.getHeight()) / bitmap.getWidth();
                        return Bitmap.createScaledBitmap(bitmap, mWidth, mHeight, true);
                    }
                })
                .build();

        ImageLoader.getInstance().displayImage(mPictureUrl, ivPhoto, options);


        return view;
    }

    public int getHeight() {
        return mHeight;
    }

    public int getWidth() {
        return mWidth;
    }
}
