package com.nativedevps.model

data class CellModel(
    val cellRow: Int? = null,
    val cellColumn: String?,
    val cellSheet: String?,
    val value: String,
) {
    fun range(): String {
        return "$cellSheet!$cellColumn$cellRow"
    }
}