package com.nativedevps.exception

class MismatchingValuesException(override val message: String?, val sheetId:String) : Exception() {
}