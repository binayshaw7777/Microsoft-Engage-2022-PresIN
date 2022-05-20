package com.geekym.face_recognition_engage.HomeFragments.Tools.PDFs;

public class PDFsModel {
    String filename, fileurl;

    PDFsModel() {}

    public PDFsModel(String filename, String fileurl) {
        this.filename = filename;
        this.fileurl = fileurl;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFileurl() {
        return fileurl;
    }

    public void setFileurl(String fileurl) {
        this.fileurl = fileurl;
    }

}
