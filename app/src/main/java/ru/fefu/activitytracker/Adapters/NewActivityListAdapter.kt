package ru.fefu.activitytracker.Adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.fefu.activitytracker.Models.NewActivityData
import ru.fefu.activitytracker.Models.UserActivityData
import ru.fefu.activitytracker.R

class NewActivityListAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var selected = -1

    private var activities = listOf<NewActivityData>(
        NewActivityData("Велосипед", false),
        NewActivityData("Бег", false ),
        NewActivityData("Шаг", false ))

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.new_activity_item, parent, false)
        return NewActivityViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as NewActivityViewHolder).bind(activities[position])
    }

    override fun getItemCount(): Int = 3

    inner class NewActivityViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val type = itemView.findViewById<TextView>(R.id.activity_type)

        @SuppressLint("SetTextI18n")
        fun bind(activity: NewActivityData) {
            type.text = activity.type
            itemView.isSelected = activity.isSelected

            if (itemView.isSelected) {
                itemView.setBackgroundResource(R.drawable.border_selected)
            } else {
                itemView.setBackgroundResource(R.drawable.border)
            }
            itemView.setOnClickListener {
                Log.d("select", activities.toString())
                activities[adapterPosition].isSelected = true
                if (selected != -1 && selected != adapterPosition) {
                    activities[selected].isSelected = false
                }
                selected = adapterPosition
                notifyDataSetChanged()
            }
        }
    }
}