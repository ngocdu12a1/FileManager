package com.example.filemanager.models;

import android.widget.ImageView;

import java.io.File;
import java.text.SimpleDateFormat;
import com.example.filemanager.R;

public class FileInfo {
    private File file;
    private String name;
    private String size;
    private String time;
    private String path;
    private int  icon;
    private long time_raw;

    public FileInfo(File file) {
       this.file = file;
       this.name = file.getName();

       SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
       this.time = sdf.format(file.lastModified());

       this.path = file.getAbsolutePath();
       this.size = getSizeFile(file);
       this.time_raw = file.lastModified();

       if(file.isDirectory())
           this.icon = R.mipmap.file_icon_folder;
       else
           this.icon = getIdFileIcon(this.name);
    }

    private int  getIdFileIcon(String nameFile){
        String suffix = "folder";

        if(nameFile.lastIndexOf(".") != -1)
            suffix = nameFile.substring(nameFile.lastIndexOf("."));


        switch (suffix){
            case ".pdf":
                return R.mipmap.file_icon_pdf;
            case ".audio":
                return R.mipmap.file_icon_audio;
            case ".jpg":
            case ".png":
                return R.mipmap.file_icon_picture;
            case ".rar":
                return R.mipmap.file_icon_rar;
            case ".mp4":
                return R.mipmap.file_icon_video;
            case ".doc":
            case ".docx":
                return R.mipmap.file_icon_doc;
            case ".xls":
            case ".xlsx":
                return R.mipmap.file_icon_xls;
            case ".txt":
                return R.mipmap.file_icon_txt;
            case ".zip":
                return R.mipmap.file_icon_zip;
            case ".xml":
                return R.mipmap.file_icon_xml;
            default:
                return R.mipmap.file_icon_default;
        }
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

    public int  getIcon() {
        return icon;
    }

    public long getTime_raw() {
        return time_raw;
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

    public void setIcon(int  icon) {
        this.icon = icon;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setTime_raw(long time_raw) {
        this.time_raw = time_raw;
    }
}
