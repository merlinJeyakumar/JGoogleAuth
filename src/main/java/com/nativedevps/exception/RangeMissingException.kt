package com.nativedevps.exception

class RangeMissingException(override val message: String?,val sheetId:String) : Exception() {
}