package com.beintoo.adapters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.beintoo.beclubapp.R;
import com.beintoo.utils.BeUtils;
import com.beintoo.wrappers.EarnBedollarsWrapper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;

import java.util.Iterator;
import java.util.List;

public class EarnBedollarsAdapterV2 extends ArrayAdapter<EarnBedollarsWrapper> {

    public List<EarnBedollarsWrapper> objectsList;

    private Activity mContext;

    public EarnBedollarsAdapterV2(Activity context, int textViewResourceId, List<EarnBedollarsWrapper> objects) {
        super(context, textViewResourceId, objects);
        objectsList = objects;
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.earn_bedollars_row_v2, null);

            EarnHolder holder = new EarnHolder();

            holder.brand = (ImageView) convertView.findViewById(R.id.brand_1);
            holder.brandPicture = (ImageView) convertView.findViewById(R.id.brand_1_picture);
            holder.brandCal = (ImageView) convertView.findViewById(R.id.brand_1_cal);
            holder.brandCode = (ImageView) convertView.findViewById(R.id.brand_1_code);
            holder.brandWeb = (ImageView) convertView.findViewById(R.id.brand_1_web);
            holder.brandCollective = (ImageView) convertView.findViewById(R.id.brand_1_collective);
            holder.brandCard = (ImageView) convertView.findViewById(R.id.brand_1_card);
            holder.image = "";

            convertView.setTag(holder);
        }

        try {
            EarnHolder holder = (EarnHolder) convertView.getTag();
            EarnBedollarsWrapper leftBrand = getItem(position);

            holder.brandCal.setImageDrawable(mContext.getResources().getDrawable(R.drawable.piedini_grey));
            holder.brandCode.setImageDrawable(mContext.getResources().getDrawable(R.drawable.code_icon_gray));
            holder.brandWeb.setImageDrawable(mContext.getResources().getDrawable(R.drawable.web_icon_gray_big));
            holder.brandCollective.setImageDrawable(mContext.getResources().getDrawable(R.drawable.collective_mini_grey));
            holder.brandCard.setImageDrawable(mContext.getResources().getDrawable(R.drawable.card_grey));
            holder.brandPicture.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_picture_camera_icon_grey));


            // Applying fix for WALKIN_TIME mission type
            applyFixforWalkinTimeType(leftBrand);

            if (leftBrand.getBrands().getSponsoredactiontype() != null) {
                holder.brandCal.setVisibility(View.GONE);
                holder.brandCode.setVisibility(View.GONE);

                holder.brandWeb.setVisibility(View.GONE);
                holder.brandCollective.setVisibility(View.GONE);

                holder.brandCard.setVisibility(View.GONE);
                holder.brandPicture.setVisibility(View.GONE);

                for (Iterator<EarnBedollarsWrapper.MissionActionTypeEnum> i = leftBrand.getBrands().getSponsoredactiontype().iterator(); i.hasNext(); ) {
                    EarnBedollarsWrapper.MissionActionTypeEnum item = i.next();
                    if (item != null) {
                        addOrRemoveMissionInfoCell(leftBrand, item, holder);
                    }
                }
                for (Iterator<EarnBedollarsWrapper.MissionActionTypeEnum> i = leftBrand.getSubtype().iterator(); i.hasNext(); ) {
                    EarnBedollarsWrapper.MissionActionTypeEnum item = i.next();
                    if (item != null) {
                        addOrRemoveMissionInfoCell(leftBrand, item, holder);
                    }
                }
            } else {
                if (leftBrand.getSubtype().contains(EarnBedollarsWrapper.MissionActionTypeEnum.WALKIN) || leftBrand.getSubtype().contains(EarnBedollarsWrapper.MissionActionTypeEnum.WALKIN_TIME))
                    holder.brandCal.setImageDrawable(mContext.getResources().getDrawable(R.drawable.piedini_green));
                if (leftBrand.getSubtype().contains(EarnBedollarsWrapper.MissionActionTypeEnum.SCAN) || leftBrand.getSubtype().contains(EarnBedollarsWrapper.MissionActionTypeEnum.MULTIPLESCAN))
                    holder.brandCode.setImageDrawable(mContext.getResources().getDrawable(R.drawable.code_icon));
                if (leftBrand.getSubtype().contains(EarnBedollarsWrapper.MissionActionTypeEnum.ONLINE))
                    holder.brandWeb.setImageDrawable(mContext.getResources().getDrawable(R.drawable.web_icon));
                if (leftBrand.getSubtype().contains(EarnBedollarsWrapper.MissionActionTypeEnum.COLLECTIVE))
                    holder.brandCollective.setImageDrawable(mContext.getResources().getDrawable(R.drawable.collective_mini));
                if (leftBrand.getSubtype().contains(EarnBedollarsWrapper.MissionActionTypeEnum.CARDSPRING))
                    holder.brandCard.setImageDrawable(mContext.getResources().getDrawable(R.drawable.card_green));
                if (leftBrand.getSubtype().contains(EarnBedollarsWrapper.MissionActionTypeEnum.TAKEAPICTURE))
                    holder.brandPicture.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_picture_camera_icon_green));
            }

            if (leftBrand.getBrands().getImagesmall() != null) {
                if (!leftBrand.getBrands().getImagesmall().equals(holder.image)) {
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
                    ImageLoader.getInstance().displayImage(leftBrand.getBrands().getImagesmall(), holder.brand, options);
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
                    ImageLoader.getInstance().displayImage(leftBrand.getBrands().getImagesmall(), holder.brand, options);
                }
            }
            holder.image = leftBrand.getBrands().getImagesmall();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

    public class EarnHolder {
        public ImageView brand;
        public ImageView brandPicture;
        public ImageView brandCal;
        public ImageView brandCode;
        public ImageView brandWeb;
        public ImageView brandCollective;
        public ImageView brandCard;
        public String image;
    }

    /*
     * FIX for WALKIN_TIME mission type
     * 
     * if subtype or actioncheckbean contain WALKIN_TIME, let's do:
     * 	- if WALKIN is already present, delete WALKIN_TIME
     *  - if WALINK is not present, replace WALKIN_TIME with WALKIN
     */

    private void applyFixforWalkinTimeType(EarnBedollarsWrapper object) {
        if (object.getSubtype().contains(EarnBedollarsWrapper.MissionActionTypeEnum.WALKIN_TIME)) {
            if (object.getSubtype().contains(EarnBedollarsWrapper.MissionActionTypeEnum.WALKIN)) {
                int index = object.getSubtype().indexOf(EarnBedollarsWrapper.MissionActionTypeEnum.WALKIN_TIME);
                object.getSubtype().remove(index);
            } else {
                int index = object.getSubtype().indexOf(EarnBedollarsWrapper.MissionActionTypeEnum.WALKIN_TIME);
                object.getSubtype().set(index, EarnBedollarsWrapper.MissionActionTypeEnum.WALKIN);
            }
        }

        if (object.getBrands().getSponsoredactiontype() == null)
            return;

        if (object.getBrands().getSponsoredactiontype().contains(EarnBedollarsWrapper.MissionActionTypeEnum.WALKIN_TIME)) {
            if (object.getBrands().getSponsoredactiontype().contains(EarnBedollarsWrapper.MissionActionTypeEnum.WALKIN)) {
                int index = object.getBrands().getSponsoredactiontype().indexOf(EarnBedollarsWrapper.MissionActionTypeEnum.WALKIN_TIME);
                object.getBrands().getSponsoredactiontype().remove(index);
            } else {
                int index = object.getBrands().getSponsoredactiontype().indexOf(EarnBedollarsWrapper.MissionActionTypeEnum.WALKIN_TIME);
                object.getBrands().getSponsoredactiontype().set(index, EarnBedollarsWrapper.MissionActionTypeEnum.WALKIN);
            }
        }
    }
    
    /*
     * Check Subtype or Actioncheck Bean of Brand, to decide which are visible and active cell
     * 
     */

    private void addOrRemoveMissionInfoCell(EarnBedollarsWrapper object, EarnBedollarsWrapper.MissionActionTypeEnum type, EarnHolder holder) {
        ImageView imageView = null;
        int imageId = 0;

        switch (type) {
            case WALKIN:
            case WALKIN_TIME: {
                imageId = R.drawable.piedini_green;
                imageView = holder.brandCal;
            }
            break;

            case MULTIPLESCAN:
            case SCAN: {
                imageId = R.drawable.code_icon;
                imageView = holder.brandCode;
            }
            break;

            case ONLINE: {
                imageId = R.drawable.web_icon;
                imageView = holder.brandWeb;
            }
            break;

            case COLLECTIVE: {
                imageId = R.drawable.collective_mini;
                imageView = holder.brandCollective;
            }
            break;

            case CARDSPRING: {
                imageId = R.drawable.card_green;
                imageView = holder.brandCard;
            }
            break;

            case TAKEAPICTURE: {
                imageId = R.drawable.ic_picture_camera_icon_green;
                imageView = holder.brandPicture;
            }
            break;

            default:
                return;
        }

        if (object.getSubtype().contains(type)) {
            imageView.setImageDrawable(mContext.getResources().getDrawable(imageId));
            imageView.setVisibility(View.VISIBLE);
        } else {
            if (object.getBrands().getSponsoredactiontype() != null) {
                if (object.getBrands().getSponsoredactiontype().contains(type))
                    imageView.setVisibility(View.VISIBLE);
            }
        }
    }
}
