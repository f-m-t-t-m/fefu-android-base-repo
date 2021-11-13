package ru.fefu.activitytracker.Screens.Tracker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import ru.fefu.activitytracker.Adapters.NewActivityListAdapter
import ru.fefu.activitytracker.App
import ru.fefu.activitytracker.Enums.ActivitiesEnum
import ru.fefu.activitytracker.Models.NewActivityData
import ru.fefu.activitytracker.R
import ru.fefu.activitytracker.Room.ActivityRoom
import ru.fefu.activitytracker.databinding.NewActivityFragmentBinding
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class NewActivityFragment: Fragment() {
    private var _binding: NewActivityFragmentBinding? = null
    private val binding get() = _binding!!
    private var activities = mutableListOf<NewActivityData>()
    private lateinit var adapter: NewActivityListAdapter

    companion object {
        fun newInstance(): NewActivityFragment {
            return NewActivityFragment()
        }
        const val tag = "new_activity"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fillActivities()
        _binding = NewActivityFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recycleView = binding.newActivityList
        recycleView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recycleView.adapter = adapter

        binding.backButton.setOnClickListener {
            activity?.onBackPressed()
        }

        binding.buttonContinue.setOnClickListener {
            if (adapter.selected != -1) {
                val endDate = System.currentTimeMillis() - (0..604800000).random()
                val startDate = endDate - (600000..86400000).random()
                App.INSTANCE.db.activityDao().insert (
                    ActivityRoom (
                        0,
                        adapter.selected,
                        startDate,
                        endDate,
                        123.0,
                        131.0
                    )
                )

                val manager = activity?.supportFragmentManager?.findFragmentByTag(ActivityTabs.tag)?.childFragmentManager
                manager?.beginTransaction()?.apply {
                    manager.fragments.forEach(::hide)
                    add (
                        R.id.activity_fragment_container,
                        StartedActivityFragment.newInstance(),
                        StartedActivityFragment.tag,
                    )
                    addToBackStack(null)
                    commit()
                }
            }
        }
    }

    fun fillActivities() {
        for(i in ActivitiesEnum.values()) {
            activities.add(NewActivityData(i.type, false))
        }
        adapter = NewActivityListAdapter(activities)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}