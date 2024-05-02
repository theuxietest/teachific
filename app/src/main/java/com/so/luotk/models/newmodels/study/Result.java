
package com.so.luotk.models.newmodels.study;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Result {

    @SerializedName("folders")
    @Expose
    private Folders folders;
    @SerializedName("files")
    @Expose
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
