package ru.fefu.activitytracker

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import ru.fefu.activitytracker.databinding.ActivityLayoutBinding

class FragmentInfo(val buttonId: Int, private val _newInstance: Fragment, val tag: String) {
    fun newInstance(): Fragment {
        return _newInstance
    }
}

class Activity : AppCompatActivity() {
    private lateinit var binding: ActivityLayoutBinding

    val fragments = listOf<FragmentInfo>(
        FragmentInfo(R.id.action_activity_tracker, ActivityTrackerFragment().newInstance(), "tracking"),
        FragmentInfo(R.id.action_profile, ProfileFragment().newInstance(), "profile")
    )

    private var activeFragment = fragments[0]
    private var notActiveFragment = fragments[1]

    private fun replaceFragment(buttonId: Int) {
        if (activeFragment.buttonId == buttonId) {
            return
        }

        val active = supportFragmentManager.findFragmentByTag(activeFragment.tag)
        val notActive = supportFragmentManager.findFragmentByTag(notActiveFragment.tag)

        if (active != null) {
            supportFragmentManager.beginTransaction().apply {
                hide(active)
                commit()
            }
        }

        if (notActive != null) {
            supportFragmentManager.beginTransaction().apply {
                show(notActive)
                commit()
            }
        }

        else {
            supportFragmentManager.beginTransaction().apply {
                add(R.id.fragment_container_view, notActiveFragment.newInstance(), notActiveFragment.tag)
                commit()
            }
        }
        activeFragment = notActiveFragment.also { notActiveFragment = activeFragment }
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
                    ActivityTrackerFragment().newInstance(),
                "tracking"
                )
                commit()
            }
        }
        binding.bottomNavigationView.setOnNavigationItemSelectedListener {
            replaceFragment(it.itemId)
            true
        }
    }
}