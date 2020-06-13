package com.example.android.mymovie.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.android.mymovie.R;
import com.example.android.mymovie.fragments.DetailsFragment;

import static com.example.android.mymovie.helper.Utils.MOVIE_DETAILS;

public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_details);



        if (getIntent().hasExtra(MOVIE_DETAILS)) {

            Bundle data = new Bundle();
            data.putParcelable(MOVIE_DETAILS, getIntent().getParcelableExtra(MOVIE_DETAILS));

            DetailsFragment detailsFragment = new DetailsFragment();
            detailsFragment.setArguments(data);

            if (savedInstanceState == null)
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.dFragment, detailsFragment).commit();
        } else finish();


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
