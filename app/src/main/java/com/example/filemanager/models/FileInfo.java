package com.example.filemanager.models;

import android.widget.ImageView;

import java.io.File;
import java.text.SimpleDateFormat;

public class FileInfo {
    private File file;
    private String name;
    private String size;
    private String time;
    private String path;
    private ImageView icon;

    public FileInfo(File file) {
       this.file = file;
       this.name = file.getName();

       SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
       this.time = sdf.format(file.lastModified());

       this.path = file.getAbsolutePath();
       this.size = getSizeFile(file);
    }



    private String getSizeFile(File file){
        long size = file.length();
        String res;
        res = null;
        if(size < 1024){
            res = String.format("%d bytes", size);
        }
        else if(size < 1024 * 1024){
            res = String.format("%d KB", size/1024);
        }
        else {
            res = String.format("%d MB", size/(1024 * 1024));
        }
        return res;
    }

    public File getFile() {
        return file;
    }

    public String getName() {
        return name;
    }

    public String getSize() {
        return size;
    }

    public String getTime() {
        return time;
    }

    public String getPath() {
        return path;
    }

    public ImageView getIcon() {
        return icon;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setName(String name) {
        this.name = name;
    }


    public void setPath(String path) {
        this.path = path;
    }

    public void setIcon(ImageView icon) {
        this.icon = icon;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void setTime(String time) {
        this.time = time;
    }


}
