package org.gag.appdriver.Utilities

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class FragmentAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    val lafragments : List<Fragment> = arrayListOf()

    override fun createFragment(p0: Int): Fragment {

        return lafragments.get(p0)
    }

    override fun getItemCount(): Int {
        return lafragments.size
    }
}