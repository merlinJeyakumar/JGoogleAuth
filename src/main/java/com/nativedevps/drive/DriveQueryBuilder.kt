package com.nativedevps.drive

/*
* mimeType = 'application/vnd.google-apps.spreadsheet' and name contains '${applicationName}' and trashed = false
* docs: https://developers.google.com/drive/api/guides/search-files
* */
class DriveQueryBuilder {
    private var query: StringBuilder = StringBuilder()

    fun setMime(mime: String): DriveQueryBuilder {
        query.append("mimeType = '$mime'")
        return this
    }

    fun and(): DriveQueryBuilder {
        query.append(" and ")
        return this
    }

    fun contains(field: String = "name", text: String): DriveQueryBuilder {
        query.append("$field contains '$text'")
        return this
    }

    fun trashed(boolean: Boolean): DriveQueryBuilder {
        query.append("trashed = $boolean")
        return this
    }

    fun clear(): DriveQueryBuilder {
        query.clear()
        return this
    }

    fun build(): String {
        return query.toString()
    }

    override fun toString(): String {
        return query.toString()
    }
}

object DriveMimeTypes {
    val mimeSpreadsheet = "application/vnd.google-apps.spreadsheet"
}