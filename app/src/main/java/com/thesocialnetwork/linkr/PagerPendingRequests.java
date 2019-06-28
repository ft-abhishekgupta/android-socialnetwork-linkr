package com.thesocialnetwork.linkr;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by corei3 on 06-05-2018.
 */

public class PagerPendingRequests extends FragmentPagerAdapter {

    public PagerPendingRequests(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
        case 0:
            TabRequestSent tabRequestSent = new TabRequestSent();
            return tabRequestSent;
        case 1:
            TabRequestRecieved tabRequestRecieved = new TabRequestRecieved();
            return tabRequestRecieved;
        default:
            return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        if (position == 0)
            return "Sent";
        else if (position == 1)
            return "Recieved";
        else
            return null;
    }
}
