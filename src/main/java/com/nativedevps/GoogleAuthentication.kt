package com.nativedevps

import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.nativedevps.drive.AuthenticationState
import com.nativedevps.spreadsheet.UselessJob
import com.nativedevps.utility.getLastSignedAccount
import com.nativedevps.utility.runOnAsyncThread
import com.nativedevps.utility.runOnMainThread
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import org.apache.http.auth.AuthenticationException

/*
* created by merlinjeyakumar at 11/8/2022
* */
open class GoogleAuthentication(
    private val context: Context,
) {
    fun <T> runGoogleClientRequest(
        uselessJob: UselessJob<T> = UselessJob<T>(),
        execution: () -> Any
    ): UselessJob<T> {
        runOnAsyncThread {
            try {
                val result = execution() as T
                runOnMainThread {
                    uselessJob.setResult(result)
                }
            } catch (e: UserRecoverableAuthIOException) {
                googleAuthenticationInterface?.authenticationCallback = {
                    when (it) {
                        AuthenticationState.success -> {
                            runGoogleClientRequest<T>(uselessJob, execution)
                        }
                        AuthenticationState.failed -> runOnMainThread {
                            uselessJob.setResult(exception = AuthenticationException("failed"))
                        }
                        AuthenticationState.cancelled -> runOnMainThread {
                            uselessJob.setResult(exception = CancellationException("cancelled"))
                        }
                    }
                    googleAuthenticationInterface?.authenticationCallback = null
                }
                runOnMainThread {
                    googleAuthenticationInterface?.launch(e.intent)
                        ?: error("Authentication needed to be handled with interface setup")
                }
                Log.d("JeyK", "UserRecoverableAuthIOException ${e.message}")
            } catch (e: Exception) {
                //e.printStackTrace()
                runOnMainThread {
                    uselessJob.setResult(exception = e)
                }
                Log.e("JeyK", "exception ${e.message}")
            }
        }
        return uselessJob
    }


    abstract class GoogleAuthenticationInterface {
        var authenticationCallback: ((authenticationState: AuthenticationState) -> Unit)? = null

        abstract fun launch(intent: Intent)
        abstract fun setGoogleAuthenticationResult(authenticationState: AuthenticationState)
    }

    companion object {
        var googleAuthenticationInterface: GoogleAuthenticationInterface? = null
    }
}