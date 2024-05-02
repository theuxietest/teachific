package com.so.luotk.models.output;

import java.util.ArrayList;

import lombok.Data;

@Data
public class StudyMaterialData {
    private String id;
    private String fk_batchId;
    private String type;
    private ArrayList<String> content;
    private String name;
    private boolean isNewItem;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFk_batchId() {
        return fk_batchId;
    }

    public void setFk_batchId(String fk_batchId) {
        this.fk_batchId = fk_batchId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<String> getContent() {
        return content;
    }

    public void setContent(ArrayList<String> content) {
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isNewItem() {
        return isNewItem;
    }

    public void setNewItem(boolean newItem) {
        isNewItem = newItem;
    }
}
