package org.ginryan.oemtab;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;

import org.ginryan.oemtab.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding activityMainBinding;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = ActivityMainBinding.bind(getLayoutInflater().inflate(R.layout.activity_main, null));
        setContentView(activityMainBinding.getRoot());

        activityMainBinding.tablayout.addOnTabListener(itemIndex -> {
            activityMainBinding.logtext.setText("Selected Tab: " + itemIndex);
        });
        int nTabViewsCount = activityMainBinding.tablayout.getNTabViewsCount();
        //setSelectedTab() usage and a simple test.
        for (int i = 0; i < nTabViewsCount; i++) {
            int finalI = i;
            handler.postDelayed(() -> {
                activityMainBinding.tablayout.setSelectedTab(finalI);
            }, 1000L * i);
        }
    }
}