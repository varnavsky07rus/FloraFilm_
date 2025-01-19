package com.alaka_ala.florafilm.ui.updateApp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.alaka_ala.florafilm.R;
import com.alaka_ala.florafilm.databinding.ActivityUpdateBinding;
import com.alaka_ala.florafilm.sys.update_app.UpdateApp;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;

import at.huber.youtubeExtractor.BuildConfig;

public class UpdateActivity extends AppCompatActivity {
    private ActivityUpdateBinding binding;
    private static final int REQUEST_INSTALL_PERMISSION = 100;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(binding.getRoot().getId()), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String newVersionCode = getIntent().getStringExtra("newVersionCode");
        String description = getIntent().getStringExtra("description");
        String urlAPK = getIntent().getStringExtra("urlAPK");
        String urlMetadataJson = getIntent().getStringExtra("urlMetadataJson");


        binding.textView7.setText("Найдено обновление: " + newVersionCode);
        binding.textView8.setText("Текущая версия: " + UpdateApp.getAppVersion(this));
        binding.textView11.setText("Описание:" + description);

        binding.buttonInstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.buttonInstall.getText().equals("Установить")) {

                    if (getPackageManager().canRequestPackageInstalls()) {
                        // Разрешение уже предоставлено
                        // Продолжайте установку
                        installApp();
                    } else {
                        // Разрешение не предоставлено
                        // Обработайте ошибку
                        Snackbar.make(binding.getRoot(), "Разрешение не предоставлено", Snackbar.LENGTH_SHORT).setAction("Предоставить", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                requestInstallPermission();
                            }
                        }).show();
                    }


                } else {
                    binding.progressBar.setVisibility(View.VISIBLE);
                    binding.constraintLayoutDownload.setVisibility(View.VISIBLE);
                    UpdateApp.downloadUpdate(UpdateActivity.this, urlAPK, new UpdateApp.UpdateCallbackDownload() {
                        @Override
                        public void onUpdateDownloaded(File file) {
                            binding.progressBar.setVisibility(View.GONE);
                            binding.textView9.setText("Обновление загружено");
                            binding.buttonInstall.setText("Установить");
                        }

                        @Override
                        public void onError(String error, String moreError) {
                            new MaterialAlertDialogBuilder(UpdateActivity.this).setMessage("Ошибка загрузки обновления").show();
                        }

                        @Override
                        public void onProgressDownload(int progress, String downloadedSize, String countSize) {
                            binding.progressBar.setProgress(progress);
                            binding.textView10.setText(downloadedSize + " Mb \\ " + countSize + " Mb");
                        }
                    });
                }

            }
        });




    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_INSTALL_PERMISSION) {
            if (resultCode == RESULT_OK) {
                // Разрешение предоставлено
                // Продолжайте установку
                installApp();
            } else {
                // Разрешение не предоставлено
                // Обработайте ошибку
                Snackbar.make(binding.getRoot(), "Разрешение не предоставлено", Snackbar.LENGTH_SHORT).setAction("Предоставить", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        requestInstallPermission();
                    }
                });
            }
        }
    }


    private void requestInstallPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
            intent.putExtra("android.provider.extra.APP_PACKAGE", getPackageName()); // Используем строку как ключ
            intent.setData(Uri.fromParts("package", getPackageName(), null));
            startActivityForResult(intent, REQUEST_INSTALL_PERMISSION);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!getPackageManager().canRequestPackageInstalls()) {
                // Запрос разрешения на установку из неизвестных источников
                startActivityForResult(new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES), REQUEST_INSTALL_PERMISSION);
            } else {
                // Разрешение уже предоставлено
                // Продолжайте установку
                installApp();
            }
        } else {
            // Для старых версий Android
            // Запрос разрешения на установку из неизвестных источников
            startActivityForResult(new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES), REQUEST_INSTALL_PERMISSION);
        }
    }


    private void installApp() {
        File file = new File(this.getExternalCacheDir(), "app-release.apk");
        if (file.exists()) {
            Uri fileUri = FileProvider.getUriForFile(UpdateActivity.this, BuildConfig.APPLICATION_ID + ".fileprovider", file);
            Intent installIntent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
            installIntent.setData(fileUri);
            installIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(installIntent);
        } else {
            // Обработка ошибки, если файл не найден
        }
    }


}