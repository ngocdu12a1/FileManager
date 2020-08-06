package com.example.filemanager.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.filemanager.models.FileInfo;
import com.example.filemanager.R;
import com.example.filemanager.adapters.FileAdaper;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class ShowFileFragment extends Fragment implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    private String TAG = this.getClass().getName();

    private View view;
    private String rootPath;

    private FileAdaper mFileAdapter;
    private List<FileInfo> items;
    private File rootFile;
    private EditText input;
    private int longClickPosition;

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
        mFileAdapter = new FileAdaper(items);
        mListView.setAdapter(mFileAdapter);

        // set click list view
        setOnClickListView();
        setOnLongClickListView();

        // register context menu
        registerForContextMenu(mListView);
        return view;
    }

    // return parent folder when click back button
    public boolean onBackPress(){
        if(rootPath.compareTo("/storage/emulated/0") == 0){
            return false;
        }
        else {
            rootPath = rootFile.getParent();
            rootFile = new File(rootPath);
            items = getSubFolder(rootPath);
            mFileAdapter.updateData(items);
            mTextViewCurrentFolder.setText(rootPath);
            return true;
        }
    }

    // bind view xml with java code
    private void findView(){
        mListView = view.findViewById(R.id.lv_main);
        mTextViewCurrentFolder = view.findViewById(R.id.tv_current_folder);
        mImageViewPopupMenu = view.findViewById(R.id.imv_popup_menu);

        mImageViewPopupMenu.setOnClickListener(this);
    }

    // implement View.OnClickListener's method
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.imv_popup_menu){
            showPopupMenu(v);
        }
    }

    // set event short click list view
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
                    mFileAdapter.updateData(items);
                }
                else {
                    // Get URI and MIME type of file
                    Uri uri = Uri.parse("file://"+rootFile.getAbsolutePath());
                    String mime = URLConnection.guessContentTypeFromName(rootFile.getName());

                    // Open file with user selected app
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri, mime);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intent);
                }
            }
        });
    }


    /*
    * Handle long click item and show context menu
    */

    // set event long click list view
    private void setOnLongClickListView(){
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                longClickPosition = position;
                return false;
            }
        });
    }

    // create context menu for items in list view
    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        File item = items.get((int)longClickPosition).getFile();
        if(item.isDirectory()){
            super.onCreateContextMenu(menu, v, menuInfo);
            MenuInflater inflater = Objects.requireNonNull(getActivity()).getMenuInflater();
            inflater.inflate(R.menu.menu_folder_context, menu);
        }
        else {
            super.onCreateContextMenu(menu, v, menuInfo);
            MenuInflater inflater = Objects.requireNonNull(getActivity()).getMenuInflater();
            inflater.inflate(R.menu.menu_file_context, menu);
        }
    }

    // handle item click in context menu of list view
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_context_delete_file:
            case R.id.menu_context_delete_folder:
                if(items.get(longClickPosition).getFile().delete()){
                    Toast.makeText(getContext(), "delete file successfully", Toast.LENGTH_SHORT).show();
                    items.remove(longClickPosition);
                    mFileAdapter.updateData(items);
                }
                else {
                    Toast.makeText(getContext(), "delete file failed", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.menu_context_rename_file:
            case R.id.menu_context_rename_folder:
                showRenameAlertDialog();
                break;

            case R.id.menu_context_send_file:
                sendFile();
                break;
        }
        return  super.onContextItemSelected(item);
    }

    // show alert dialog to rename folder
    private void showRenameAlertDialog(){
        // set up input new name
        input = new EditText(getContext());
        final AlertDialog.Builder inputNewName = new AlertDialog.Builder(getContext());
        inputNewName.setTitle("Rename");
        inputNewName.setCancelable(true);
        inputNewName.setMessage("Enter new name:");
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        inputNewName.setView(input);

        // Set up the buttons
        inputNewName.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String newName = input.getText().toString();

                String oldPath = items.get(longClickPosition).getPath();
                String newPath = rootPath + "/" + newName;

                File oldFile = new File(oldPath);
                File newFile = new File(newPath);

                if(oldFile.renameTo(newFile)){
                    Toast.makeText(getContext(), "Rename successfully", Toast.LENGTH_SHORT).show();
                    items = getSubFolder(rootPath);
                    mFileAdapter.updateData(items);
                }
                else {
                    Toast.makeText(getContext(), "Rename failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
        inputNewName.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        inputNewName.show();
    }

    // send file using intent
    private void sendFile(){
        Intent intentShareFile = new Intent(Intent.ACTION_SEND);
        File file = items.get(longClickPosition).getFile();

        intentShareFile.setType(URLConnection.guessContentTypeFromName(file.getName()));
        intentShareFile.putExtra(Intent.EXTRA_STREAM,
                Uri.parse("file://"+file.getAbsolutePath()));

        startActivity(Intent.createChooser(intentShareFile, "Share File"));
    }


    /*
    * show popup menu and handle event
    */

    // show popup menu (sort and create folder)
    private void showPopupMenu(View v){
        viewClick = v;
        PopupMenu popup = new PopupMenu(getContext(), v);
        popup.inflate(R.menu.menu_popup);
        popup.setOnMenuItemClickListener(this);
        popup.show();
    }

    // show detail popup menu sort
    private void showPopupMenuSort(View v){
        PopupMenu popup = new PopupMenu(getContext(), v);
        popup.inflate(R.menu.menu_popup_sort);
        popup.setOnMenuItemClickListener(this);
        popup.show();
    }

    // sort item order by name or date
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
            mFileAdapter.updateData(items);
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
            mFileAdapter.updateData(items);
        }
    }

    // handle popup menu click
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_popup_sort_item:
                showPopupMenuSort(viewClick);
                break;
            case R.id.menu_popup_create:
                showCreateFolderAlertDialog();
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

    // show alert dialog to create folder
    private void showCreateFolderAlertDialog(){
        // set up input new name
        input = new EditText(getContext());
        final AlertDialog.Builder inputNewFolder = new AlertDialog.Builder(getContext());
        inputNewFolder.setTitle("Create Folder");
        inputNewFolder.setCancelable(true);
        inputNewFolder.setMessage("Enter name new folder:");
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        inputNewFolder.setView(input);

        // Set up the buttons
        inputNewFolder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newFolder = input.getText().toString();
                File directory  = new File(rootPath, newFolder);
                if(directory.mkdirs()){
                    Toast.makeText(getContext(), "make dir successfully", Toast.LENGTH_SHORT).show();
                    items = getSubFolder(rootPath);
                    mFileAdapter.updateData(items);
                }
                else {
                    Toast.makeText(getContext(), "make dir failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
        inputNewFolder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        inputNewFolder.show();
    }



    // get all subfolder of root path
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
}
