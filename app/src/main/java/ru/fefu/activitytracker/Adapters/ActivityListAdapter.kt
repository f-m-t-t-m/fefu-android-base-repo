package ru.fefu.activitytracker.Adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.fefu.activitytracker.Models.ActivityData
import ru.fefu.activitytracker.Models.DateData
import ru.fefu.activitytracker.R
import ru.fefu.activitytracker.Models.UserActivityData
import java.time.Duration
import java.time.LocalDateTime

class ActivityListAdapter(private val activities: List<Any>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var ItemClickListener: (Int) -> Unit = {}

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        if(viewType == 0) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.my_activity_item, parent, false)
            return ActivityViewHolder(view)
        }
        else if (viewType == 1) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.user_activity_item, parent, false)
            return UserActivityViewHolder(view)
        }
        else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.date_item, parent, false)
            return DateViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == 0) {
            ((holder as ActivityViewHolder)).bind(activities[position] as ActivityData)
        }
        else if (getItemViewType(position) == 1) {
            ((holder as UserActivityViewHolder)).bind(activities[position] as UserActivityData)
        }
        else {
            ((holder as DateViewHolder)).bind(activities[position] as DateData)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            activities[position] is ActivityData -> 0
            activities[position] is UserActivityData -> 1
            else -> 2
        }
    }

    override fun getItemCount(): Int = activities.size;

    companion object {
        fun getNoun(number: Long, one: String, two: String, three: String) : String {
            var n = number
            n %= 100
            if (n in 5..20) {
                return three
            }
            n %= 10
            if (n == 1L) {
                return one
            }
            if (n in 2..4) {
                return two
            }
            return three
        }
    }

    fun setItemClickListener(listener: (Int) -> Unit) {
        ItemClickListener = listener
    }

    inner class ActivityViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val distance = itemView.findViewById<TextView>(R.id.distance)
        private val duration = itemView.findViewById<TextView>(R.id.duration)
        private val type = itemView.findViewById<TextView>(R.id.activity_type)
        private val date = itemView.findViewById<TextView>(R.id.date)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                ItemClickListener.invoke(position)
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(activity: ActivityData) {
            distance.text = activity.distance
            type.text = activity.activityType
            val duration_ = Duration.between(activity.endDate, activity.startDate);
            var seconds: Long = Math.abs(duration_.getSeconds())
            val hours = seconds / 3600
            seconds -= hours * 3600
            val minutes = seconds / 60
            val hours_ = getNoun(hours, "час", "часа", "часов")
            val minutes_ = getNoun(minutes, "минута", "минуты", "минут")
            duration.text = hours.toString() + ' ' +hours_ + ' ' +minutes.toString() + ' '+ minutes_
            if (LocalDateTime.now().year == activity.endDate.year &&
                LocalDateTime.now().monthValue == activity.endDate.monthValue &&
                LocalDateTime.now().dayOfMonth == activity.endDate.dayOfMonth) {
                date.text = Duration.between(activity.endDate, LocalDateTime.now()).toHours().toString() +
                        getNoun(Duration.between(activity.endDate, LocalDateTime.now()).toHours(), " час", " часа", " часов") +
                        " назад"
            }
            else date.text = activity.endDate.dayOfMonth.toString() + '.'+
                    activity.endDate.monthValue.toString() + '.' + activity.endDate.year.toString()
        }
    }

    inner class UserActivityViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val distance = itemView.findViewById<TextView>(R.id.distance)
        private val duration = itemView.findViewById<TextView>(R.id.duration)
        private val type = itemView.findViewById<TextView>(R.id.activity_type)
        private val date = itemView.findViewById<TextView>(R.id.date)
        private val user = itemView.findViewById<TextView>(R.id.user)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                ItemClickListener.invoke(position)
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(activity: UserActivityData) {
            distance.text = activity.distance
            type.text = activity.activityType
            user.text = activity.user
            val duration_ = Duration.between(activity.endDate, activity.startDate);
            var seconds: Long = Math.abs(duration_.getSeconds())
            val hours = seconds / 3600
            seconds -= hours * 3600
            val minutes = seconds / 60
            val hours_ = getNoun(hours, "час", "часа", "часов")
            val minutes_ = getNoun(minutes, "минута", "минуты", "минут")
            duration.text = hours.toString() + ' ' +hours_ + ' ' +minutes.toString() + ' '+ minutes_
            if (LocalDateTime.now().year == activity.endDate.year &&
                LocalDateTime.now().monthValue == activity.endDate.monthValue &&
                LocalDateTime.now().dayOfMonth == activity.endDate.dayOfMonth) {
                date.text = Duration.between(activity.endDate, LocalDateTime.now()).toHours().toString() +
                        getNoun(Duration.between(activity.endDate, LocalDateTime.now()).toHours(), " час", " часа", " часов") +
                        " назад"
            }
            else date.text = activity.endDate.dayOfMonth.toString() + '.'+
                    activity.endDate.monthValue.toString() + '.' + activity.endDate.year.toString()
        }
    }

    inner class DateViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val date = itemView.findViewById<TextView>(R.id.date_label)

        @SuppressLint("SetTextI18n")
        fun bind(date_: DateData) {
            date.text = date_.Date
        }
    }
}

