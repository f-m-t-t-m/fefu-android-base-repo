package ru.fefu.activitytracker.Screens.Tracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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
        if (savedInstanceState == null) {
            childFragmentManager.beginTransaction().apply {
                add(R.id.activity_fragment_container, ActivityTrackerFragment.newInstance())
                commit()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}