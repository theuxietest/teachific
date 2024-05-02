package com.so.luotk.models.output;

import lombok.Data;

@Data
public class AdminSettings {
    private String name;
    private String isOn;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIsOn() {
        return isOn;
    }

    public void setIsOn(String isOn) {
        this.isOn = isOn;
    }

    public AdminSettings(String name) {
        this.name = name;
    }
}
