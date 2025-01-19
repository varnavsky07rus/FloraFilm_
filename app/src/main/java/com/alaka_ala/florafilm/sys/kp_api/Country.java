package com.alaka_ala.florafilm.sys.kp_api;

import java.io.Serializable;

public class Country implements Serializable {

    public Country(String country) {
        this.country = country;
    }

    public String getCountry() {
        return country;
    }

    private final String country;

}
