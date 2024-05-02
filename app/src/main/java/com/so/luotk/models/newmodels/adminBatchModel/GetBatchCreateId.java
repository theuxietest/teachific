package com.so.luotk.models.newmodels.adminBatchModel;

import java.util.ArrayList;

import lombok.Data;

@Data
public class GetBatchCreateId {
    public ArrayList<IdResult> result;

    private String success;

    private String status;

    public ArrayList<IdResult> getResult() {
        return result;
    }

    public void setResult(ArrayList<IdResult> result) {
        this.result = result;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static class IdResult {
        private String categoryName;
        private String id;
        private boolean isSlelected;

        public IdResult(String categoryName, String id) {
            this.categoryName = categoryName;
            this.id = id;
        }

        public IdResult() {
        }

        public String getCategoryName() {
            return categoryName;
        }

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public boolean isSlelected() {
            return isSlelected;
        }

        public void setSlelected(boolean slelected) {
            isSlelected = slelected;
        }
    }
}
