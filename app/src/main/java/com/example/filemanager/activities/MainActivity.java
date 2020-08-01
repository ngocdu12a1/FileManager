package com.example.filemanager.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.filemanager.R;
import com.example.filemanager.fragments.ShowFileFragment;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private String TAG = this.getClass().getName();
    private ImageView mImageViewFolder;
    private ImageView mImageViewSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
}
