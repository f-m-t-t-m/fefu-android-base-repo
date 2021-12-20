package ru.fefu.activitytracker.Screens.SignUp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.fefu.activitytracker.Retrofit.LoginRepository
import ru.fefu.activitytracker.Retrofit.Result
import ru.fefu.activitytracker.Retrofit.response.TokenUserModel

class SignUpViewModel:ViewModel() {
    private val loginRepository = LoginRepository()

    private val _dataFlow = MutableSharedFlow<Result<TokenUserModel>>(replay = 0)

    val dataFlow get() = _dataFlow

    fun register(login:String, password:String, name:String, gender:Int) {
        viewModelScope.launch {
            loginRepository.register(login, password, name, gender)
                .collect {
                    when(it) {
                        is Result.Success<*> -> _dataFlow.emit(it)
                        is Result.Error<*> -> _dataFlow.emit(it)
                    }
                }
        }
    }
}