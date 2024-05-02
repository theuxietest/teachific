package com.so.luotk.models.output;

import lombok.Data;

@Data
public class GetBatchFolderVideosResult {
    private Folders folders;
    private Files files;

    public Folders getFolders() {
        return folders;
    }

    public void setFolders(Folders folders) {
        this.folders = folders;
    }

    public Files getFiles() {
        return files;
    }

    public void setFiles(Files files) {
        this.files = files;
    }
}
