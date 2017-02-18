package com.example.dscs;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.example.dscs.preview.PreviewFragment;

/**
 * Placeholder for the three main fragments.
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());
        ViewPager viewPager = (ViewPager) findViewById(R.id.activity_main_view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.setCurrentItem(1);
    }

    /**
     * Adapter for holding fragments in order.
     */
    public static class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new SettingsFragment();
                case 1:
                    return new StartFragment();
                case 2:
                    return new PreviewFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SETTINGS";
                case 1:
                    return "START";
                case 2:
                    return "TASKS";
            }
            return null;
        }
    }
}
