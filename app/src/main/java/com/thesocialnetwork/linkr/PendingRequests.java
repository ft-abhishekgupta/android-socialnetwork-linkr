package com.thesocialnetwork.linkr;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class PendingRequests extends AppCompatActivity {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    PagerPendingRequests mPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_requests);
        setTitle("Pending Requests");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mViewPager=(ViewPager)findViewById(R.id.tab_pager2);
        mPager= new PagerPendingRequests(getSupportFragmentManager());
        mViewPager.setAdapter(mPager);

        mTabLayout=(TabLayout)findViewById(R.id.tab_layout2);
        mTabLayout.setupWithViewPager(mViewPager);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();
        Runtime.getRuntime().gc();
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
