package org.ginryan.oemtab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerTitleStrip;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.os.Handler;

import org.ginryan.oemtab.databinding.ActivityMainBinding;

import java.util.ArrayList;

/**
 * demo case and test case.
 */
public class MainActivity extends AppCompatActivity {

    ActivityMainBinding activityMainBinding;
    Handler handler = new Handler();
    ArrayList<FillFragment> fillFragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = ActivityMainBinding.bind(getLayoutInflater().inflate(R.layout.activity_main, null));
        setContentView(activityMainBinding.getRoot());

        activityMainBinding.tablayout.addOnTabListener(itemIndex -> {
            activityMainBinding.logtext.setText("Selected Tab: " + itemIndex);
            activityMainBinding.viewPager.setCurrentItem(itemIndex);
        });

        int nTabViewsCount = activityMainBinding.tablayout.getNTabViewsCount();

        //Automation setSelectedTab() usage and a simple test.
        for (int i = 0; i < nTabViewsCount; i++) {
            int finalI = i;
            handler.postDelayed(() -> {
                activityMainBinding.tablayout.setSelectedTab(finalI);
            }, 1000L * i);
        }

        //viewpager test
        for (int i = 0; i < nTabViewsCount; i++) {
            fillFragments.add(new FillFragment().setTip("Page " + i));
        }
        activityMainBinding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                activityMainBinding.tablayout.setSelectedTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        activityMainBinding.viewPager.setAdapter(new FragmentStatePagerAdapter(
                getSupportFragmentManager(),
                FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            @Override
            public Fragment getItem(int position) {
                return fillFragments.get(position);
            }

            @Override
            public int getCount() {
                return fillFragments.size();
            }
        });

    }
}