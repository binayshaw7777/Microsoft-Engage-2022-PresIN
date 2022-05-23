package com.geekym.face_recognition_engage.HomeFragments.Tools.PDFs;

public class PDFsModel {
    String filename, fileurl, author;

    PDFsModel() {}

    public PDFsModel(String filename, String fileurl,String author) {
        this.filename = filename;
        this.fileurl = fileurl;
        this.author = author;
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

    public String getAuthor() {return author;}

    public void setAuthor(String author) {this.author = author;}
}
