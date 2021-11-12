package ru.fefu.activitytracker

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import ru.fefu.activitytracker.Screens.Tracker.ActivityTabs
import ru.fefu.activitytracker.Screens.Tracker.NewActivityFragment
import ru.fefu.activitytracker.Screens.Tracker.ProfileFragment
import ru.fefu.activitytracker.databinding.ActivityLayoutBinding

data class FragmentInfo (
    val buttonId: Int,
    val newInstance: () -> Fragment,
    val tag: String,
)

class Activity : AppCompatActivity() {
    private lateinit var binding: ActivityLayoutBinding

    private val fragments = listOf<FragmentInfo>(
        FragmentInfo(R.id.action_activity_tracker, ActivityTabs::newInstance, ActivityTabs.tag),
        FragmentInfo(R.id.action_profile, ProfileFragment::newInstance, ProfileFragment.tag)
    )

    private fun replaceFragment(buttonId: Int) {
        val active = supportFragmentManager.fragments.firstOrNull{!it.isHidden}
        val fragmentToShowInfo = fragments.first { it.buttonId == buttonId }
        val fragmentToShow = supportFragmentManager.findFragmentByTag(fragmentToShowInfo.tag)

        if (active == fragmentToShow) {
            return
        }

        if (active != null) {
            supportFragmentManager.beginTransaction().apply {
                hide(active)
                commit()
            }
        }

        if (fragmentToShow != null) {
            supportFragmentManager.beginTransaction().apply {
                show(fragmentToShow)
                commit()
            }
        }

        else {
            supportFragmentManager.beginTransaction().apply {
                add(R.id.fragment_container_view, fragmentToShowInfo.newInstance(), fragmentToShowInfo.tag)
                commit()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLayoutBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().apply {
                add(
                    R.id.fragment_container_view,
                    ActivityTabs.newInstance(),
                    ActivityTabs.tag,
                )
                commit()
            }
        }
        binding.bottomNavigationView.setOnNavigationItemSelectedListener {
            replaceFragment(it.itemId)
            true
        }
    }

    override fun onBackPressed() {
        val active = supportFragmentManager.fragments.firstOrNull{!it.isHidden}!!
        val childManager = active.childFragmentManager

        if (binding.bottomNavigationView.visibility == View.GONE) {
            if (childManager.findFragmentByTag(NewActivityFragment.tag)?.isVisible == true)
                binding.bottomNavigationView.visibility = View.VISIBLE
        }
        if (supportFragmentManager.backStackEntryCount != 0) {
            supportFragmentManager.popBackStack()
        }
        else if (childManager.backStackEntryCount != 0) {
            childManager.popBackStack()
        }
        else {
            super.onBackPressed()
        }
    }
}