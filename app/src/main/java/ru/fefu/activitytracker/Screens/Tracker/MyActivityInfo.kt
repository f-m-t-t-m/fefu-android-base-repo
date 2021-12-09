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
        var startTime = "%02d".format(info.startDate.hour) + ":" + "%02d".format(info.startDate.minute)

        var endTime = "%02d".format(info.endDate.hour) + ":" + "%02d".format(info.endDate.minute)

        binding.startTime.text = startTime
        binding.finishTime.text = endTime

        if (LocalDateTime.now().equals(info.endDate)) {
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
        val duration_ = Duration.between(info.endDate, info.startDate);
        var seconds: Long = Math.abs(duration_.getSeconds())
        val hours = seconds / 3600
        seconds -= hours * 3600
        val minutes = seconds / 60
        if (hours > 0) binding.duration.text = "%d ч %d мин".format(hours, minutes)
        else binding.duration.text = "%d мин".format(minutes)

        binding.toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}