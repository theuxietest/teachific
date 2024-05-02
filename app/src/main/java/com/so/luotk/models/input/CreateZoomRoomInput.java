package com.so.luotk.models.input;

import lombok.Data;

@Data
public class CreateZoomRoomInput {
    private String batchId;
    private String type;
    private String zoomRoomId;
    private String zoomRoomPassword;

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getZoomRoomId() {
        return zoomRoomId;
    }

    public void setZoomRoomId(String zoomRoomId) {
        this.zoomRoomId = zoomRoomId;
    }

    public String getZoomRoomPassword() {
        return zoomRoomPassword;
    }

    public void setZoomRoomPassword(String zoomRoomPassword) {
        this.zoomRoomPassword = zoomRoomPassword;
    }
}
