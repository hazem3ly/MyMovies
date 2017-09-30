package com.example.android.mymovie.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.android.mymovie.R;
import com.example.android.mymovie.fragments.DetailsFragment;
import com.example.android.mymovie.helper.Movie;
import com.example.android.mymovie.helper.OnMovieSelectListener;
import com.example.android.mymovie.helper.ViewPagerAdapter;

import static com.example.android.mymovie.fragments.MainFragment.pageChangeListener;
import static com.example.android.mymovie.helper.Utils.MOVIE_DETAILS;

public class MainActivity extends AppCompatActivity implements OnMovieSelectListener {

    private ViewPager viewPager;
    private android.support.v7.app.ActionBar actionBar;
    public static OnMovieSelectListener movieSelectListener;
    private boolean twoPaneUi = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        movieSelectListener = this;

        twoPaneUi = findViewById(R.id.dFragment) != null;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.windowBackground));
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.popular));
        }


        viewPager = (ViewPager) findViewById(R.id.view_pager);

        ViewPagerAdapter adapter = new ViewPagerAdapter(this, getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        if (actionBar != null) actionBar.setTitle(getString(R.string.popular));
                        if (pageChangeListener != null) {
                            pageChangeListener.OnPageChange(0);
                        }
                        break;
                    case 1:
                        if (actionBar != null)
                            actionBar.setTitle(getString(R.string.top_rated));
                        if (pageChangeListener != null) {
                            pageChangeListener.OnPageChange(1);
                        }
                        break;
                    case 2:
                        if (actionBar != null)
                            actionBar.setTitle(getString(R.string.favourite));
                        if (pageChangeListener != null) {
                            pageChangeListener.OnPageChange(2);
                        }
                        break;
                    default:
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.popular:
                viewPager.setCurrentItem(0);
                if (actionBar != null) actionBar.setTitle(getString(R.string.popular));
                break;
            case R.id.top_rate:
                viewPager.setCurrentItem(1);
                if (actionBar != null) actionBar.setTitle(getString(R.string.top_rated));
                break;
            case R.id.fav:
                viewPager.setCurrentItem(2);
                if (actionBar != null) actionBar.setTitle(getString(R.string.favourite));
                break;
            default:
                break;
        }

        return true;
    }

    @Override
    public void OnMovieSelected(Movie movie) {
        if (!twoPaneUi)
            startActivity(new Intent(this, DetailsActivity.class).putExtra(MOVIE_DETAILS, movie));
        else {
            Bundle data = new Bundle();
            data.putParcelable(MOVIE_DETAILS, movie);
            DetailsFragment detailsFragment = new DetailsFragment();
            detailsFragment.setArguments(data);

            getSupportFragmentManager().beginTransaction().replace(R.id.dFragment, detailsFragment).commit();

        }

    }
}
