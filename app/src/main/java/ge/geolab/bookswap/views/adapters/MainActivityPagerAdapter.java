package ge.geolab.bookswap.views.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;

import ge.geolab.bookswap.fragments.BookSwapFragment;
import ge.geolab.bookswap.utils.TypeFaceSpan;

/**
 * Created by dalkh on 25-Dec-15.
 */
public class MainActivityPagerAdapter extends FragmentStatePagerAdapter {
    private int categoryId;
    public MainActivityPagerAdapter(FragmentManager fm,int categoryId) {
        super(fm);
        this.categoryId=categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a BookSwapFragment (defined as a static inner class below).
        return BookSwapFragment.newInstance(position + 1,this.categoryId);
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        switch (position) {
            case 0:
                return "ვცვლი";
            case 1:
                return "ვეძებ";
        }
        return null;
    }
}
