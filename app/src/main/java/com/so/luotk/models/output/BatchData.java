package com.so.luotk.models.output;

import java.io.Serializable;

import lombok.Data;

@Data
public class BatchData implements Serializable {
    private String id;
    private String batchName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBatchName() {
        return batchName;
    }

    public void setBatchName(String batchName) {
        this.batchName = batchName;
    }
}
