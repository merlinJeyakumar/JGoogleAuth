package com.nativedevps.utility

import com.google.api.services.sheets.v4.model.AppendValuesResponse
import com.google.api.services.sheets.v4.model.BatchUpdateValuesRequest
import com.google.api.services.sheets.v4.model.BatchUpdateValuesResponse
import com.google.api.services.sheets.v4.model.ValueRange
import com.nativedevps.model.CellModel
import com.nativedevps.spreadsheet.JSpreadsheet


const val sheetBaseUrl = "https://docs.google.com/spreadsheets/d/"

fun asSheetUrl(sheetId: String): String {
    return sheetBaseUrl + sheetId
}

fun getSheetId(url: String): String? {
    if (url.contains(sheetBaseUrl)) {
        url.substringAfter(sheetBaseUrl).split("/").let {
            if (it.isNotEmpty()) {
                return it[0]
            }
        }
    } else {
        if (url.length in 20..50) {
            return url
        }
    }
    return null
}

fun isValidSheet(urlOrSheetId: String?): Boolean {
    return urlOrSheetId?.let { getSheetId(it).isNullOrBlank() } ?: false
}

fun List<ValueRange>.toColumn(): MutableList<List<CellModel>> {
    val cellModelList = mutableListOf<List<CellModel>>()
    for (valueRange in this) {
        val cellPlace = valueRange.range

        val returnList = mutableListOf<CellModel>()
        for ((index, item) in (valueRange?.getValues()?.getOrNull(0) ?: listOf()).withIndex()) {
            returnList.add(CellModel(
                cellRow = cellPlace.toCellRow(),
                cellColumn = cellPlace.toCellColumn(index),
                cellSheet = cellPlace.toCellSheet(),
                value = item.toString()))
        }
        if (returnList.isNotEmpty()) {
            cellModelList.add(returnList)
        }
    }
    return cellModelList
}

fun List<ValueRange>.toCells(): MutableList<CellModel> {
    val cellModelList = mutableListOf<CellModel>()
    for (valueRange in this) {
        valueRange.let {
            val cellPlace = it.range
            val value = it.getValues()?.get(0)?.get(0)?.toString()
            value?.let { it1 ->
                cellModelList.add(CellModel(
                    cellRow = cellPlace.toCellRow(),
                    cellColumn = cellPlace.toCellColumn(),
                    cellSheet = cellPlace.toCellSheet(),
                    value = it1))
            }
        }
    }
    return cellModelList
}

fun String.toCellSheet(): String {
    return this.substring(0, this.indexOf("!"))
}

fun String.toCellColumn(index: Int = -1): String {
    return this.substr(indexOf("!") + 1, 1).run {
        if (index != -1) {
            columnAddress(columnNumber(this) + index)
        }else{
            this
        }
    }
}

fun String.toCellRow(): Int {
    return if (this.contains(":")) {
        substring(indexOf("!") + 2, indexOf(":"))
    } else {
        substring(this.indexOf("!") + 2, this.length)
    }.toInt()
}

fun List<Any>.asColumn(): MutableList<List<Any>> {
    val gridList = mutableListOf<List<Any>>()
    for (any in this) {
        gridList.add(listOf())
    }
    return gridList
}

fun AppendValuesResponse.appendedRange(): List<Pair<Any, String>> {
    updates.updatedData.range

    val range = updates.updatedData.range
    val sheetNameEnd = range.indexOf("!")
    val columnEnd = range.indexOf(":", sheetNameEnd)
    val withColumn = if (columnEnd == -1) {
        range.substring(sheetNameEnd + 1, range.length)
    } else {
        range.substring(sheetNameEnd + 1, columnEnd)
    }
    val rowIndex = withColumn.substring(1).toInt()

    return updates.updatedData.getValues().mapIndexed { index, anies ->
        anies[0] to "${range.toCellSheet()}!${range.toCellColumn(index)}${rowIndex + index}"
    }
}

fun JSpreadsheet.updateOnSheet(
    sheetId: String,
    valueRange: List<ValueRange>,
): BatchUpdateValuesResponse {
    return sheets
        .Spreadsheets()
        .values()
        .batchUpdate(sheetId, BatchUpdateValuesRequest()
            .setValueInputOption("USER_ENTERED")
            .setData(valueRange))
        .execute()
}

fun columnAddress(col: Int): String {
    if (col <= 26) {
        return Char(col + 64).toString()
    }
    var div = col / 26
    var mod = col % 26
    if (mod == 0) {
        mod = 26
        div--
    }
    return columnAddress(div) + columnAddress(mod)
}

fun columnNumber(colAddress: String): Int {
    val digits = IntArray(colAddress.length)
    for (i in colAddress.indices) {
        digits[i] = colAddress[i].code - 64
    }
    var mul = 1
    var res = 0
    for (pos in digits.size - 1 downTo 0) {
        res += digits[pos] * mul
        mul *= 26
    }
    return res
}