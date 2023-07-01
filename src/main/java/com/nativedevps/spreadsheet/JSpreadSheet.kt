package com.nativedevps.spreadsheet

import android.content.Context
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.api.services.sheets.v4.model.*
import com.nativedevps.GoogleAuthentication
import com.nativedevps.utility.getGoogleAccountCredential

class JSpreadsheet constructor(
    private val context: Context,
) : GoogleAuthentication(context) {

    private var applicationName: String = "Spreadsheet"
    private val googleAccountCredential: GoogleAccountCredential? by lazy {
        return@lazy context.getGoogleAccountCredential(listOf(SheetsScopes.SPREADSHEETS))
    }

    val sheets: Sheets by lazy {
        return@lazy Sheets.Builder(
            NetHttpTransport(),
            GsonFactory.getDefaultInstance(),
            googleAccountCredential
        ).setApplicationName(applicationName).build()
    }

    fun setApplicationName(applicationName: String) {
        this.applicationName = applicationName
    }

    /**
     * eg. "17wXoV0IfUghXdIgfn0MBV6ZFhEcL1ZR9TAsAc6RvXLk"
     **/
    fun retrieveSheet(
        sheetId: String,
    ): UselessJob<Spreadsheet> {
        return runGoogleClientRequest {
            sheets.spreadsheets().get(sheetId).execute()
            //throw UserRecoverableAuthIOException(UserRecoverableAuthException("some intent", Intent()))
//            throw java.lang.IllegalStateException("cat touched it")
        }
    }

    fun createSpreadsheet(
        onEditAction: ((Spreadsheet) -> Unit)?,
    ): UselessJob<Spreadsheet> {
        val spreadsheet = Spreadsheet()
        val spreadsheetProperties = SpreadsheetProperties()
        spreadsheetProperties.title =
            "Spreadsheet - $applicationName"
        spreadsheet.properties = spreadsheetProperties
        onEditAction?.invoke(spreadsheet)

        return runGoogleClientRequest {
            sheets.spreadsheets()
                .create(spreadsheet)
                .execute()
        }
    }

    /**
     * eg. "17wXoV0IfUghXdIgfn0MBV6ZFhEcL1ZR9TAsAc6RvXLk"
     * eg. E!, E4
     *
     * https://developers.google.com/sheets/api/reference/rest/v4/ValueRenderOption
     **/
    fun retrieveCellsOfSheet(
        sheetId: String,
        range: List<String>,
        renderOption:RenderOption = RenderOption.FORMULA
    ): UselessJob<BatchGetValuesResponse> {
        return runGoogleClientRequest {
            sheets.spreadsheets()
                .values()
                .batchGet(sheetId)
                .setValueRenderOption(renderOption.name)
                .setRanges(range)
                .execute()
        }
    }

    fun batchUpdateRequest(
        sheetId: String,
        requestList: List<Request>,
    ): UselessJob<BatchGetValuesResponse> {
        return runGoogleClientRequest {
            sheets.spreadsheets()
                .batchUpdate(sheetId, BatchUpdateSpreadsheetRequest().setRequests(requestList))
                .execute()
        }
    }

    fun batchClearSheet(
        sheetId: String,
        sheetList: List<String>,
    ): UselessJob<BatchClearValuesResponse> {
        return runGoogleClientRequest {
            sheets.spreadsheets().values()
                .batchClear(sheetId, BatchClearValuesRequest().setRanges(sheetList)).execute()
        }
    }

    fun clearRange(
        sheetId: String,
        range: String,
        clearValuesRequest: ClearValuesRequest = ClearValuesRequest(),
    ): UselessJob<BatchClearValuesResponse> {
        return runGoogleClientRequest {
            sheets
                .spreadsheets()
                .values()
                .clear(sheetId, range, clearValuesRequest)
                .execute()
        }
    }
}

/*
* Enums
FORMATTED_VALUE	Values will be calculated & formatted in the reply according to the cell's formatting. Formatting is based on the spreadsheet's locale, not the requesting user's locale. For example, if A1 is 1.23 and A2 is =A1 and formatted as currency, then A2 would return "$1.23".
UNFORMATTED_VALUE	Values will be calculated, but not formatted in the reply. For example, if A1 is 1.23 and A2 is =A1 and formatted as currency, then A2 would return the number 1.23.
FORMULA	Values will not be calculated. The reply will include the formulas. For example, if A1 is 1.23 and A2 is =A1 and formatted as currency, then A2 would return "=A1".
*
* */
enum class RenderOption {
    FORMULA,
    UNFORMATTED_VALUE,
    FORMATTED_VALUE
}

class UselessJob<T> {
    private var uselessInterface: UselessInterface<T>? = null

    fun addOnCompletion(uselessInterface: UselessInterface<T>) {
        this.uselessInterface = uselessInterface
    }

    fun setResult(spreadsheet: T? = null, exception: Exception? = null) {
        uselessInterface?.onCompletion(spreadsheet, exception)
    }

    interface UselessInterface<T> {
        fun onCompletion(spreadsheet: T?, exception: Exception?)
    }
}