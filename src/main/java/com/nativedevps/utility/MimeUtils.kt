package com.nativedevps.utility

/* https://developers.google.com/drive/api/guides/ref-export-formats */
object CommonMime {
    val mimeList = listOf(
        DRIVE_MIME_EXCEL,
        DRIVE_MIME_CSV,
        DRIVE_MIME_PDF,
        DRIVE_MIME_ZIP,
        DRIVE_MIME_ODS
    )

    val DRIVE_MIME_EXCEL: FileExtension get() = FileExtension("Excel","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "xlsx")
    val DRIVE_MIME_CSV: FileExtension get() = FileExtension("Comma Separated Values","text/csv", "csv")
    val DRIVE_MIME_PDF: FileExtension get() = FileExtension("PDF","application/pdf", "pdf")
    val DRIVE_MIME_ZIP: FileExtension get() = FileExtension("Web Page","application/zip", "zip")
    val DRIVE_MIME_ODS: FileExtension get() = FileExtension("OpenDocument","application/x-vnd.oasis.opendocument.spreadsheet", "ods")
}


data class FileExtension(val name: String, val mime: String, val extension: String) {
    companion object {
        fun byExtension(extension: String): FileExtension {
            return CommonMime.mimeList.firstOrNull {
                return@firstOrNull it.extension.lowercase() == extension.lowercase()
            }?: error("Please make sure mime present at list")
        }
    }
}