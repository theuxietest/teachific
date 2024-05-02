
package com.so.luotk.models.newmodels.study;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum_ {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("content")
    @Expose
    private List<String> content = null;
    @SerializedName("fk_batchId")
    @Expose
    private Integer fkBatchId;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("folderId")
    @Expose
    private Object folderId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getContent() {
        return content;
    }

    public void setContent(List<String> content) {
        this.content = content;
    }

    public Integer getFkBatchId() {
        return fkBatchId;
    }

    public void setFkBatchId(Integer fkBatchId) {
        this.fkBatchId = fkBatchId;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Object getFolderId() {
        return folderId;
    }

    public void setFolderId(Object folderId) {
        this.folderId = folderId;
    }

}
