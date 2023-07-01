package com.nativedevps.drive

import android.content.Context
import android.util.Log
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.FileList
import com.nativedevps.GoogleAuthentication
import com.nativedevps.spreadsheet.UselessJob
import com.nativedevps.utility.getGoogleAccountCredential
import java.io.FileOutputStream

class JGoogleDrive constructor(
    private val context: Context,
) : GoogleAuthentication(context) {
    private var applicationName: String = "Drive"
    private val googleAccountCredential: GoogleAccountCredential? by lazy {
        return@lazy context.getGoogleAccountCredential(
            listOf(DriveScopes.DRIVE_FILE)
        )
    }
    private val drive: Drive by lazy {
        return@lazy Drive.Builder(
            NetHttpTransport(),
            GsonFactory.getDefaultInstance(),
            googleAccountCredential
        ).setApplicationName(applicationName).build()
    }

    fun setApplicationName(applicationName: String) {
        this.applicationName = applicationName
    }

    fun retrieveFiles(
        query: String = "mimeType = 'application/vnd.google-apps.spreadsheet' and trashed = false",
        pageToken: String? = null,
        pageSize: Int = 1000,
    ): UselessJob<FileList?> {
        Log.d("JeyK", "query: $query")

        return runGoogleClientRequest {
            drive.files().list().apply {
                this.pageToken = pageToken
                this.pageSize = pageSize
                this.q = query
                this.fields = "nextPageToken, files(id, name)";
            }.execute()
        }
    }

    fun downloadFile(
        fileId: String,
        mime: String,
        outputStream: FileOutputStream,
    ): UselessJob<Drive.Files.Export> {
        return runGoogleClientRequest<Drive.Files.Export> {
            return@runGoogleClientRequest drive.files().export(fileId, mime)
                .executeMediaAndDownloadTo(outputStream)
        }
    }

}

enum class AuthenticationState {
    success,
    failed,
    cancelled
}