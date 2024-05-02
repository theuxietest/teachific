package com.so.luotk.models.newmodels.adminBatchModel;

import java.util.ArrayList;

import lombok.Data;

@Data
public class GetSubjectIdResponse {
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
        private String subjectName;
        private String id;
        private boolean isSlelected;

        public IdResult(String subjectName, String id) {
            this.subjectName = subjectName;
            this.id = id;
        }

        public IdResult() {
        }

        public String getSubjectName() {
            return subjectName;
        }

        public void setSubjectName(String subjectName) {
            this.subjectName = subjectName;
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
