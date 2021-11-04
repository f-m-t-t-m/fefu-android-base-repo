package ru.fefu.activitytracker.Adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import ru.fefu.activitytracker.Screens.Tracker.ActivityMyTrackerFragment
import ru.fefu.activitytracker.Screens.Tracker.ActivityOtherTrackerFragment
import ru.fefu.activitytracker.Screens.Tracker.ActivityTrackerFragment

class PagerAdapter(fragment: ActivityTrackerFragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return if (position == 0) {
            ActivityMyTrackerFragment()
        } else {
            ActivityOtherTrackerFragment()
        }
    }
}