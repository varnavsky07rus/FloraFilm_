package com.alaka_ala.florafilm.sys;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alaka_ala.florafilm.R;

import java.util.List;

import io.noties.markwon.Markwon;
import io.noties.markwon.core.CorePlugin;
import io.noties.markwon.html.HtmlPlugin;
import io.noties.markwon.html.TagHandlerNoOp;
import io.noties.markwon.utils.NoCopySpannableFactory;

public class TimelineView extends View {
    private Paint linePaint;
    private Paint circlePaint;
    private int circleRadius = 10;
    private int lineWidth = 5;
    private int ringWidth = 3; // Толщина кольца
    public String color_circle = "#2AB12A";
    public String colorLine = "#FF505050";


    public TimelineView(Context context) {
        super(context);
        init();
    }

    public TimelineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        for (int i = 0; i < attrs.getAttributeCount(); i++) {
            if (attrs.getAttributeName(i).startsWith("color")) {
                color_circle = attrs.getAttributeValue(i);
            } else if (attrs.getAttributeName(i).startsWith("backgroundTint")) {
                colorLine = attrs.getAttributeValue(i);
            }
        }
        init();
    }

    private void init() {
        linePaint = new Paint();
        linePaint.setColor(Color.parseColor(colorLine));
        linePaint.setStrokeWidth(lineWidth);

        circlePaint = new Paint();
        circlePaint.setColor(Color.parseColor(color_circle));
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(ringWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        // Рисуем верхнюю часть линии
        canvas.drawLine(centerX, 0, centerX, centerY - circleRadius, linePaint);

        // Рисуем кольцо
        canvas.drawCircle(centerX, centerY, circleRadius, circlePaint);

        // Рисуем нижнюю часть линии
        canvas.drawLine(centerX, centerY + circleRadius, centerX, getHeight(), linePaint);
    }



    public static class UpdateAdapter extends RecyclerView.Adapter<UpdateAdapter.UpdateViewHolder> {

        private final List<String> updateComments;
        private final Markwon markwon;

        public UpdateAdapter(List<String> updateComments, Context context) {
            this.updateComments = updateComments;
            this.markwon = Markwon.builder(context).usePlugin(HtmlPlugin.create()).build();
        }

        @NonNull
        @Override
        public UpdateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.commits_app_item, parent, false);
            return new UpdateViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull UpdateViewHolder holder, int position) {
            holder.updateCommentTextView.setSpannableFactory(NoCopySpannableFactory.getInstance());
            String comment = updateComments.get(position);
            //markwon.setMarkdown(holder.updateCommentTextView, comment);
            final Spanned spanned = markwon.toMarkdown(comment);
            holder.updateCommentTextView.setText(spanned);
        }

        @Override
        public int getItemCount() {
            return updateComments.size();
        }

        public static class UpdateViewHolder extends RecyclerView.ViewHolder {
            TextView updateCommentTextView;

            public UpdateViewHolder(@NonNull View itemView) {
                super(itemView);
                updateCommentTextView = itemView.findViewById(R.id.updateCommentTextView);
            }
        }
    }

}