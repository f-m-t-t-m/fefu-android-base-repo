package ru.fefu.activitytracker.Screens.Tracker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import ru.fefu.activitytracker.R
import ru.fefu.activitytracker.Screens.Tracker.ActivityMyTrackerFragment
import ru.fefu.activitytracker.Screens.Tracker.ActivityTabs
import ru.fefu.activitytracker.Screens.Tracker.NewActivityFragment
import ru.fefu.activitytracker.Screens.Tracker.ProfileFragment
import ru.fefu.activitytracker.databinding.ActivityLayoutBinding
import java.io.File

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

//    override fun onNewIntent(intent: Intent?) {
//        super.onNewIntent(intent)
//        setIntent(intent)
//
//    }

    override fun onBackPressed() {
        val active = supportFragmentManager.fragments.firstOrNull{!it.isHidden}!!
        val childManager = active.childFragmentManager

        if (binding.bottomNavigationView.visibility == View.GONE) {
                binding.bottomNavigationView.visibility = View.VISIBLE
        }

        if (childManager.backStackEntryCount != 0) {
            if (childManager.findFragmentByTag(StartedActivityFragment.tag)?.isVisible == true
                && intent?.getIntExtra("notification", 0) == 0) {
                childManager.popBackStack("new_active", FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }
            else {
                childManager.popBackStack()
            }
        }
        else if (supportFragmentManager.backStackEntryCount != 0) {
            supportFragmentManager.popBackStack()
        }
        else {
            super.onBackPressed()
        }
        intent.putExtra("notification", 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val fragment = supportFragmentManager.findFragmentByTag(ActivityTabs.tag)
        fragment?.onActivityResult(requestCode, resultCode, data)
    }
}