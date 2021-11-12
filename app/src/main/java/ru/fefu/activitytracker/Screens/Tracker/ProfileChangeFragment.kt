package ru.fefu.activitytracker.Screens.Tracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.fefu.activitytracker.databinding.ProfileChangeActivityBinding

class ProfileChangeFragment: Fragment() {
    private var _binding: ProfileChangeActivityBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance(): ProfileChangeFragment{
            return ProfileChangeFragment()
        }
        const val tag = "profile_change"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ProfileChangeActivityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.backButton.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}