package com.nativedevps.google.delegate

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.Keep
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.util.ExponentialBackOff
import com.nativedevps.utility.getLastSignedAccount

/*
* Implement it as your activity property
**/
open class GoogleAuthenticationProvider {

    private var authContractor: AuthContractor
    private var connectivityEvent: ((SignInModel) -> Unit)? = null
    private var signInInResult: ActivityResultLauncher<Intent>


    constructor(activity: FragmentActivity, authContractor: AuthContractor) {
        this.authContractor = authContractor
        signInInResult =
            activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                handleGoogleSigninAuthenticationResult(result)
            }
    }

    constructor(fragment: Fragment, authContractor: AuthContractor) {
        this.authContractor = authContractor
        signInInResult =
            fragment.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                handleGoogleSigninAuthenticationResult(result)
            }
    }

    private fun handleGoogleSigninAuthenticationResult(result: ActivityResult) {
        if (result.resultCode == RESULT_OK) {
            Log.v("GoogleAuthentication", "RESULT_OK")
            GoogleSignIn.getSignedInAccountFromIntent(result.data).apply {
                if (isSuccessful) {
                    getResult(ApiException::class.java).let {
                        connectivityEvent?.invoke(
                            SignInModel(
                                it.email
                                    ?: error("Email not retrieved from GoogleSigninAccount result"),
                                it.givenName ?: it.displayName ?: it.familyName!!
                            )
                        )
                    }
                } else {
                    connectivityEvent?.invoke(SignInModel(error = exception?.message))
                }
            }
        } else {
            val account = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            connectivityEvent?.invoke(SignInModel(error = account.exception?.message?:"unknown failure with google login"))
            account.exception?.printStackTrace()
        }
    }

    /**
     * invoke signIn option
     **/
    fun signIn(
        signOut: Boolean = true,
        clientId: String? = null,
        callback: ((SignInModel) -> Unit),
    ) {
        connectivityEvent = callback
        if (signOut) {
            getSignInIntent().signOut()
        }
        val account = authContractor.retrieveActivity().getLastSignedAccount()
        account?.let {
            callback(SignInModel(error = "account already connected"))
            connectivityEvent = null
        } ?: run {
            signInInResult.launch(getSignInIntent(clientId).signInIntent)
        }
    }

    fun signOut(callback: (exception: Exception?) -> Unit) {
        getSignInIntent().signOut().addOnCompleteListener {
            if (it.isSuccessful) {
                connectivityEvent?.invoke(SignInModel(error = "signed_out"))
                callback(null)
            } else {
                callback(it.exception!!)
            }
        }
    }

    private fun getSignInIntent(clientId: String? = null): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(
            GoogleSignInOptions.DEFAULT_SIGN_IN
        ).apply {
            if (clientId != null) {
                requestIdToken(clientId)
            }
        }.requestEmail()
            .build()
        return GoogleSignIn.getClient(authContractor.retrieveActivity(), gso)
    }

    fun getGoogleAccountCredential(
        list: List<String> = listOf(
            "https://www.googleapis.com/auth/userinfo.email",
            "https://www.googleapis.com/auth/userinfo.profile"
        ),
    ): GoogleAccountCredential? {
        return GoogleSignIn.getLastSignedInAccount(authContractor.retrieveActivity())?.let {
            GoogleAccountCredential.usingOAuth2(
                authContractor.retrieveActivity(), list
            )
                .setSelectedAccountName(it.account?.name)
                .setBackOff(ExponentialBackOff())
        }
    }

    fun init() {
        //noop
    }

    fun isAuthenticated(): Boolean {
        return authContractor.retrieveActivity().getLastSignedAccount() != null
    }

    @Keep
    data class SignInModel(
        var email: String? = null,
        var givenName: String? = null,
        var error: String? = null,
    )
}