package ru.fefu.activitytracker.Screens.Tracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import ru.fefu.activitytracker.Adapters.PagerAdapter
import ru.fefu.activitytracker.databinding.ActivityFragmentTrackingBinding


class ActivityTrackerFragment : Fragment() {
    private var _binding: ActivityFragmentTrackingBinding? = null

    private val binding get() = _binding!!
    private lateinit var adapter : PagerAdapter

    companion object {
        fun newInstance(): ActivityTrackerFragment {
            return ActivityTrackerFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ActivityFragmentTrackingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = PagerAdapter(this)
        binding.pager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.pager) {tab, position ->
            if (position == 0) {
                tab.text = "Мои"
            }
            else {
                tab.text = "Пользователей"
            }
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}