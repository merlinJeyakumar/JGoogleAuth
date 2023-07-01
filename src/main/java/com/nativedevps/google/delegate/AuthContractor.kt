package com.nativedevps.google.delegate

import androidx.fragment.app.FragmentActivity

interface AuthContractor {
    fun retrieveActivity(): FragmentActivity
}