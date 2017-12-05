package com.base.httpmvp.mode;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/11/8.
 */

public class UploadData implements Serializable{
    private String fileName;
    private String fileUrl;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
}
