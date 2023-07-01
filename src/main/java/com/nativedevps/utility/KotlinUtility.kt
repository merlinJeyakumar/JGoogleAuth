package com.nativedevps.utility

import com.nativedevps.spreadsheet.UselessJob
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


suspend fun <T> UselessJob<T>.await(): T {
    return suspendCancellableCoroutine { cancellableContinuation ->
        addOnCompletion(object : UselessJob.UselessInterface<T> {
            override fun onCompletion(spreadsheet: T?, exception: Exception?) {
                if (spreadsheet == null) {
                    cancellableContinuation.cancel(exception)
                } else {
                    cancellableContinuation.resume(spreadsheet)
                }
            }
        })
    }
}

fun String.substr(startIndex: Int, length: Int): String {
    return substring(startIndex, startIndex + length)
}