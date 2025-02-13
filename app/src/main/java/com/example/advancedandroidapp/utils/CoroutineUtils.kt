package com.example.advancedandroidapp.utils

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

object CoroutineUtils {
    private val mainScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val ioScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun launchMain(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ): Job = mainScope.launch(context, start, block)

    fun launchIO(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ): Job = ioScope.launch(context, start, block)

    suspend fun <T> withMainContext(block: suspend CoroutineScope.() -> T): T =
        withContext(Dispatchers.Main, block)

    suspend fun <T> withIOContext(block: suspend CoroutineScope.() -> T): T =
        withContext(Dispatchers.IO, block)

    fun <T> Flow<T>.flowOnMain(): Flow<T> = flowOn(Dispatchers.Main)

    fun <T> Flow<T>.flowOnIO(): Flow<T> = flowOn(Dispatchers.IO)

    fun CoroutineScope.launchCatching(
        onError: (Throwable) -> Unit = {},
        block: suspend CoroutineScope.() -> Unit
    ): Job = launch {
        try {
            block()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            onError(e)
        }
    }
}

class CoroutineErrorHandler(
    private val onError: (Throwable) -> Unit
) : CoroutineExceptionHandler {
    override val key = CoroutineExceptionHandler.Key
    override fun handleException(context: CoroutineContext, exception: Throwable) {
        onError(exception)
    }
}

sealed class AsyncResult<out T> {
    object Loading : AsyncResult<Nothing>()
    data class Success<T>(val data: T) : AsyncResult<T>()
    data class Error(val error: Throwable) : AsyncResult<Nothing>()
}

fun <T> Flow<T>.asAsyncResult(): Flow<AsyncResult<T>> = flow {
    emit(AsyncResult.Loading)
    try {
        collect { value ->
            emit(AsyncResult.Success(value))
        }
    } catch (e: Exception) {
        emit(AsyncResult.Error(e))
    }
}

fun <T> Flow<T>.retryWithDelay(
    retries: Int = 3,
    delayMillis: Long = 1000,
    shouldRetry: (Throwable) -> Boolean = { true }
): Flow<T> = retry(retries) { throwable ->
    if (shouldRetry(throwable)) {
        delay(delayMillis)
        true
    } else {
        false
    }
}

fun <T> Flow<T>.debounceFirst(
    timeoutMillis: Long = 1000
): Flow<T> = flow {
    var lastEmissionTime = 0L
    collect { value ->
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastEmissionTime > timeoutMillis) {
            lastEmissionTime = currentTime
            emit(value)
        }
    }
}

fun <T> Flow<T>.throttleFirst(
    timeoutMillis: Long = 1000
): Flow<T> = flow {
    var lastEmissionTime = 0L
    collect { value ->
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastEmissionTime >= timeoutMillis) {
            lastEmissionTime = currentTime
            emit(value)
        }
    }
}

fun <T> Flow<T>.throttleLast(
    timeoutMillis: Long = 1000
): Flow<T> = flow {
    var lastValue: T? = null
    var lastEmissionTime = 0L
    
    collect { value ->
        lastValue = value
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastEmissionTime >= timeoutMillis) {
            lastEmissionTime = currentTime
            lastValue?.let { emit(it) }
        }
    }
}

fun <T> Flow<T>.withPrevious(): Flow<Pair<T?, T>> = flow {
    var previous: T? = null
    collect { value ->
        emit(Pair(previous, value))
        previous = value
    }
}

fun <T> Flow<T>.withLoading(
    onLoading: () -> Unit,
    onFinished: () -> Unit
): Flow<T> = flow {
    try {
        onLoading()
        collect { value ->
            emit(value)
        }
    } finally {
        onFinished()
    }
}

fun <T> Flow<AsyncResult<T>>.onSuccess(action: suspend (T) -> Unit): Flow<AsyncResult<T>> =
    onEach { result ->
        if (result is AsyncResult.Success) {
            action(result.data)
        }
    }

fun <T> Flow<AsyncResult<T>>.onError(action: suspend (Throwable) -> Unit): Flow<AsyncResult<T>> =
    onEach { result ->
        if (result is AsyncResult.Error) {
            action(result.error)
        }
    }

fun <T> Flow<AsyncResult<T>>.onLoading(action: suspend () -> Unit): Flow<AsyncResult<T>> =
    onEach { result ->
        if (result is AsyncResult.Loading) {
            action()
        }
    }
