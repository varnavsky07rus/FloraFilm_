package com.alaka_ala.florafilm.ui.settings;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.alaka_ala.florafilm.MainActivity;
import com.alaka_ala.florafilm.R;
import com.alaka_ala.florafilm.databinding.FragmentSettingsAppBinding;
import com.alaka_ala.florafilm.sys.utils.SettingsApp;
import com.alaka_ala.florafilm.ui.vk.AccountManager;
import com.alaka_ala.florafilm.ui.vk.LoginVkActivity;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

public class SettingsAppFragment extends Fragment {
    private FragmentSettingsAppBinding binding;
    private SettingsApp settingsApp;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSettingsAppBinding.inflate(inflater, container, false);
        settingsApp = new SettingsApp(getContext());

        // Скрыть показывать плавающую кнопку на главном экране использующуюся в MainActivity
        MaterialSwitch switchVisibleFloatActionButtonMenu = binding.switchVisibleFloatActionButtonMenu;
        switchVisibleFloatActionButtonMenu.setChecked(settingsApp.getParam(
                SettingsApp.SettingsKeys.FLOAT_ACTION_BUTTON_MENU,
                SettingsApp.SettingsDefsVal.VISIBLE_FLOAT_ACTION_BUTTON_MENU));
        switchVisibleFloatActionButtonMenu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settingsApp.saveParam(SettingsApp.SettingsKeys.FLOAT_ACTION_BUTTON_MENU, isChecked);
                //MainActivity.setVisibilityFloatActionButtonMenu(isChecked);
            }
        });


        MaterialSwitch switchFullscreenMode = binding.switchFullscreenMode;
        switchFullscreenMode.setChecked(settingsApp.getParam(
                SettingsApp.SettingsKeys.FULL_SCREEN_APP_MODE,
                SettingsApp.SettingsDefsVal.FULL_SCREEN_APP_MODE));
        switchFullscreenMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settingsApp.saveParam(SettingsApp.SettingsKeys.FULL_SCREEN_APP_MODE, isChecked);
                MainActivity.setFullscreenAppMode(isChecked);
            }
        });


        MaterialSwitch switchMinimizeInterfaceNoise = binding.switchMinimizeMode;
        switchMinimizeInterfaceNoise.setChecked(settingsApp.getParam(
                SettingsApp.SettingsKeys.INTERFACE_ANIMATION,
                SettingsApp.SettingsDefsVal.DEF_INTERFACE_ANIMATION));
        switchMinimizeInterfaceNoise.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settingsApp.saveParam(SettingsApp.SettingsKeys.INTERFACE_ANIMATION, isChecked);
            }
        });


        LoginVkActivity.Account account = AccountManager.getAccount(getContext());
        // ТОКЕН КОТОРЫЙ ПОЛУЧЕН ПУТЁМ vk.com дейтсвителен только 86400 сек. необходимо реализовать замену токена по истечению действительности
        ImageView imageViewDeleteAccount = binding.imageViewDeleteAccount;

        if (account != null) {
            ImageView imageViewPhotoProfile = binding.imageViewPhotoProfile;
            Picasso.get().load(account.getPhoto200()).into(imageViewPhotoProfile);
            TextView textViewNameProfile = binding.textViewNameProfile;
            textViewNameProfile.setText(account.getFirstName() + " " + account.getLastName());
            TextView textViewNumberMaskProfile = binding.textViewNumberMaskProfile;
            textViewNumberMaskProfile.setText(account.getPhone());
        } else {
            imageViewDeleteAccount.setVisibility(View.GONE);
        }

        imageViewDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView imageViewPhotoProfile = binding.imageViewPhotoProfile;
                Picasso.get().load(R.drawable.sad_rounded_square_emoticon).into(imageViewPhotoProfile);
                AccountManager.deleteAccount(getContext());
                TextView textViewNameProfile = binding.textViewNameProfile;
                TextView textViewNumberMaskProfile = binding.textViewNumberMaskProfile;

                textViewNameProfile.setText("Добавить аккаунт");
                textViewNumberMaskProfile.setText("+7 ###-###-##-##");


                Snackbar.make(binding.getRoot(), "Аккаунт удален", Snackbar.LENGTH_SHORT).show();
            }
        });

        CardView cardViewAccountVk = binding.cardViewAccountVk;
        cardViewAccountVk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), LoginVkActivity.class);
                getContext().startActivity(intent);
            }
        });


        return binding.getRoot();
    }


    @Override
    public void onResume() {
        super.onResume();
        MainActivity.setVisibilityFloatActionButtonMenu(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MainActivity.setVisibilityFloatActionButtonMenu(settingsApp.getParam(SettingsApp.SettingsKeys.FLOAT_ACTION_BUTTON_MENU, SettingsApp.SettingsDefsVal.VISIBLE_FLOAT_ACTION_BUTTON_MENU));
    }
}