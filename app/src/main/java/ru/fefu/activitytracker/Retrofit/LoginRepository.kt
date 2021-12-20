package ru.fefu.activitytracker.Retrofit

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import ru.fefu.activitytracker.App
import ru.fefu.activitytracker.Retrofit.response.TokenUserModel
import ru.fefu.activitytracker.Retrofit.response.UserModel

class LoginRepository {
    private val activityApi = App.INSTANCE.retrofit.create(ActivityAPI::class.java)

    suspend fun register(login:String, password:String, name:String, gender: Int): Flow<Result<TokenUserModel>> =
        flow<Result<TokenUserModel>> {
            emit(
                Result.Success(
                    activityApi.register(login, password, name, gender)
                )
            )
        }
            .catch { emit(Result.Error(it)) }
            .flowOn(Dispatchers.IO)

    suspend fun login(login:String, password:String): Flow<Result<TokenUserModel>> =
        flow<Result<TokenUserModel>> {
            emit(
                Result.Success(
                    activityApi.login(login, password)
                )
            )
        }
            .catch { emit(Result.Error(it)) }
            .flowOn(Dispatchers.IO)

    suspend fun getProfile(): Flow<Result<UserModel>> =
        flow<Result<UserModel>> {
            emit(
                Result.Success(
                    activityApi.getProfile()
                )
            )
        }
            .catch { emit(Result.Error(it)) }
            .flowOn(Dispatchers.IO)

    suspend fun logout(): Flow<Result<Unit>> =
        flow<Result<Unit>> {
            emit(
                Result.Success(
                    activityApi.logout()
                )
            )
        }
            .catch { emit(Result.Error(it)) }
            .flowOn(Dispatchers.IO)
}