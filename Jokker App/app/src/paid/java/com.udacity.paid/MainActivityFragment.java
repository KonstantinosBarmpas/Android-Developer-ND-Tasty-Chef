package com.udacity.paid;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import udacity.gradle.builditbigger.R;

public class MainActivityFragment extends Fragment{
    public MainActivityFragment(){}

    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        View root=inflater.inflate(R.layout.fragment_main,container,false);
        return root;
    }
}

