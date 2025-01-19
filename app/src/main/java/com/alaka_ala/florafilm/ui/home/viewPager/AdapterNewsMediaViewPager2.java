package com.alaka_ala.florafilm.ui.home.viewPager;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.alaka_ala.florafilm.sys.kp_api.NewsMedia;
import com.alaka_ala.florafilm.ui.home.viewPager.fragments.NewsMediaItemFragment;

import java.util.ArrayList;

public class AdapterNewsMediaViewPager2 extends FragmentStateAdapter {
    private ArrayList<NewsMedia> newsMediaArrayList;

    public AdapterNewsMediaViewPager2(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, ArrayList<NewsMedia> newsMediaArrayList) {
        super(fragmentManager, lifecycle);
        this.newsMediaArrayList = newsMediaArrayList;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        Bundle bundle = new Bundle();
        bundle.putInt("index", position);
        bundle.putSerializable("newsMedia", newsMediaArrayList.get(position));

        return NewsMediaItemFragment.newInstance(bundle);
    }

    @Override
    public int getItemCount() {
        return newsMediaArrayList.size();
    }


    public static class ZoomOutPageTransformer implements ViewPager2.PageTransformer, ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.85f;
        private static final float MIN_ALPHA = 0.5f;

        @Override
        public void transformPage(@NonNull View page, float position) {
            int pageWidth = page.getWidth();
            int pageHeight = page.getHeight();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                page.setAlpha(0f);
            } else if (position <= 1) { // [-1,1]
                // Modify the default slide transition to shrink the page as well
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    page.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    page.setTranslationX(-horzMargin + vertMargin / 2);
                }

                // Scale the page down (between MIN_SCALE and 1)
                page.setScaleX(scaleFactor);
                page.setScaleY(scaleFactor);

                // Fade the page relative to its size.
                page.setAlpha(MIN_ALPHA +
                        (scaleFactor - MIN_SCALE) /
                                (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            } else { // (1,Infinity]
                // This page is way off-screen to the right.
                page.setAlpha(0f);
            }
        }
    }

    public static class DepthPageTransformerimplements implements ViewPager2.PageTransformer {

        private static final float MIN_SCALE = 0.75f;

        @Override
        public void transformPage(@NonNull View page, float position) {
            int pageWidth = page.getWidth();

            if (position < -1) { //[-Infinity,-1)
                // This page is way off-screen to the left.
                page.setAlpha(0f);
            } else if (position <= 0) { // [-1,0]
                // Use the default slide transition when moving to the left page
                page.setAlpha(1f);
                page.setTranslationX(0f);
                page.setScaleX(1f);
                page.setScaleY(1f);
            } else if (position <= 1) { // (0,1]
                // Fade the page out.page.setAlpha(1 - position);

                // Counteract the default slide transition
                page.setTranslationX(pageWidth * -position);

                // Scale the page down (between MIN_SCALE and 1)
                float scaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position));
                page.setScaleX(scaleFactor);
                page.setScaleY(scaleFactor);

                // Change the page's elevation to give it a 3D effect
                page.setElevation(1 - position);
            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                page.setAlpha(0f);
            }
        }
    }

    public static class CubeOutPageTransformer implements ViewPager2.PageTransformer {

        @Override
        public void transformPage(@NonNull View page, float position) {
            if (position < -1){    // [-Infinity,-1)
                // This page is way off-screen to the left.
                page.setAlpha(0);
            } else if (position <= 0) {    // [-1,0]
                page.setAlpha(1);
                page.setPivotX(page.getWidth());
                page.setRotationY(-90 * Math.abs(position));
            } else if (position <= 1){    // (0,1]
                page.setAlpha(1);
                page.setPivotX(0);
                page.setRotationY(90 * Math.abs(position));
            } else {    // (1,+Infinity]
                // This page is way off-screen to the right.
                page.setAlpha(0);
            }


            if (position < -1){    // [-Infinity,-1)
                // This page is way off-screen to the left.
                page.setAlpha(0);
            }
            else if (position <= 0) {    // [-1,0]
                page.setAlpha(1);
                page.setPivotX(page.getWidth());
                page.setRotationY(-90 * Math.abs(position));
            }
            else if (position <= 1){    // (0,1]
                page.setAlpha(1);
                page.setPivotX(0);
                page.setRotationY(90 * Math.abs(position));
            }
            else {    // (1,+Infinity]
                // This page is way off-screen to the right.
                page.setAlpha(0);
            }

            // Change the page's elevation to give it a 3D effect
            if (position < -1 || position > 1) {
                page.setElevation(0);
            } else {
                page.setElevation(-Math.abs(position));
            }
        }
    }

    public static class PageFlipPageTransformer implements ViewPager2.PageTransformer {

        @Override
        public void transformPage(@NonNull View page, float position) {
            float percentage = 1 - Math.abs(position);
            page.setCameraDistance(3000);

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                page.setAlpha(0);
            } else if (position <= 0) { // [-1,0]
                page.setAlpha(1);
                page.setPivotX(0); // Pivot point is now on the left edge
                page.setRotationY(90 * Math.abs(position)); // Rotate in the opposite direction
                page.setTranslationX(page.getWidth() * position); // Translate in the opposite direction
            } else if (position <= 1) { // (0,1]
                page.setAlpha(1);
                page.setPivotX(page.getWidth()); // Pivot point is now on the right edge
                page.setRotationY(-90 * Math.abs(position)); // Rotate in the opposite direction
                page.setTranslationX(page.getWidth() * position); // Translate in the opposite direction
            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                page.setAlpha(0);
            }
        }
    }

    public static class DepthPageTransformer implements ViewPager2.PageTransformer {

        private static final float MIN_SCALE = 0.75f;

        @Override
        public void transformPage(@NonNull View page, float position) {
            int pageWidth = page.getWidth();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                page.setAlpha(0f);
            } else if (position <= 0) { // [-1,0]
                // Use the default slide transition when moving to the left page
                page.setAlpha(1f);
                page.setTranslationX(0f);
                page.setScaleX(1f);
                page.setScaleY(1f);
            } else if (position <= 1) { // (0,1]
                // Fade the page out.
                page.setAlpha(1 - position);

                // Counteract the default slide transition
                page.setTranslationX(pageWidth * -position);

                // Scale the page down (between MIN_SCALE and 1)
                float scaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position));
                page.setScaleX(scaleFactor);
                page.setScaleY(scaleFactor);

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                page.setAlpha(0f);
            }
        }
    }


}
