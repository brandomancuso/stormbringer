package com.apps.brando.stormbringer;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class FragmentAdapter extends FragmentPagerAdapter {
    private Context context;

    public FragmentAdapter(Context context, FragmentManager fm){
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch(position){
            case 0:
                return context.getString(R.string.page_1);
            case 1:
                return context.getString(R.string.page_2);
            case 2:
                return context.getString(R.string.page_3);
            case 3:
                return context.getString(R.string.page_4);
            case 4:
                return context.getString(R.string.page_5);
            default:
                return null;

        }
    }

    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0:
                return new GeneralFragment();
            case 1:
                return new StatsFragment();
            case 2:
                return new SkillsFragment();
            case 3:
                return new MagicFragment();
            case 4:
                return new BackgroundFragment();
            default:
                return null;
        }
    }
}
