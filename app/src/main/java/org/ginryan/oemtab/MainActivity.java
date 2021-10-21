package org.ginryan.oemtab;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import org.ginryan.oemtab.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding activityMainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = ActivityMainBinding.bind(getLayoutInflater().inflate(R.layout.activity_main, null));
        setContentView(activityMainBinding.getRoot());

        activityMainBinding.tablayout.addOnTabListener(itemIndex -> {
            activityMainBinding.logtext.setText("Selected Tab: " + itemIndex);
        });
    }


}