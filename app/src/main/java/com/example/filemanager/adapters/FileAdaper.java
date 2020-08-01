package com.example.filemanager.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.filemanager.models.FileInfo;
import com.example.filemanager.R;

import java.util.List;

public class FileAdaper extends BaseAdapter {
    private String TAG = this.getClass().getName();
    private List<FileInfo>  items;


    public FileAdaper(List<FileInfo> items) {
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void updateData(List<FileInfo> items){
        this.items = items;
        notifyDataSetChanged();
    }
    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_item, viewGroup, false);

        FileInfo file = items.get(position);

        TextView mTextViewNameFile = view.findViewById(R.id.tv_file_name);
        TextView mTextViewSizeFile = view.findViewById(R.id.tv_file_size);
        TextView mTextViewTimeFile = view.findViewById(R.id.tv_file_time);

        Log.d(TAG, "getView: " + file.getSize());
        mTextViewNameFile.setText(file.getName());
        mTextViewSizeFile.setText(file.getSize());
        mTextViewTimeFile.setText(file.getTime());

        return view;
    }
}
