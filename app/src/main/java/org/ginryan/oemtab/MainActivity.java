package org.ginryan.oemtab;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import org.ginryan.oemtab.databinding.ActivityMainBinding;
import org.ginryan.oemtab.tablayout.NTabLayout;
import org.ginryan.oemtab.tablayout.NTabView;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding activityMainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = ActivityMainBinding.bind(getLayoutInflater().inflate(R.layout.activity_main, null));
        setContentView(activityMainBinding.getRoot());

    }


}