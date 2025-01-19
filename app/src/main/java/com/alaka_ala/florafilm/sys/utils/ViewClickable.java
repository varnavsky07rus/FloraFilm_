package com.alaka_ala.florafilm.sys.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.GestureDetectorCompat;

/** Класс для обработки нажатия на View элементы.
 *@Method {@link ViewClickable#onTouchClick(View, MotionEvent)},
 *@Method {@link ViewClickable#onTouchLongClick(View, MotionEvent)},
 *@Method {@link ViewClickable#onDoubleClick(View, MotionEvent)}*/
public abstract class ViewClickable implements View.OnTouchListener {

    /**
     * Одиночный тап на View
     */
    public abstract void onTouchClick(View view, MotionEvent e);

    /**
     * Одиночный тап на View но с продолжительностью более 1 сек (1000мс)
     */
    public abstract void onTouchLongClick(View view, MotionEvent e);

    /**
     * Двойной клик
     */
    public abstract void onDoubleClick(View view, MotionEvent e);

    private GestureDetectorCompat gestureDetector;

    private int countTap = 0;

    /**Получение View объекта на котором вызывается клик*/
    public View getView() {
        return view;
    }

    /**Получение количества тапов.
     * @Warn: Спустя 350мс сек тапы сбрасываются на значение: 0.
     * По этому метод пригодится если необходимо получить кол-во тапов сиеминутно*/
    public int getCountTap() {
        return countTap;
    }

    /**Получение значения задержки когда тапы сбросятся до значения 0*/
    public int getDelayTapReset() {
        return delayTapReset;
    }

    /**Можно установить задержку когда тапы сбросят до значения 0*/
    public void setDelayTapReset(int delayTapReset) {
        this.delayTapReset = delayTapReset;
    }

    private int delayTapReset = 350;

    private View view;

    public ViewClickable(Context context) {
        Handler handlerResetCountTap = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                countTap = 0;
                return false;
            }
        });
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                handlerResetCountTap.sendEmptyMessage(0);
                handlerResetCountTap.removeCallbacks(this);
            }
        };
        gestureDetector = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (countTap == 0) {
                    onTouchClick(view, e); // Вызываем onTouchClick при одиночном клике
                }
                ++countTap;
                handlerResetCountTap.postDelayed(runnable, delayTapReset);
                return true;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                onDoubleClick(view, e); // Вызываем onDoubleClick при двойном клике
                countTap = 2;
                handlerResetCountTap.postDelayed(runnable, delayTapReset);
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                onTouchLongClick(view, e); // Вызываем onTouchLongClick при длительном нажатии
                ++countTap;
                handlerResetCountTap.postDelayed(runnable, delayTapReset);
            }
        });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        this.view = v;
        gestureDetector.onTouchEvent(event); // Передаем событие GestureDetector
        return false; // Возвращаем true, чтобы перехватить событие
    }


}