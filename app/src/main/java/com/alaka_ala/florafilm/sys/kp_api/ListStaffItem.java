package com.alaka_ala.florafilm.sys.kp_api;

import java.io.Serializable;

/**
 * Данный класс содержит в себе краткую информацию об актёре/актрисе.
 * Предназначен для быстрого отображения списка актёров/актрис на странице фильма
 * Данный класс добавляется в массив ArrayList
 */
public class ListStaffItem implements Serializable {

    private int staffId;
    private String nameRu;
    private String nameEn;
    private String description;
    private String posterUrl;
    private String professionText;
    private String professionKey;

    public String getProfessionKey() {
        return professionKey;
    }

    public String getProfessionText() {
        return professionText;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public String getDescription() {
        return description;
    }

    public String getNameEn() {
        return nameEn;
    }

    public String getNameRu() {
        return nameRu;
    }

    public int getStaffId() {
        return staffId;
    }


    public ListStaffItem(int staffId, String nameRu, String nameEn, String description, String posterUrl, String professionText, String professionKey) {
        this.staffId = staffId;
        this.nameRu = nameRu;
        this.nameEn = nameEn;
        this.description = description;
        this.posterUrl = posterUrl;
        this.professionText = professionText;
        this.professionKey = professionKey;
    }


}
