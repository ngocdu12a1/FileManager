package com.example.filemanager.fragments;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.filemanager.models.FileInfo;
import com.example.filemanager.R;
import com.example.filemanager.adapters.FileAdaper;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ShowFileFragment extends Fragment implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    private String TAG = this.getClass().getName();

    private View view;
    private String rootPath;

    private FileAdaper mFileAdaper;
    private List<FileInfo> items;
    private File rootFile;

    private ListView mListView;
    private TextView mTextViewCurrentFolder;
    private ImageView mImageViewPopupMenu;
    private View viewClick;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_showfile, container, false);
        findView();

        // get root path Internal storage
        rootFile = Environment.getExternalStorageDirectory();
        rootPath = rootFile.getAbsolutePath();
        items = getSubFolder(rootPath);

        // set text view current folder
        mTextViewCurrentFolder.setText(rootPath);

        // set items list view
        mFileAdaper = new FileAdaper(items);
        mListView.setAdapter(mFileAdaper);

        // set click list view
        setOnClickListView();
        setOnLongCickListView();

        return view;
    }


    public boolean onBackPress(){
        if(rootPath.compareTo("/storage/emulated/0") == 0){
            return false;
        }
        else {
            rootPath = rootFile.getParent();
            rootFile = new File(rootPath);
            items = getSubFolder(rootPath);
            mFileAdaper.updateData(items);
            mTextViewCurrentFolder.setText(rootPath);
            return true;
        }
    }

    private void findView(){
        mListView = view.findViewById(R.id.lv_main);
        mTextViewCurrentFolder = view.findViewById(R.id.tv_current_folder);
        mImageViewPopupMenu = view.findViewById(R.id.imv_popup_menu);

        mImageViewPopupMenu.setOnClickListener(this);
    }

    private void setOnClickListView(){
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(), "short click", Toast.LENGTH_SHORT).show();
                String nameFolder = items.get(position).getName();
                rootFile = items.get(position).getFile();

                if(rootFile.isDirectory()){
                    rootPath = rootPath + "/" + nameFolder;
                    mTextViewCurrentFolder.setText(rootPath);
                    items = getSubFolder(rootPath);
                    mFileAdaper.updateData(items);
                }
                else {
                    Log.d(TAG, "you click file " + nameFolder);
                }
            }
        });
    }

    private void setOnLongCickListView(){
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(), "long click", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.imv_popup_menu){
            showPopupMenu(v);
        }
    }

    private List<FileInfo> getSubFolder(String rootPath){
        List<FileInfo> items = new ArrayList<>();

        File root = new File(rootPath);
        File[] files = root.listFiles();
        Log.d(TAG, "getSubFolder: " + rootPath);
        Log.d(TAG, "getSubFolder: " + files.length);
        for(File file:files){
            FileInfo fileInfo = new FileInfo(file);
            items.add(fileInfo);
        }

        return items;
    }

    private void showPopupMenu(View v){
        viewClick = v;
        PopupMenu popup = new PopupMenu(getContext(), v);
        popup.inflate(R.menu.menu_popup);
        popup.setOnMenuItemClickListener(this);
        popup.show();
    }

    private void showPopupMenuSort(View v){
        PopupMenu popup = new PopupMenu(getContext(), v);
        popup.inflate(R.menu.menu_popup_sort);
        popup.setOnMenuItemClickListener(this);
        popup.show();
    }
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_popup_sort_item:
                showPopupMenuSort(viewClick);
                break;
            case R.id.menu_popup_create:
                break;
            case R.id.menu_popup_sort_name:
                sortItem(R.id.menu_popup_sort_name);
                break;
            case R.id.menu_popup_sort_date:
                sortItem(R.id.menu_popup_sort_date);
                break;
        }
        return true;
    }

    private void sortItem(int cmd){
        Toast.makeText(getContext(), "sort by name", Toast.LENGTH_SHORT).show();
        if(cmd == R.id.menu_popup_sort_name){
            class CustomComparator implements Comparator<FileInfo> {
                @Override
                public int compare(FileInfo o1, FileInfo o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            }

            Collections.sort(items, new CustomComparator());
            mFileAdaper.updateData(items);
        }
        else if(cmd == R.id.menu_popup_sort_date){
            class CustomComparator implements Comparator<FileInfo> {
                @Override
                public int compare(FileInfo o1, FileInfo o2) {
                    int delta = (int) (o1.getTime_raw() - o2.getTime_raw());
                    return delta;
                }
            }

            Collections.sort(items, new CustomComparator());
            mFileAdaper.updateData(items);
        }
    }
}
