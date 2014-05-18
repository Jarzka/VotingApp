package org.voimala.votingapp.fragments.adapters;

/* This class has been created with the help of the following AndroidHive tutorial:
 * http://www.androidhive.info/2013/10/android-tab-layout-with-swipeable-views-1/
 */

import java.util.ArrayList;

import org.voimala.votingapp.fragments.ClosedVotingsFragment;
import org.voimala.votingapp.fragments.OpenVotingsFragment;
import org.voimala.votingapp.fragments.UpcomingVotingsFragment;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
 
public class TabsPagerAdapter extends FragmentPagerAdapter {
    private ArrayList<Fragment> fragments = new ArrayList<Fragment>();
    
    public TabsPagerAdapter(final FragmentManager fragmentManager) {
        super(fragmentManager);
        initializeFragments();
    }
 
    private void initializeFragments() {
        fragments.add(new OpenVotingsFragment());
        fragments.add(new ClosedVotingsFragment());
        fragments.add(new UpcomingVotingsFragment());
    }

    @Override
    /** @return Fragment The corresponding tab Fragment.*/
    public Fragment getItem(final int index) {
        return fragments.get(index);
    }
 
    @Override
    /** @return int The number of tabs. */
    public int getCount() {
        return fragments.size();
    }
 
}