package com.alaka_ala.florafilm.sys.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.StringDef;
import androidx.recyclerview.widget.RecyclerView;

import com.alaka_ala.florafilm.R;
import com.alaka_ala.florafilm.sys.kp_api.ListStaffItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PersonsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final String TYPE_HOLDER_MAIN_PERSON = "TYPE_HOLDER_MAIN_PERSON";

    private final ArrayList<ListStaffItem> listStaffItem;
    private final String currentTypeHolder;
    private final LayoutInflater layoutInflater;

    public ArrayList<ListStaffItem> getListStaffItem() {
        return listStaffItem;
    }

    @StringDef({TYPE_HOLDER_MAIN_PERSON})
    public @interface TypeHoldersPersons {
    }

    public PersonsRecyclerAdapter(@TypeHoldersPersons String typeHolder, ArrayList<ListStaffItem> listStaffItem, LayoutInflater layoutInflater) {
        this.listStaffItem = listStaffItem;
        this.currentTypeHolder = typeHolder;
        this.layoutInflater = layoutInflater;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (currentTypeHolder.equals(TYPE_HOLDER_MAIN_PERSON)) {
            View view = layoutInflater.inflate(R.layout.rv_item_person, null, false);
            return new ViewHolderMainPerson(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolderMainPerson) {
            Picasso.get().load(listStaffItem.get(position).getPosterUrl()).into(((ViewHolderMainPerson) holder).imageViewPerson);
            ((ViewHolderMainPerson) holder).textViewNamePerson.setText(listStaffItem.get(position).getNameRu());
            String role;

            role = listStaffItem.get(position).getDescription();
            if (role.equals("null")) {
                role = listStaffItem.get(position).getProfessionText();
            }

            ((ViewHolderMainPerson) holder).textViewRolePerson.setText(role);
        }
    }

    @Override
    public int getItemCount() {
        return listStaffItem.size();
    }


    private static class ViewHolderMainPerson extends RecyclerView.ViewHolder {
        private final ImageView imageViewPerson;
        private final TextView textViewNamePerson;
        private final TextView textViewRolePerson;


        public ViewHolderMainPerson(@NonNull View itemView) {
            super(itemView);
            imageViewPerson = itemView.findViewById(R.id.imageViewPerson);
            textViewNamePerson = itemView.findViewById(R.id.textViewNamePerson);
            textViewRolePerson = itemView.findViewById(R.id.textViewRolePersone);
        }


        public TextView getTextViewNamePerson() {
            return textViewNamePerson;
        }

        public ImageView getImageViewPerson() {
            return imageViewPerson;
        }

        public TextView getTextViewRolePerson() {
            return textViewRolePerson;
        }
    }


}
