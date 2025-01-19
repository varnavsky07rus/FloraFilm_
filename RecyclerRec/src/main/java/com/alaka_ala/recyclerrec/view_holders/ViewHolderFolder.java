package com.alaka_ala.recyclerrec.view_holders;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alaka_ala.recyclerrec.R;

public class ViewHolderFolder extends RecyclerView.ViewHolder {
    private final ImageView imageViewFolder;
    private final TextView textViewTitleFileFolder;
    private final FrameLayout frameLayoutRecyclerViewFolder;
    private final RecyclerView recyclerViewFolder;


    public RecyclerView getRecyclerViewFolder() {
        return recyclerViewFolder;
    }

    public FrameLayout getFrameLayoutRecyclerViewFolder() {
        return frameLayoutRecyclerViewFolder;
    }

    public TextView getTitleFileFolder() {
        return textViewTitleFileFolder;
    }

    public ImageView getImageViewFolder() {
        return imageViewFolder;
    }



    public ViewHolderFolder(@NonNull View itemView) {
        super(itemView);
        imageViewFolder = itemView.findViewById(R.id.imageViewFolder);
        textViewTitleFileFolder = itemView.findViewById(R.id.textViewTitleFileFolder);
        frameLayoutRecyclerViewFolder = itemView.findViewById(R.id.FrameLayoutRecyclerViewFolder);
        recyclerViewFolder = itemView.findViewById(R.id.recyclerViewFolder);
    }


}
