package com.so.luotk.models.output;

import lombok.Data;

@Data
public class GetStudyMaterialResult {
    private StudyMaterialFolders folders;
    private StudyMaterialFiles files;

    public StudyMaterialFolders getFolders() {
        return folders;
    }

    public void setFolders(StudyMaterialFolders folders) {
        this.folders = folders;
    }

    public StudyMaterialFiles getFiles() {
        return files;
    }

    public void setFiles(StudyMaterialFiles files) {
        this.files = files;
    }
}
