package com.nativedevps.utility

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.util.ExponentialBackOff
import com.nativedevps.google.delegate.GoogleAuthenticationProvider

fun Context.getGoogleAccountCredential(list: List<String>): GoogleAccountCredential? {
    return GoogleSignIn.getLastSignedInAccount(this)?.let {
        GoogleAccountCredential.usingOAuth2(
            this, list
        )
            .setSelectedAccountName(it.account?.name)
            .setBackOff(ExponentialBackOff())
    }
}

fun Context.getLastSignedAccount(): GoogleSignInAccount? {
    return GoogleSignIn.getLastSignedInAccount(this)
}

fun Context.getLastSignedAccountSignInModel(): GoogleAuthenticationProvider.SignInModel? {
    return getLastSignedAccount()?.let {
        GoogleAuthenticationProvider.SignInModel(
            it.email
                ?: error("Email not retrieved from GoogleSigninAccount result"),
            it.givenName ?: it.displayName ?: it.familyName!!
        )
    }
}