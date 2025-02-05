package com.alaka_ala.florafilm.ui.settings;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
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
import com.alaka_ala.florafilm.sys.update_app.UpdateApp;
import com.alaka_ala.florafilm.sys.utils.SettingsApp;
import com.alaka_ala.florafilm.ui.updateApp.UpdateActivity;
import com.alaka_ala.florafilm.ui.vk.AccountManager;
import com.alaka_ala.florafilm.ui.vk.LoginVkActivity;
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.io.File;

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
        MaterialSwitch switchVisibleBlockNewsMedia = binding.switchVisibleBlockNewsMedia;
        switchVisibleBlockNewsMedia.setChecked(settingsApp.getParam(
                SettingsApp.SettingsKeys.BLOCK_NEWS_MEDIA,
                SettingsApp.SettingsDefsVal.VISIBLE_BLOCK_NEWS_MEDIA));
        switchVisibleBlockNewsMedia.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settingsApp.saveParam(SettingsApp.SettingsKeys.BLOCK_NEWS_MEDIA, isChecked);
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
                textViewNumberMaskProfile.setText("+7 ****-***-**-**");


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

        TextView textViewCurrentVersionApp = binding.textViewCurrentVersionApp;
        textViewCurrentVersionApp.setText("Версия приложения: " + UpdateApp.getCurrentAppVersionName(getContext()) + "(" + UpdateApp.getCurrentAppVersionCode(getContext()) + ")");

        Chip chipFindUpdate = binding.chipFindUpdate;
        chipFindUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateApp.findUpdate(getContext(), new UpdateApp.FindUpdateCallback() {
                    @Override
                    public void onUpdateDetect(String urlApk, String newVersionName, int newVersionCode, int currentVersionCode) {
                        Snackbar.make(binding.getRoot(), "Доступно обновление", Snackbar.LENGTH_SHORT).setAction("Обновить", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getContext(), UpdateActivity.class);
                                intent.putExtra("newVersionCode", newVersionCode);
                                intent.putExtra("currentVersionCode", currentVersionCode);
                                intent.putExtra("newVersionName", newVersionName);
                                intent.putExtra("urlAPK", urlApk);
                                startActivity(intent);

                            }
                        }).show();
                    }

                    @Override
                    public void findError(String error) {
                        new MaterialAlertDialogBuilder(getContext()).setMessage(error).show();
                    }
                });
            }
        });


        MaterialSwitch switchHideAppBarLayout = binding.switchHideAppBarLayout;
        switchHideAppBarLayout.setChecked(settingsApp.getParam(
                SettingsApp.SettingsKeys.HIDE_APP_BAR_LAYOUT,
                SettingsApp.SettingsDefsVal.HIDE_APP_BAR_LAYOUT));
        switchHideAppBarLayout.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settingsApp.saveParam(SettingsApp.SettingsKeys.HIDE_APP_BAR_LAYOUT, isChecked);
                MainActivity.onHideAppBarLayout(isChecked);
            }
        });


        return binding.getRoot();
    }


}