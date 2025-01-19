package com.alaka_ala.recyclerrec.view_holders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alaka_ala.recyclerrec.R;

public class ViewHolderFile extends RecyclerView.ViewHolder {
    private final ImageView imageViewFile;
    private final TextView textViewTitleFile;

    public ViewHolderFile(@NonNull View itemView) {
        super(itemView);
        imageViewFile = itemView.findViewById(R.id.imageViewFile);
        textViewTitleFile = itemView.findViewById(R.id.textViewTitleFile);
    }


    public TextView getTextViewTitleFile() {
        return textViewTitleFile;
    }

    public ImageView getImageViewFile() {
        return imageViewFile;
    }
}
