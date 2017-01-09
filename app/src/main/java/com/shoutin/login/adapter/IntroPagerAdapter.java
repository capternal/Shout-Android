package com.shoutin.login.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.shoutin.R;
import com.shoutin.login.model.IntroPagerModel;

import java.util.ArrayList;

import pl.droidsonroids.gif.GifImageView;

/**
 * Created by jupitor on 14/12/16.
 */

public class IntroPagerAdapter extends PagerAdapter {

    int imageIndex = 0;
    Activity objActivity;
    ArrayList<IntroPagerModel> arrIntroPagerModel = new ArrayList<IntroPagerModel>();
    ImageView image;
    GifImageView imageLoader;

    public IntroPagerAdapter(Activity objActivity, ArrayList<IntroPagerModel> arrIntroPagerModel) {
        this.objActivity = objActivity;
        this.arrIntroPagerModel = arrIntroPagerModel;

    }

    public void ImageViewAnimatedChange(Context c, final ImageView v, final int imageId) {
//        final Animation anim_out = AnimationUtils.loadAnimation(c, R.anim.slide_down);
        final Animation anim_out = AnimationUtils.loadAnimation(c, android.R.anim.fade_out);
//        final Animation anim_in = AnimationUtils.loadAnimation(c, R.anim.slide_in_up);
        final Animation anim_in = AnimationUtils.loadAnimation(c, android.R.anim.fade_in);
        anim_out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                v.setImageResource(imageId);
                anim_in.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        callImageLoader();
                    }
                });
                v.startAnimation(anim_in);
            }
        });
        v.startAnimation(anim_out);
    }

    private void callImageLoader() {

        Handler objImageHandler = new Handler();
        objImageHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                switch (imageIndex) {
                    case 0:
                        ImageViewAnimatedChange(objActivity, imageLoader, R.drawable.bike);
                        imageIndex = 1;
                        break;
                    case 1:
                        ImageViewAnimatedChange(objActivity, imageLoader, R.drawable.car);
                        imageIndex = 2;
                        break;
                    case 2:
                        ImageViewAnimatedChange(objActivity, imageLoader, R.drawable.dress);
                        imageIndex = 3;
                        break;
                    case 3:
                        ImageViewAnimatedChange(objActivity, imageLoader, R.drawable.gamepad);
                        imageIndex = 4;
                        break;
                    case 4:
                        ImageViewAnimatedChange(objActivity, imageLoader, R.drawable.grill);
                        imageIndex = 5;
                        break;
                    case 5:
                        ImageViewAnimatedChange(objActivity, imageLoader, R.drawable.photo_camera);
                        imageIndex = 6;
                        break;
                    case 6:
                        ImageViewAnimatedChange(objActivity, imageLoader, R.drawable.ping_pong);
                        imageIndex = 7;
                        break;
                    case 7:
                        ImageViewAnimatedChange(objActivity, imageLoader, R.drawable.smartphone);
                        imageIndex = 8;
                        break;
                    case 8:
                        ImageViewAnimatedChange(objActivity, imageLoader, R.drawable.sofa);
                        imageIndex = 9;
                        break;
                    case 9:
                        ImageViewAnimatedChange(objActivity, imageLoader, R.drawable.stroller);
                        imageIndex = 10;
                        break;
                    case 10:
                        ImageViewAnimatedChange(objActivity, imageLoader, R.drawable.tent);
                        imageIndex = 0;
                        break;
                }
            }
        }, 200);
    }

    @Override
    public int getCount() {
        return arrIntroPagerModel.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater mLayoutInflater = (LayoutInflater) objActivity.getSystemService(objActivity.LAYOUT_INFLATER_SERVICE);
        View itemView = mLayoutInflater.inflate(R.layout.intro_page_layout, container, false);
        image = (ImageView) itemView.findViewById(R.id.intro_image);
        imageLoader = (GifImageView) itemView.findViewById(R.id.imageView_loader);
//        callImageLoader();
        image.setTag(position);
        IntroPagerModel objIntroPagerModel = arrIntroPagerModel.get(position);
        image.setBackgroundResource(objIntroPagerModel.getImageId());
        if (position == arrIntroPagerModel.size() - 1) {
            imageLoader.setVisibility(ImageView.VISIBLE);
        } else {
            imageLoader.setVisibility(ImageView.GONE);
        }
        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(View container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);
    }
}
