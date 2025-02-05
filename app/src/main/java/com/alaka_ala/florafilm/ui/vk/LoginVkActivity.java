package com.alaka_ala.florafilm.ui.vk;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.alaka_ala.florafilm.R;
import com.alaka_ala.florafilm.databinding.ActivityLoginVkBinding;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginVkActivity extends AppCompatActivity {
    private ActivityLoginVkBinding binding;
    private WebView webViewLoginVk;
    private static final String CLIENT_ID = "6463690"; //  6287487 (VK.COM)   | 6121396 (VK ADMIN)  | KATE (2685278) | 6463690 (Маруся)
    private static final String SCOPE = "video,audio,offline"; //   // 1073737727 - Полный доступ
    private static String baseURl;
    public static final String USER_AGENT = "VKAndroidApp/5.52-4543 (Android 5.1.1; SDK 22; x86_64; unknown Android SDK built for x86_64; en; 320x240)";
    public static final String USER_AGENT_YA = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0.0.0 YaBrowser/24.12.0.0 Safari/537.36";
    public static final String USER_AGENT_KATE = "KateMobileAndroid/56 lite-460 (Android 4.4.2; SDK 19; x86; unknown Android SDK built for x86; en)";
    public static final String REDIRECT_URI = "https://oauth.vk.com/blank.html";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginVkBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        webViewLoginVk = binding.webViewLoginVk;
        webViewLoginVk.getSettings().setJavaScriptEnabled(true);
        // Запрет открытия новых страниц в браузере
        webViewLoginVk.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", USER_AGENT);
        baseURl = "https://oauth.vk.com/authorize?&redirect_uri=" + REDIRECT_URI + "&client_id=" + CLIENT_ID + "&scope=" + SCOPE + "&response_type=token&v=5.199&revoke=1&display=android";

        webViewLoginVk.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                // Обработка редиректа
                if (url.startsWith("https://oauth.vk.com/auth_redirect") || url.startsWith("https://oauth.vk.com/blank.html")) {
                    handleRedirect(url);
                }
                return false; // Разрешить WebView загрузить URL
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                // Обработка ошибок загрузки страницы
                System.err.println("Error loading page: " + error.getDescription());
            }
        });

        webViewLoginVk.loadUrl(baseURl, headers);

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(baseURl));
        //startActivity(intent);

    }


    private void handleRedirect(String url) {
        // Здесь вы можете обработать редирект
        // Например, закрыть WebView и передать URL в другое место
        webViewLoginVk.destroy();

        Uri uri = Uri.parse(url);
        String sheme = uri.getScheme();
        if (sheme.equals("https")) {
            String authorize_url = uri.getQueryParameter("authorize_url");
            String decodeUrl = Uri.decode(authorize_url);
            String replaceSharp = decodeUrl.replace("#", "?");

            Uri authorize_uri = Uri.parse(replaceSharp);
            String acceessToken = authorize_uri.getQueryParameter("access_token");
            String userId = authorize_uri.getQueryParameter("user_id");
            String expiresIn = authorize_uri.getQueryParameter("expires_in");
            if (expiresIn == null || expiresIn.isEmpty()) {
                int tsExpiresToken = (int) Math.abs(System.currentTimeMillis() / 1000 + 3600);
                expiresIn = "" + tsExpiresToken; // 1 Час действителен токен
            }
            getProfileInfo(acceessToken, Integer.parseInt(expiresIn), new CallbackGetProfileInfo() {
                @Override
                public void onSuccess(Account account) {
                    AccountManager.saveAccount(LoginVkActivity.this, account);
                    finish();
                }

                @Override
                public void onError(Exception e) {

                }
            });



        }

    }

    private void getProfileInfo(String acceessToken, int expiresIn, CallbackGetProfileInfo callback) {
        String baseUrl = "https://api.vk.com/method/account.getProfileInfo?v=5.131&access_token=" + acceessToken;
        Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                if (!msg.getData().getBoolean("ok")) {
                    callback.onError(new Exception("Error | :D"));
                    return true;
                }

                String body = msg.getData().getString("body");
                if (body != null) {
                    if (JsonParser.parseString(body).isJsonObject()) {
                        try {
                            JSONObject jsonObject = new JSONObject(body);
                            JSONObject response = jsonObject.getJSONObject("response");
                            Account.Builder builder = new Account.Builder();
                            builder.setId(response.getString("id"));
                            builder.setHomeTown(response.getString("home_town"));
                            builder.setStatus(response.getString("status"));
                            builder.setPhoto200(response.getString("photo_200"));
                            builder.setBdate(response.getString("bdate"));

                            builder.setIsVerified(response.has("is_verified") && response.getBoolean("is_verified"));
                            builder.setFirstName(response.getString("first_name"));
                            builder.setLastName(response.getString("last_name"));
                            builder.setPhone(response.getString("phone"));
                            builder.setScreenName(response.getString("screen_name"));
                            builder.setExpireIn(expiresIn);
                            builder.setAccessToken(acceessToken);
                            callback.onSuccess(builder.build());
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                return false;
            }
        });
        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder req = new Request.Builder();
        req.url(baseUrl);
        req.header("User-Agent", USER_AGENT_YA);
        okHttpClient.newCall(req.build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Bundle bundle = new Bundle();
                bundle.putString("body", "");
                bundle.putBoolean("ok", false);
                Message msg = new Message();
                msg.setData(bundle);
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String body = response.body().string();
                Bundle bundle = new Bundle();
                bundle.putString("body", body);
                bundle.putBoolean("ok", true);
                Message msg = new Message();
                msg.setData(bundle);
                handler.sendMessage(msg);
            }
        });


    }

    private interface CallbackGetProfileInfo {
        void onSuccess(Account account);
        void onError(Exception e);
    }

    public static class Account implements Serializable {
        public static int DEF_EXPIRES_TOKEN_SEC = 3600; // 1 часа в сек
        public static int DEF_EXPIRES_TOKEN_MS = 3600000; // 1 час в мс

        public Account(Builder builder) {
            this.tokenReplacementTimeStamp = builder.tokenReplacementTimeStamp;
            this.id = builder.id;
            this.home_town = builder.home_town;
            this.status = builder.status;
            this.photo_200 = builder.photo_200;
            this.bdate = builder.bdate;
            this.is_verified = builder.is_verified;
            this.first_name = builder.first_name;
            this.last_name = builder.last_name;
            this.phone = builder.phone;
            this.screen_name = builder.screen_name;
            this.access_token = builder.access_token;
            this.expire_in = builder.expire_in;
        }

        public String getId() {
            return id;
        }

        public String getHomeTown() {
            return home_town;
        }

        public String getStatus() {
            return status;
        }

        public String getPhoto200() {
            return photo_200;
        }

        public String getBdate() {
            return bdate;
        }

        public boolean isIsVerified() {
            return is_verified;
        }

        public String getFirstName() {
            return first_name;
        }

        public String getLastName() {
            return last_name;
        }

        public String getPhone() {
            return phone;
        }

        public String getScreenName() {
            return screen_name;
        }

        public String getAccessToken() {
            return access_token;
        }

        public int getExpireIn() {
            return expire_in;
        }

        public long getTokenReplacementTimeStamp() {
            return tokenReplacementTimeStamp;
        }

        private final long tokenReplacementTimeStamp;
        private final String id;
        private final String home_town;
        private final String status;
        private final String photo_200;
        private final String bdate;
        private final boolean is_verified;
        private final String first_name;
        private final String last_name;
        private final String phone;
        private final String screen_name;
        private final String access_token;
        private final int expire_in;


        public static class Builder implements Serializable {
            protected long tokenReplacementTimeStamp;
            private String id;
            private String home_town;
            private String status;
            private String photo_200;
            private String bdate;
            private boolean is_verified;
            private String first_name;
            private String last_name;
            private String phone;
            private String screen_name;
            private String access_token;
            private int expire_in;

            public void setTokenReplacementTimeStamp(long tokenReplacementTimeStamp) {
                this.tokenReplacementTimeStamp = tokenReplacementTimeStamp;
            }

            public void setId(String id) {
                this.id = id;
            }

            public void setHomeTown(String home_town) {
                this.home_town = home_town;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public void setPhoto200(String photo_200) {
                this.photo_200 = photo_200;
            }

            public void setBdate(String bdate) {
                this.bdate = bdate;
            }

            public void setIsVerified(boolean is_verified) {
                this.is_verified = is_verified;
            }

            public void setFirstName(String first_name) {
                this.first_name = first_name;
            }

            public void setLastName(String last_name) {
                this.last_name = last_name;
            }

            public void setPhone(String phone) {
                this.phone = phone;
            }

            public void setScreenName(String screen_name) {
                this.screen_name = screen_name;
            }

            public void setExpireIn(int expire_in) {
                this.expire_in = expire_in;
            }

            public void setAccessToken(String access_token) {
                this.access_token = access_token;
                setTokenReplacementTimeStamp(System.currentTimeMillis() + DEF_EXPIRES_TOKEN_MS);
            }

            public Account build() {
                return new Account(this);
            }
        }

    }





}