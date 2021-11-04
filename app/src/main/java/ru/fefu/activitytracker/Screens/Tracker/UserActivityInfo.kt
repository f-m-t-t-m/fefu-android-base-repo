package ru.fefu.activitytracker.Screens.Tracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.fefu.activitytracker.databinding.UserActivityDetailsBinding

class UserActivityInfo: Fragment() {
    private var _binding: UserActivityDetailsBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance(): UserActivityInfo {
            return UserActivityInfo()
        }
        const val tag = "user_info"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = UserActivityDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}