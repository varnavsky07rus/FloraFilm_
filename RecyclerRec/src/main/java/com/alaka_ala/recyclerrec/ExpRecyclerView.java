package com.alaka_ala.recyclerrec;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import androidx.annotation.NonNull;
import androidx.annotation.StringDef;
import androidx.recyclerview.widget.RecyclerView;

import com.alaka_ala.recyclerrec.data_class.FileItem;
import com.alaka_ala.recyclerrec.data_class.FolderItem;
import com.alaka_ala.recyclerrec.view_holders.ViewHolderFile;
import com.alaka_ala.recyclerrec.view_holders.ViewHolderFolder;

import java.util.ArrayList;

public class ExpRecyclerView extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final String TYPE_HOLDER_FOLDER = "folder";
    public static final String TYPE_HOLDER_FILE = "file";

    public ExpRecyclerView(ArrayList<Object> list, LayoutInflater inflater) {
        this.list = list;
        this.inflater = inflater;
    }

    @StringDef({TYPE_HOLDER_FOLDER, TYPE_HOLDER_FILE})
    public @interface TypesHolder {}
    private final ArrayList<Object> list; // Здесь собраны массивы файлов и папок
    private final LayoutInflater inflater;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0x0000001) {
            View viewFile = inflater.inflate(R.layout.item_file, parent, false);
            return new ViewHolderFile(viewFile);
        } else if (viewType == 0x0000002) {
            View viewFolder = inflater.inflate(R.layout.item_folder, parent, false);
            return new ViewHolderFolder(viewFolder);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position) instanceof ArrayList) {
            ArrayList<?> arrayList = (ArrayList<?>) list.get(position);
            if (arrayList.get(position) instanceof FileItem) {
                return 0x0000001;
            } else if (arrayList.get(position) instanceof FolderItem) {
                return 0x0000002;
            }
        }
        return position;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolderFile) {
            ArrayList<FileItem> arrayList = (ArrayList<FileItem>) list.get(position);

            FileItem fileItem = (FileItem) list.get(position);
            ViewHolderFile holderFile = (ViewHolderFile) holder;

            holderFile.getTextViewTitleFile().setText(fileItem.getTitleFile());
            if (fileItem.getDrawableResId() != 0) {
                holderFile.getImageViewFile().setImageResource(fileItem.getDrawableResId());
            }

        } else if (holder instanceof ViewHolderFolder) {
            FolderItem folderItem = (FolderItem) list.get(position);
            ViewHolderFolder holderFolder = (ViewHolderFolder) holder;

            holderFolder.getTitleFileFolder().setText(folderItem.getTitleFolder());
            if (folderItem.getDrawableResId() != 0) {
                holderFolder.getImageViewFolder().setImageResource(folderItem.getDrawableResId());
            }
        }
    }

    @Override
    public int getItemCount() {
        int sizeList = list.size();
        return sizeList;
    }
}
