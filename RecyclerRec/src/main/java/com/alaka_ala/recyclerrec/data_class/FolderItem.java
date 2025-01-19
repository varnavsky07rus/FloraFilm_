package com.alaka_ala.recyclerrec.data_class;

import androidx.annotation.DrawableRes;

import java.util.ArrayList;

public class FolderItem {

    private final int drawableResId;

    private final int idFolder;

    private final String titleFolder;

    private final ArrayList<FileItem> listFiles;

    public String getTitleFolder() {
        return titleFolder;
    }

    public int getIdFolder() {
        return idFolder;
    }

    public ArrayList<FileItem> getListFiles() {
        return listFiles;
    }




    public FolderItem(Builder builder) {
        this.listFiles = builder.listFiles;
        this.drawableResId = builder.drawableResId;
        this.idFolder = builder.idFolder;
        this.titleFolder = builder.titleFolder;
    }

    public int getDrawableResId() {
        return drawableResId;
    }


    public static class Builder {
        private int drawableResId;
        private int idFolder;
        private String titleFolder;
        private ArrayList<FileItem> listFiles;

        public void setListFiles(ArrayList<FileItem> listFiles) {
            this.listFiles = listFiles;
        }

        public void setDrawableResId(@DrawableRes int drawableResId) {
            this.drawableResId = drawableResId;
        }

        public Builder setIdFolder(int idFolder) {
            this.idFolder = idFolder;
            return this;
        }

        public Builder setTitleFolder(String titleFolder) {
            this.titleFolder = titleFolder;
            return this;
        }

        public FolderItem build() {
            return new FolderItem(this);
        }
    }

}
