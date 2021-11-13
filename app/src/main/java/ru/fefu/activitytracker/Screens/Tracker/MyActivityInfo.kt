package ru.fefu.activitytracker.Screens.Tracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.fefu.activitytracker.Adapters.ActivityListAdapter
import ru.fefu.activitytracker.Models.ActivityData
import ru.fefu.activitytracker.databinding.MyActivityDetailsBinding
import java.time.Duration
import java.time.LocalDateTime

class MyActivityInfo(private val info: ActivityData): Fragment() {
    private var _binding: MyActivityDetailsBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance(info: ActivityData): MyActivityInfo {
            return MyActivityInfo(info)
        }
        const val tag = "my_info"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = MyActivityDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.distance.text = info.distance
        var startTime = ""
        if (info.startDate.hour.toString().length == 1) {
            startTime += '0' + info.startDate.hour.toString() + ':'
        } else {
            startTime += info.startDate.hour.toString() + ':'
        }
        if (info.startDate.minute.toString().length == 1) {
            startTime += '0' + info.startDate.hour.toString()
        } else {
            startTime += info.startDate.minute.toString()
        }

        var endTime = ""
        if (info.endDate.hour.toString().length == 1) {
            endTime += '0' + info.endDate.hour.toString() + ':'
        } else {
            endTime += info.endDate.hour.toString() + ':'
        }
        if (info.endDate.minute.toString().length == 1) {
            endTime += '0' + info.endDate.hour.toString()
        } else {
            endTime += info.endDate.minute.toString()
        }

        binding.startTime.text = startTime
        binding.finishTime.text = endTime

        if (LocalDateTime.now().year == info.endDate.year &&
            LocalDateTime.now().monthValue == info.endDate.monthValue &&
            LocalDateTime.now().dayOfMonth == info.endDate.dayOfMonth) {
            binding.date.text = Duration.between(info.endDate, LocalDateTime.now()).toHours().toString() +
                    ActivityListAdapter.getNoun(
                        Duration.between(
                            info.endDate,
                            LocalDateTime.now()
                        ).toHours(), " час", " часа", " часов"
                    ) +
                    " назад"
        }
        else binding.date.text = info.endDate.dayOfMonth.toString() + '.'+
                info.endDate.monthValue.toString() + '.' + info.endDate.year.toString()

        binding.toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}