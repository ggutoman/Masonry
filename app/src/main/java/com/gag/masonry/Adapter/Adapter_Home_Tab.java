package com.gag.masonry.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import java.util.List;

public class Adapter_Home_Tab extends FragmentStateAdapter {

    private List<Fragment> laFragments;

    public Adapter_Home_Tab(@NonNull FragmentActivity fragmentActivity, List<Fragment> faFragments) {
        super(fragmentActivity);

        this.laFragments = faFragments;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return laFragments.get(position);
    }

    @Override
    public int getItemCount() {
        return laFragments.size();
    }
}
