package com.alaka_ala.florafilm;

import static android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EdgeEffect;
import android.widget.TextClock;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.alaka_ala.florafilm.databinding.ActivityMainBinding;
import com.alaka_ala.florafilm.sys.update_app.UpdateApp;
import com.alaka_ala.florafilm.sys.utils.SettingsApp;
import com.alaka_ala.florafilm.sys.utils.ViewClickable;
import com.alaka_ala.florafilm.ui.updateApp.UpdateActivity;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private NavigationView nav_view;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavController navController;
    private SettingsApp settingsApp;
    private static CallbackVisibilityFloatActionButtonMenu callbackVisibilityFloatActionButtonMenu;
    private static CallbackFullscreenAppMode callbackFullscreenAppMode;
    private AppBarConfiguration appBarConfiguration;
    private CoordinatorLayout coordinatorLayout;
    private AppBarLayout appBarLayout;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(binding.getRoot().getId()), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getWindow().setFlags(
                // Включает аппаратное ускорение
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
        );







        settingsApp = new SettingsApp(this);
        nav_view = binding.navView;
        toolbar = binding.toolbar;
        drawerLayout = binding.drawerLayout;
        coordinatorLayout = binding.coordinatorLayout;
        appBarLayout = binding.appBarLayout;


        if (settingsApp.getParam(SettingsApp.SettingsKeys.HIDE_APP_BAR_LAYOUT, SettingsApp.SettingsDefsVal.HIDE_APP_BAR_LAYOUT)) {
            appBarLayout.setVisibility(View.GONE);
        }

        // Делаем статус-бар прозрачным
        // getWindow().setStatusBarColor(Color.TRANSPARENT);

        setSupportActionBar(toolbar);

        navController = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(nav_view, navController);
        AppBarConfiguration.Builder builder = new AppBarConfiguration.Builder(navController.getGraph());
        builder.setDrawerLayout(drawerLayout);
        appBarConfiguration = builder.build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);


        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        // Обработчик и натройка на полноэкранный режим
        fullscreenAppMode(settingsApp.getParam(SettingsApp.SettingsKeys.FULL_SCREEN_APP_MODE, SettingsApp.SettingsDefsVal.FULL_SCREEN_APP_MODE), false);
        setCallbackFullscreenAppMode(new CallbackFullscreenAppMode() {
            @Override
            public void onFullscreenAppMode(boolean fullScreen) {
                fullscreenAppMode(settingsApp.getParam(SettingsApp.SettingsKeys.FULL_SCREEN_APP_MODE, fullScreen), true);
            }

            @Override
            public void onHideAppBars(boolean hide) {
                appBarLayout.setVisibility(hide ? View.GONE : View.VISIBLE);
                Snackbar.make(binding.getRoot(), "Перезагрузить приложение сейчас?", Snackbar.LENGTH_SHORT).setAction("Да", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        startActivity(MainActivity.this.getIntent());
                    }
                }).show();

            }
        });

        // Что бы нажатие на задний фон DrawerLayout (тот что затемняется) закрывал DrawerLayout
        drawerLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // Проверяем, находится ли нажатие на scrim
                    if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
                        drawerLayout.closeDrawer(GravityCompat.START);
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        });

        // Что бы сделать плавное закрытие floatingActionButtonMenu при закрытии DrawerLayout
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            private boolean isOpened = false;
            private float startFabPosition = 0;

            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {


                float fabShift = drawerView.getWidth() * slideOffset;
                coordinatorLayout.setX(fabShift);
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                isOpened = true;
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                isOpened = false;
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                // Не используется в данном случае
            }
        });

        updateStatusBarIconColor(this);

        UpdateApp.findUpdate(this, new UpdateApp.FindUpdateCallback() {
            @Override
            public void onUpdateDetect(String urlApk, String newVersionName, int newVersionCode, int currentVersionCode) {
                Snackbar.make(binding.getRoot(), "Доступно обновление", Snackbar.LENGTH_SHORT).setAction("Обновить", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, UpdateActivity.class);
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
                if (!error.startsWith("#3")) {
                    Snackbar.make(binding.getRoot(), error, Snackbar.LENGTH_SHORT).show();
                }

            }
        });


    }

    private void fullscreenAppMode(boolean fullScreen, boolean isManualEdit) {
        // Скрывает верхнюю строку состояния
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(binding.drawerLayout.getId()), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        if (fullScreen) {
            EdgeToEdge.enable(this);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            if (isManualEdit) {
                Snackbar snackbar = Snackbar.make(binding.getRoot(), "Перезагрузить приложение сейчас?", Snackbar.LENGTH_SHORT);
                snackbar.setAction("Да", new View.OnClickListener() {
                    @SuppressLint("UnsafeIntentLaunch")
                    @Override
                    public void onClick(View v) {
                        finish();
                        startActivity(getIntent());
                    }
                }).show();
            }
        }

    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && drawerLayout.isOpen()) {
            drawerLayout.close();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK && Navigation.findNavController(this, R.id.nav_host_fragment_activity_main).getGraph().getStartDestination() == Navigation.findNavController(this, R.id.nav_host_fragment_activity_main).getCurrentDestination().getId()) {
            new MaterialAlertDialogBuilder(this).setMessage("Выйти из приложения?").setPositiveButton("Да", (dialog, which) -> finish()).setNegativeButton("Нет", (dialog, which) -> dialog.dismiss()).show();
            return false;
        }
        if (keyCode == KeyEvent.KEYCODE_HOME) {
            navController.popBackStack();
        }
        return super.onKeyDown(keyCode, event);
    }


    private interface CallbackVisibilityFloatActionButtonMenu {
        void onVisibilityFloatActionButtonMenu(boolean visibility);
    }

    public static void setFullscreenAppMode(boolean fullScreen) {
        callbackFullscreenAppMode.onFullscreenAppMode(fullScreen);
    }

    public void setCallbackFullscreenAppMode(CallbackFullscreenAppMode cb) {
        callbackFullscreenAppMode = cb;
    }

    public static void onHideAppBarLayout(boolean hide) {
        callbackFullscreenAppMode.onHideAppBars(hide);
    }

    private interface CallbackFullscreenAppMode {
        void onFullscreenAppMode(boolean fullScreen);

        void onHideAppBars(boolean hide);
    }

    public static void updateStatusBarIconColor(Activity activity) {
        int currentNightMode = activity.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        boolean isDarkTheme = currentNightMode == Configuration.UI_MODE_NIGHT_YES;
        if (isDarkTheme) {
            // White icons on dark background
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                activity.getWindow().getDecorView().getWindowInsetsController().setSystemBarsAppearance(0, APPEARANCE_LIGHT_STATUS_BARS);
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                activity.getWindow().getDecorView().getWindowInsetsController().setSystemBarsAppearance(APPEARANCE_LIGHT_STATUS_BARS, APPEARANCE_LIGHT_STATUS_BARS);
            }
            // Black icons on light background
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
}