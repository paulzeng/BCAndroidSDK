package com.beintoo.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.beintoo.activities.OnlineMissionActivity;
import com.beintoo.beclubapp.R;
import com.beintoo.utils.BeUtils;
import com.beintoo.wrappers.OnlineProductBean;
import com.etsy.android.grid.util.DynamicHeightImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Random;

/**
 * Created by Giulio Bider on 05/09/14.
 * Copyright (c) 2014 Beintoo. All rights reserved.
 */
public class OnlineProductAdapter extends ArrayAdapter<OnlineProductBean> {

    private final LayoutInflater mLayoutInflater;
    private final Random mRandom;
    private final SparseArray<Double> sPositionHeightRatios = new SparseArray<Double>();
    private Context mContext;
    private Bundle mIntentBundle;

    public OnlineProductAdapter(Context context, int textViewResourceId, List<OnlineProductBean> objects, Bundle bundle) {
        super(context, textViewResourceId, objects);
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mRandom = new Random();
        this.mContext = context;
        this.mIntentBundle = bundle;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.online_product_item, parent, false);
            vh = new ViewHolder();
            vh.imgView = (DynamicHeightImageView) convertView.findViewById(R.id.imgView);
            vh.textView = (TextView) convertView.findViewById(R.id.tv_earn_bedollars);
            vh.tvGetItFor = (TextView) convertView.findViewById(R.id.tv_get_it_for);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        double positionHeight = getPositionRatio(position);
        vh.imgView.setHeightRatio(positionHeight);

        final OnlineProductBean item = getItem(position);

        if(!item.getImagebig().equals(vh.imgUrl)) {
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.ic_image_placeholder_a)
                    .displayer(new FadeInBitmapDisplayer(250))
                    .cacheOnDisk(true)
                    .preProcessor(new BitmapProcessor() {
                        @Override
                        public Bitmap process(Bitmap bitmap) {
                            return BeUtils.getRoundedCornerBitmap(mContext, bitmap, 3, bitmap.getWidth(), bitmap.getHeight(), false, false, true, true);
                        }
                    })
                    .build();
            ImageLoader.getInstance().displayImage(item.getImagebig(), vh.imgView, options);
        } else {
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .cacheOnDisk(true)
                    .preProcessor(new BitmapProcessor() {
                        @Override
                        public Bitmap process(Bitmap bitmap) {
                            return BeUtils.getRoundedCornerBitmap(mContext, bitmap, 3, bitmap.getWidth(), bitmap.getHeight(), false, false, true, true);
                        }
                    })
                    .build();

            ImageLoader.getInstance().displayImage(item.getImagebig(), vh.imgView, options);
        }
        vh.imgUrl = item.getImagebig();

        DecimalFormat myFormatter = new DecimalFormat(".00");

        String html = "GET IT FOR <b>$" + myFormatter.format(item.getCost()) + "</b>";

        vh.tvGetItFor.setText(Html.fromHtml(html));
        vh.textView.setText("AND EARN " + item.getBedollars().intValue());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, OnlineMissionActivity.class);
                mIntentBundle.putString("product", item.toString());
                intent.putExtras(mIntentBundle);
                mContext.startActivity(intent);
            }
        });

        return convertView;
    }

    public class ViewHolder {
        DynamicHeightImageView imgView;
        TextView textView;
        TextView tvGetItFor;
        String imgUrl;
    }

    private double getPositionRatio(final int position) {
        double ratio = sPositionHeightRatios.get(position, 0.0);
        // if not yet done generate and stash the columns height
        // in our real world scenario this will be determined by
        // some match based on the known height and width of the image
        // and maybe a helpful way to get the column height!
        if (ratio == 0) {
            ratio = getRandomHeightRatio();
            sPositionHeightRatios.append(position, ratio);
        }
        return ratio;
    }

    private double getRandomHeightRatio() {
        return (mRandom.nextDouble() / 2.0) + 1.0; // height will be 1.0 - 1.5
        // the width
    }
}
