package com.ultimate.music.downloader.downloader;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import java.util.ArrayList;
import java.util.List;

public class SectionsPagerAdapter extends FragmentPagerAdapter {
    private static List<Fragment> TAB_FRAGMENT = new ArrayList();
    private static final String[] TAB_TITLES = {"Service 1" ,"Service 2", "Service 3"};
    private final Context mContext;

    public int getCount() {
        return 3;
    }

    public SectionsPagerAdapter(Context context, FragmentManager fragmentManager) {
        super(fragmentManager);
        this.mContext = context;
    }

    public void addFragments(List<Fragment> list) {
        TAB_FRAGMENT.clear();
        TAB_FRAGMENT.addAll(list);
        notifyDataSetChanged();
    }

    public Fragment getItem(int i) {
        return (Fragment) TAB_FRAGMENT.get(i);
    }

    public CharSequence getPageTitle(int i) {
        return TAB_TITLES[i];
    }
}
