package com.alaka_ala.recyclerrec.data_class;

import androidx.annotation.DrawableRes;

import java.util.ArrayList;

public class FileItem {
    private final int drawableResId;

    private final int idFile;

    private final String titleFile;

    private final ArrayList<FolderItem> listFolders;

    private final String uriFile;

    public ArrayList<FolderItem> getFoldersList() {
        return listFolders;
    }

    public String getTitleFile() {
        return titleFile;
    }

    public int getIdFile() {
        return idFile;
    }

    public FileItem(Builder builder) {
        this.uriFile = builder.uriFile;
        this.listFolders = builder.listFolders;
        this.drawableResId = builder.drawableResId;
        this.idFile = builder.idFile;
        this.titleFile = builder.titleFile;
    }

    public int getDrawableResId() {
        return drawableResId;
    }


    public static class Builder {
        private String uriFile;
        @DrawableRes
        private int drawableResId;
        private int idFile;
        private String titleFile;
        private ArrayList<FolderItem> listFolders;

        public Builder setFoldersList(ArrayList<FolderItem> listFolders) {
            this.listFolders = listFolders;
            return this;
        }

        public void setDrawableResId(@DrawableRes int drawableResId) {
            this.drawableResId = drawableResId;
        }

        public Builder setIdFile(int idFile) {
            this.idFile = idFile;
            return this;
        }

        public Builder setTitleFile(String titleFile) {
            this.titleFile = titleFile;
            return this;
        }

        public Builder setUriFile(String uriFile) {
            this.uriFile = uriFile;
            return this;
        }

        public FileItem build() {
            return new FileItem(this);
        }
    }

}
