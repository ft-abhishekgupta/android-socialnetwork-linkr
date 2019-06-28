package com.thesocialnetwork.linkr;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by corei3 on 02-05-2018.
 */

public class PagerContacts extends FragmentPagerAdapter {

    public PagerContacts(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
        case 0:
            TabFriendList tabFriendList = new TabFriendList();
            return tabFriendList;
        case 1:
            TabUserList tabUserList = new TabUserList();
            return tabUserList;
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
            return "Friends";
        else if (position == 1)
            return "Users";
        else
            return null;
    }
}