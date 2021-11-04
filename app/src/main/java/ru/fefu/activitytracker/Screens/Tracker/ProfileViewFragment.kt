package ru.fefu.activitytracker.Screens.Tracker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.fefu.activitytracker.R
import ru.fefu.activitytracker.databinding.ProfileViewActivityBinding

class ProfileViewFragment: Fragment() {
    private var _binding: ProfileViewActivityBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance(): ProfileViewFragment{
            return ProfileViewFragment()
        }
        const val tag = "profile_view"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ProfileViewActivityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.change.setOnClickListener{
            val manager = activity?.supportFragmentManager?.findFragmentByTag(ProfileFragment.tag)?.childFragmentManager
            manager?.beginTransaction()?.apply {
                manager.fragments.forEach(::hide)
                add (R.id.profile_fragment_container, ProfileChangeFragment.newInstance(), ProfileChangeFragment.tag)
                addToBackStack(null)
                commit()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}