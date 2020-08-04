package com.example.filemanager.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.filemanager.R;
import com.example.filemanager.fragments.ShowFileFragment;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private final int REQUEST_CODE_STORAGE = 1234;
    private final int REQUEST_CODE_WRITE_STORAGE = 4321;

    private String TAG = this.getClass().getName();
    private ImageView mImageViewFolder;
    private ImageView mImageViewSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // request permission read and write storage
      //  requestPermission();

        // find view and set click
        findView();
        setClick();

        // init fragment
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction f = fm.beginTransaction();
        f.replace(R.id.fl_activity, new ShowFileFragment());
        f.commit();

        Log.d(TAG, "onCreate: continue after request");
    }

    private void startActivityAfterRequestPermission(){
        // find view and set click
        findView();
        setClick();

        // init fragment
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction f = fm.beginTransaction();
        f.replace(R.id.fl_activity, new ShowFileFragment());
        f.commit();
    }
    @Override
    public void onBackPressed() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        boolean handled = false;

        for(Fragment f : fragments){
            if(f instanceof ShowFileFragment){
                handled = ((ShowFileFragment) f).onBackPress();
                if(handled) break;;
            }
        }

        if(!handled){
            super.onBackPressed();
        }
    }


    @Override
    public void onClick(View v){
        if(v.getId() == R.id.imv_folder){
            Toast.makeText(this, "click folder", Toast.LENGTH_SHORT).show();
        }
        else if(v.getId() == R.id.imv_search){
            Toast.makeText(this, "click search", Toast.LENGTH_SHORT).show();
        }
    }

    private void findView(){
        mImageViewFolder = findViewById(R.id.imv_folder);
        mImageViewSearch = findViewById(R.id.imv_search);
    }

    private void setClick(){
        mImageViewSearch.setOnClickListener(this);
        mImageViewFolder.setOnClickListener(this);
    }

    private void requestPermission(){
        Log.d(TAG, "requestPermission: start request");
        int permission_write  = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission_read  = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        if(permission_read == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_STORAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CODE_STORAGE){
           boolean ok = true;
           for(int value : grantResults){
               if(value == PackageManager.PERMISSION_DENIED){
                   ok = false;
                   break;
               }
           }

           if(ok){
               startActivityAfterRequestPermission();
           }
        }
    }

    private void closeNow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
        {
            finishAffinity();
        }

        else
        {
            finish();
        }
    }
}
