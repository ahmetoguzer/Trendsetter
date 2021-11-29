package com.app.trendsetter.data.remote.repository

import com.app.trendsetter.data.remote.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

open class BaseRepository {

    protected fun <T> responseFlowWrapper(remoteCall: suspend () -> Resource<T>): Flow<Resource<T>> =
        flow {
            emit(Resource.loading())
            emit(remoteCall.invoke())
        }
}
