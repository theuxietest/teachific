
package com.so.luotk.models.newmodels.study;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Datum {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("fk_batchId")
    @Expose
    private Integer fkBatchId;
    @SerializedName("folderId")
    @Expose
    private Object folderId;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("content")
    @Expose
    private List<String> content = null;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("bool")
    @Expose
    private boolean isNewItem;
    @SerializedName("is_locked")
    @Expose
    private Integer is_locked=-1;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFkBatchId() {
        return fkBatchId;
    }

    public void setFkBatchId(Integer fkBatchId) {
        this.fkBatchId = fkBatchId;
    }

    public Object getFolderId() {
        return folderId;
    }

    public void setFolderId(Object folderId) {
        this.folderId = folderId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getContent() {
        return content;
    }

    public void setContent(List<String> content) {
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

    public Integer getIs_locked() {
        return is_locked;
    }

    public void setIs_locked(Integer is_locked) {
        this.is_locked = is_locked;
    }
}
