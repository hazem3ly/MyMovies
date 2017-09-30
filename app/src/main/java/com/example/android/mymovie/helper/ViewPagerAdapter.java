package com.example.android.mymovie.helper;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.example.android.mymovie.R;
import com.example.android.mymovie.fragments.MainFragment;

import static com.example.android.mymovie.helper.Utils.PAGES_COUNT;
import static com.example.android.mymovie.helper.Utils.POSITION;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private Context context;

    public ViewPagerAdapter(Context context, FragmentManager fm) {
        super(fm);

        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        Log.d("Position ", String.valueOf(position));
        MainFragment mainFragment = new MainFragment();
        Bundle data = new Bundle();
        data.putInt(POSITION, position);
        mainFragment.setArguments(data);
        return mainFragment;
    }

    @Override
    public int getCount() {
        return PAGES_COUNT;
    }
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return context.getString(R.string.popular);
            case 1:
                return context.getString(R.string.top_rated);
            case 2:
                return context.getString(R.string.favourite);
            default:
                return "";
        }
    }
}
