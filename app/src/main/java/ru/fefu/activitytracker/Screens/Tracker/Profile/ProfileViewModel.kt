package ru.fefu.activitytracker.Screens.Tracker.Profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.fefu.activitytracker.Retrofit.LoginRepository
import ru.fefu.activitytracker.Retrofit.Result
import ru.fefu.activitytracker.Retrofit.response.UserModel

class ProfileViewModel: ViewModel() {
    private val loginRepository = LoginRepository()

    private val _profile = MutableSharedFlow<Result<UserModel>>(replay = 0)
    private val _logoutUser = MutableSharedFlow<Result<Unit>>(replay = 0)

    val profile get() = _profile
    val logoutUser get() = _logoutUser

    fun getProfile() {
        viewModelScope.launch {
            loginRepository.getProfile()
                .collect {
                    when(it) {
                        is Result.Success<*> -> _profile.emit(it)
                        is Result.Error<*> -> _profile.emit(it)
                    }
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            loginRepository.logout()
                .collect {
                    when(it) {
                        is Result.Success<*> -> _logoutUser.emit(it)
                        is Result.Error<*> -> _logoutUser.emit(it)
                    }
                }
        }
    }
}