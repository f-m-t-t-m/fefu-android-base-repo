package ru.fefu.activitytracker.Screens.Tracker

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.osmdroid.util.GeoPoint
import ru.fefu.activitytracker.R
import ru.fefu.activitytracker.databinding.FragmentActivityTabsBinding

class ActivityTabs: Fragment() {
    private var _binding: FragmentActivityTabsBinding? = null

    private val binding get() = _binding!!

    companion object {
        fun newInstance(): ActivityTabs {
            return ActivityTabs()
        }
        const val tag = "tabs"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentActivityTabsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val notificationFlag = activity?.intent?.getIntExtra("notification", 0)
        val id = activity?.intent?.getIntExtra("activity_id", -1)!!
        if (savedInstanceState == null) {
            childFragmentManager.beginTransaction().apply {
                add(
                    R.id.activity_fragment_container,
                    ActivityTrackerFragment.newInstance(),
                )
                commit()
            }

            if (notificationFlag == 1 && id !== -1) {
                val navbar = activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)
                navbar?.visibility = View.GONE
                childFragmentManager.beginTransaction().apply {
                    childFragmentManager.fragments.forEach(::hide)
                    add(
                        R.id.activity_fragment_container,
                        StartedActivityFragment.newInstance(id),
                        StartedActivityFragment.tag
                    )
                    addToBackStack(null)
                    commit()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("qwerty", "pretabs")
        val fragments = childFragmentManager.fragments
        for(f in fragments) {
            f.onActivityResult(requestCode, resultCode, data)
        }
    }
}