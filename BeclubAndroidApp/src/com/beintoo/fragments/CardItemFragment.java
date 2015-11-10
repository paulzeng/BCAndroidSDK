package com.beintoo.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beintoo.beclubapp.R;
import com.beintoo.utils.BeUtils;
import com.beintoo.wrappers.CardsInfoResponse;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;

public class CardItemFragment extends Fragment {

    private CardsInfoResponse card;

    public CardItemFragment(CardsInfoResponse cardsInfoResponse) {
        this.card = cardsInfoResponse;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cards_item, container, false);

        ((TextView)view.findViewById(R.id.itemName)).setText("**** **** **** " + card.getFourdigit());
        ImageView ivCard = (ImageView) view.findViewById(R.id.itemImage);
        final int widthPx = getResources().getDisplayMetrics().widthPixels - 20;
        final int heightPx = (widthPx * 280) / 730;
        ivCard.setLayoutParams(new RelativeLayout.LayoutParams(widthPx, heightPx));

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_image_placeholder_a)
                .displayer(new FadeInBitmapDisplayer(250))
                .cacheOnDisk(true)
                .postProcessor(new BitmapProcessor() {
                    @Override
                    public Bitmap process(Bitmap bitmap) {
                        return BeUtils.getRoundedCornerBitmap(getActivity(), bitmap, 3, bitmap.getWidth(), bitmap.getHeight(), false, false, true, true);
                    }
                })
                .build();
        ImageLoader.getInstance().displayImage(card.getCardimageurl(), ivCard, options);

        view.findViewById(R.id.imageView).setVisibility(View.GONE);

        view.findViewById(R.id.ll_total_earned).setVisibility(View.GONE);

        return view;
    }
}
