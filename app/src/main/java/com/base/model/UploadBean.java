package com.base.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/11/8.
 */

public class UploadBean implements Serializable{
    private String filePath;//文件路径
    private String fileName;//文件名
    private String fileDesc;//文件描述
    private String fileType;//文件类型

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileDesc() {
        return fileDesc;
    }

    public void setFileDesc(String fileDesc) {
        this.fileDesc = fileDesc;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
}
