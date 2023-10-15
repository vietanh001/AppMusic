package com.example.audiostreaming.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.audiostreaming.Fragment.FragmentAccount;
import com.example.audiostreaming.Fragment.FragmentHome;
import com.example.audiostreaming.Fragment.FragmentMusic;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new FragmentHome();
            case 1:
                return new FragmentMusic();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
