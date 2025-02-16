package com.alaka_ala.florafilm.sys.kp_api;

import java.io.Serializable;

/**Супруги актёра/актрисы.*/
public class StaffSpouseItem implements Serializable {
    public StaffSpouseItem(int personId, String webUrl, String name, String sex) {
        this.personId = personId;
        this.webUrl = webUrl;
        this.name = name;
        this.sex = sex;
        posterUrl = "https://kinopoiskapiunofficial.tech/images/actor_posters/kp/" + personId + ".jpg";
    }



    private final String posterUrl;
    private final int personId;
    private final String webUrl;
    private final String name;
    private final String sex;

    public String getSex() {
        return sex;
    }

    public String getName() {
        return name;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public int getPersonId() {
        return personId;
    }

    public String getPosterUrl() {
        return posterUrl;
    }
}
