package com.alaka_ala.florafilm.ui.film.vk_pager;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.media.session.MediaSessionCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.alaka_ala.florafilm.R;
import com.alaka_ala.florafilm.databinding.RvItem4Binding;
import com.alaka_ala.florafilm.sys.utils.ViewClickable;
import com.alaka_ala.florafilm.ui.player.exo.ExoActivity;
import com.alaka_ala.florafilm.ui.player.exo.models.EPData;
import com.alaka_ala.florafilm.ui.vk.AccountManager;
import com.alaka_ala.florafilm.ui.vk.LoginVkActivity;
import com.alaka_ala.florafilm.ui.vk.parser.VKVideo;
import com.alaka_ala.florafilm.ui.vk.ui.comments.VKCommentsFragment;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class VKViewPagerFragment extends Fragment {
    private RvItem4Binding binding;
    private VKVideo.VideoItem videoItem;
    private VideoView videoView;
    private ImageView imageViewReplay, imageViewPlayVideoVK;
    private CardView cardViewControlls;
    private TextView textViewCurrentTimePosition, textViewTitle, textViewTotalLike, textViewTotalViews, textViewTotalComments;
    private VKVideo vkVideo;
    private MaterialCardView mcvComments;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = RvItem4Binding.inflate(inflater, container, false);
        vkVideo = new VKVideo(AccountManager.getAccessToken(getContext()));
        videoView = binding.videoView;
        videoItem = (VKVideo.VideoItem) getArguments().getSerializable("video");
        imageViewReplay = binding.imageViewReplay;
        imageViewPlayVideoVK = binding.imageViewPlayVideoVK;
        cardViewControlls = binding.cardViewControlls;
        textViewCurrentTimePosition = binding.textViewCurrentTimePosition;
        textViewTitle = binding.textViewTitle;
        textViewTotalViews = binding.textViewTotalViews;
        textViewTotalLike = binding.textViewTotalLike;
        textViewTotalComments = binding.textViewTotalComments;
        mcvComments = binding.mcvComments;


        assert videoItem != null;
        textViewTotalViews.setText(String.valueOf(videoItem.getViews()));
        textViewTotalLike.setText(String.valueOf(videoItem.getLikes().getCount()));
        textViewTotalComments.setText(String.valueOf(videoItem.getComments()));
        String title = videoItem.getTitle() == null ? "Неизвестен" : videoItem.getTitle();
        textViewTitle.setText(title);

        vkVideo.getCommentsVideo("" + videoItem.getOwner_id(), "" + videoItem.getId(), 0, 30, new VKVideo.GetCommentsVideoCallback() {
            private VKVideo.GetCommentsVideoCallback context;
            @Override
            public void onSuccess(ArrayList<VKVideo.CommentVideo> comments) {
                context = this;
                mcvComments.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (comments.isEmpty()) {
                            Snackbar.make(binding.getRoot(), "Комментарии отсутствуют", Snackbar.LENGTH_SHORT).show();
                            return;
                        }
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("comments", comments);
                        Navigation.findNavController(v).navigate(R.id.action_filmFragment_to_VKCommentsFragment, bundle);
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                context = this;
                Snackbar.make(binding.getRoot(), "Ошибка загрузки комментариев", Snackbar.LENGTH_SHORT).setAction("Повторить", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vkVideo.getCommentsVideo("" + videoItem.getOwner_id(), "" + videoItem.getId(), 0, 30, context);
                    }
                }).show();
            }
        });


        createPlayer();


        return binding.getRoot();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void createPlayer() {
        String file480Preview = videoItem.getTrailers().getMp4_480();
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", LoginVkActivity.USER_AGENT_KATE);
        videoView.setVideoURI(Uri.parse(file480Preview), headers);
        videoView.start();

        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                textViewCurrentTimePosition.setText("Видео недоступно");
                return true;
            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                showCardViewControll();
            }
        });

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                startUpdaterCurrentPosition();
                hideCardViewControll();
            }
        });

        videoView.setOnTouchListener(new ViewClickable(getContext()) {
            @Override
            public void onTouchClick(View view, MotionEvent e) {

            }

            @Override
            public void onTouchLongClick(View view, MotionEvent e) {
                // ТУТ НАДО ОПРЕДЕЛИТЬ СКРЫТ ЛИ CardViewControll
                // и скрыть или показать
                if (cardViewControlls.getVisibility() == View.VISIBLE) {
                    hideCardViewControll();
                } else {
                    showCardViewControll();
                }
            }

            @Override
            public void onDoubleClick(View view, MotionEvent e) {

            }
        });

    }

    private void hideCardViewControll() {
        if (cardViewControlls.getVisibility() == View.GONE) return;
        videoView.start();
        cardViewControlls.setVisibility(View.VISIBLE);
        Animation animExit = AnimationUtils.loadAnimation(getContext(), R.anim.exit_action_size);
        animExit.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardViewControlls.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        cardViewControlls.startAnimation(animExit);
    }

    private void showCardViewControll() {
        if (cardViewControlls.getVisibility() == View.VISIBLE) return;
        videoView.pause();
        cardViewControlls.setVisibility(View.VISIBLE);
        Animation animShow = AnimationUtils.loadAnimation(getContext(), R.anim.show_action_size);
        animShow.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                imageViewReplay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cardViewControlls.setVisibility(View.GONE);
                        createPlayer();
                        //startUpdaterCurrentPosition();
                    }
                });

                imageViewPlayVideoVK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EPData.Film.Builder epData = new EPData.Film.Builder();
                        EPData.Film.Translations.Builder translations = new EPData.Film.Translations.Builder();
                        translations.setTitle("Неизвестен [VK Video]");
                        List<Map.Entry<String, String>> videoData = new ArrayList<>();
                        videoData.add(new AbstractMap.SimpleEntry<>("HLS", videoItem.getFiles().getHls()));
                        translations.setVideoData(videoData);
                        epData.setPoster(videoItem.getImages().get(0).getUrl());
                        ArrayList<EPData.Film.Translations> t = new ArrayList<>();
                        t.add(translations.build());
                        epData.setTranslations(t);
                        Intent intent = new Intent(getContext(), ExoActivity.class);
                        intent.putExtra("film", epData.build());
                        intent.putExtra("titleQuality", "HLS");
                        intent.putExtra("indexQuality", 0);
                        intent.putExtra("indexSeason", 0);
                        intent.putExtra("indexEpisode", 0);
                        intent.putExtra("indexTranslation", 0);

                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        cardViewControlls.startAnimation(animShow);
    }

    @Override
    public void onPause() {
        super.onPause();
        isDestroyContext = true;
        videoView.pause();
        videoView.stopPlayback();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isDestroyContext = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        isDestroyContext = false;
        createPlayer();
    }

    public static VKViewPagerFragment newInstance(Bundle b) {
        VKViewPagerFragment fragment = new VKViewPagerFragment();
        fragment.setArguments(b);
        return fragment;
    }

    private boolean isDestroyContext = false;

    public void startUpdaterCurrentPosition() {
        Handler handlerUpdater = new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                Bundle bundle = msg.getData();
                String currentPosition = bundle.getString("currentTimePosition");
                textViewCurrentTimePosition.setText(currentPosition);

                return false;
            }
        });

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isDestroyContext) {
                    if (videoView == null) return;
                    try {
                        int currentPosition = videoView.getCurrentPosition();
                        int totalMs = videoView.getDuration();
                        if (currentPosition == 0) continue;
                        String currentTimePosition;

                        Bundle bundle = new Bundle();
                        if (currentPosition == 100) {
                            currentTimePosition = formatTime(totalMs, totalMs);
                        } else {
                            currentTimePosition = formatTime(currentPosition, totalMs);
                        }

                        bundle.putString("currentTimePosition", currentTimePosition);
                        Message msg = new Message();
                        msg.setData(bundle);
                        handlerUpdater.sendMessage(msg);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
        thread.start();
    }

    public String formatTime(int currentPositMs, int totalMs) {
        int currentSeconds = currentPositMs / 1000;
        int currentMinutes = currentSeconds / 60;
        currentSeconds %= 60;

        int totalSeconds = totalMs / 1000;
        int totalMinutes = totalSeconds / 60;
        totalSeconds %= 60;
        return String.format(Locale.getDefault(), "%d:%02d / %d:%02d",
                currentMinutes, currentSeconds, totalMinutes, totalSeconds);
    }
}
