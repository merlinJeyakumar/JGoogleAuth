package com.nativedevps.utility

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

fun <T> runOnAsyncThread (callback: suspend CoroutineScope.() -> T): Job {
    return CoroutineScope(Dispatchers.IO).launch {
        callback()
    }
}

fun CoroutineScope.runOnMainThread(callback: suspend CoroutineScope.() -> Unit): Job {
    return CoroutineScope(Dispatchers.Main).launch {
        callback()
    }
}

private fun executeAndMainCallback(
    backgroundExecution: () -> Unit,
    mainThreadExecution: () -> Unit
): Job {
    return runOnAsyncThread {
        backgroundExecution()
        runOnMainThread {
            mainThreadExecution()
        }
    }
}