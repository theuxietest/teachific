package com.so.luotk.models.videoData;

public class QualityModel {
    public String id;
    public String name;
    public String value;
    public boolean isSelected;

    public QualityModel(String id, String name, boolean isSelected, String value) {
        this.id = id;
        this.name = name;
        this.value = value;
        this.isSelected = isSelected;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}