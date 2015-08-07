package com.example.admin.rxjavatestapplication;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.appunite.rx.functions.BothParams;
import com.example.admin.rxjavatestapplication.design.TabFragment;
import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.functions.Action1;

public class MainActivity extends BaseActivity {

    @InjectView(R.id.root_coordinator)
    CoordinatorLayout mCoordinator;
    @InjectView(R.id.collapsing_toolbar_layout)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @InjectView(R.id.fab)
    FloatingActionButton mFloatingActionButton;
    @InjectView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @InjectView(R.id.app_bar)
    Toolbar mToolbar;
    @InjectView(R.id.tab_layout)
    TabLayout mTabLayout;
    @InjectView(R.id.view_pager)
    ViewPager mPager;


    private ActionBarDrawerToggle mDrawerToggle;
    private YourPagerAdapter yourPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);

        setSupportActionBar(mToolbar);
//        mToolbar.setPadding(0, getStatusBarHeight(), 0, 0);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.open, R.string.close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        yourPagerAdapter = new YourPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(yourPagerAdapter);
//        Notice how the Tab Layout links with the Pager Adapter
        mTabLayout.setTabsFromPagerAdapter(yourPagerAdapter);

//        Notice how The Tab Layout adn View Pager object are linked
        mTabLayout.setupWithViewPager(mPager);
        mPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));

        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(mCoordinator, "FAB Clicked", Snackbar.LENGTH_SHORT).setAction("DISMISS", null).show();
            }
        });

        mCollapsingToolbarLayout.setTitle(getResources().getString(R.string.app_name));
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    class YourPagerAdapter extends FragmentStatePagerAdapter {

        public YourPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            TabFragment myFragment = TabFragment.newInstance(position);
            return myFragment;
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Tab " + (position + 1);
        }
    }

    @dagger.Module(
            injects = {
                    RetrofitPresenter.class,
                    MainActivity.class

            },
            addsTo = MainApplication.Module.class
    )
    class Module {
    }
}