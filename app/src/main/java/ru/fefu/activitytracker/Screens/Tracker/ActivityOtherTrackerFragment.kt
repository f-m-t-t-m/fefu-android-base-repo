package ru.fefu.activitytracker.Screens.Tracker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import ru.fefu.activitytracker.Adapters.ActivityListAdapter
import ru.fefu.activitytracker.Models.DateData
import ru.fefu.activitytracker.Models.UserActivityData
import ru.fefu.activitytracker.R
import ru.fefu.activitytracker.databinding.ActivityFragmentTrackingOtherBinding
import java.time.LocalDateTime

class ActivityOtherTrackerFragment : Fragment(R.layout.activity_fragment_tracking_other) {
    private var _binding: ActivityFragmentTrackingOtherBinding? = null
    private val binding get() = _binding!!
    private lateinit var items: MutableList<UserActivityData>

    val activities = listOf<UserActivityData>(
        UserActivityData(
            "1000 м",
            "Серфинг",
            LocalDateTime.of(2021, 10, 27, 11, 22),
            LocalDateTime.of(2021, 10, 28, 12, 40),
            "@user1",
        ),
        UserActivityData(
            "14.32 км",
            "Велосипед",
            LocalDateTime.of(2021, 10, 27, 7, 40),
            LocalDateTime.of(2021, 10, 27, 10, 59),
            "user2"
        )
    )

    val map = mapOf(1 to "Январь", 2 to "Февраль", 3 to "Март",
        4 to "Апрель", 5 to "Май", 6 to "Июнь",
        7 to "Июль", 8 to "Август", 9 to "Сентябрь",
        10 to "Октябрь", 11 to "Ноябрь", 12 to "Декабрь")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ActivityFragmentTrackingOtherBinding.inflate(inflater, container, false)
        return binding.root
    }

    val data_activities = mutableListOf<Any>()
    private fun fill_date(activities: List<UserActivityData>) {
        val cur = LocalDateTime.now()
        var date = DateData("")
        for (activity in activities) {
            if (cur.year == activity.endDate.year &&
                cur.monthValue == activity.endDate.monthValue &&
                cur.dayOfMonth == activity.endDate.dayOfMonth) {
                if (date.Date != "Сегодня") {
                    date = DateData("Сегодня")
                    data_activities.add(date)
                }
            }
            else {
                if (date.Date != map.get(activity.endDate.monthValue) + ' ' + activity.endDate.year.toString()  + "года") {
                    date = DateData(map.get(activity.endDate.monthValue) + activity.endDate.year.toString())
                    data_activities.add(date)
                }
            }
            Log.d("TAG", cur.hour.toString())
            data_activities.add(activity)
        }
    }

    private val adapter = ActivityListAdapter(data_activities)

    private fun changeFragment(position: Int) {
        if (position in data_activities.indices) {
            val manager = activity?.supportFragmentManager?.findFragmentByTag(ActivityTabs.tag)?.childFragmentManager
            manager?.beginTransaction()?.apply {
                manager.fragments.forEach(::hide)
                add (
                    R.id.activity_fragment_container,
                    UserActivityInfo.newInstance(),
                    UserActivityInfo.tag,
                )
                addToBackStack(null)
                commit()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fill_date(activities)
        val recycleView = binding.recyclerView
        recycleView.layoutManager = LinearLayoutManager(requireContext())
        recycleView.adapter = adapter
        adapter.setItemClickListener { changeFragment(it) }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}