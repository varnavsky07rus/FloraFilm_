package com.alaka_ala.florafilm.ui.search.viewPager;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.alaka_ala.florafilm.ui.search.viewPager.fragments.FavoriteSearchFragment;
import com.alaka_ala.florafilm.ui.search.viewPager.fragments.GlobalSearchFragment;
import com.alaka_ala.florafilm.ui.search.viewPager.fragments.ViewedSearchFragment;

public class AdapterSearchViewPager extends FragmentStateAdapter {
    private String title;

    public AdapterSearchViewPager(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);

    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            default:
                return new GlobalSearchFragment();
            case 1:
                return new FavoriteSearchFragment();
            case 2:
                return new ViewedSearchFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
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

    public static class WheelPageTransformer implements ViewPager2.PageTransformer {

        private static final float ROTATION_SPEED = 1.1f; // Adjust this value to control the rotation speed

        @Override
        public void transformPage(@NonNull View page, float position) {
            int pageWidth = page.getWidth();
            int pageHeight = page.getHeight();

            if (position < -1) { // [-Infinity,-1)
                page.setAlpha(0f);
            } else if (position <= 1) { // [-1,1]
                // Calculate the rotation angle based on the position and rotation speed
                float rotationAngle = 90f * position * ROTATION_SPEED;

                // Set the pivot point to the center of the bottom edge
                page.setPivotX(pageWidth / 2f);
                page.setPivotY(pageHeight);

                // Apply the rotation
                page.setRotation(rotationAngle);

                // Adjust alpha for smoother transitions (optional)
                page.setAlpha(Math.max(0f, 1f - Math.abs(position)));
            } else { // (1,+Infinity]
                page.setAlpha(0f);
            }
        }
    }

}
