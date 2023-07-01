## JSpreadSheet & JGoogleDrive

Provides easy option to implement a spreadsheet and google drive with the android app

## Implementation

Google authentication result requires to be called when ever not in authentication, to know and
update authentication event to JSpreadsheet and JGoogleDrive, its need to be **implemented on your
Activity**
``
private var authenticationResultLauncher: ActivityResultLauncher<Intent> = authenticationInterface.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
    if (result.resultCode == Activity.RESULT_OK) { //DriveState.authenticated
            setGoogleAuthenticationResult(AuthenticationState.success)
    } else { //DriveState.cancelled
            setGoogleAuthenticationResult(AuthenticationState.cancelled)
    }
}
``

# Etc,

Below methods are useful for implementing JGoogleDrive and JSpreadSheet within the activity and
updating result event,

``
    override fun launch(intent: Intent) {
        authenticationResultLauncher.launch(intent)
    }
    override fun setGoogleAuthenticationResult(authenticationState: AuthenticationState) {
        googleDrive.setGoogleAuthenticationResult(authenticationState)
        spreadSheet.setGoogleAuthenticationResult(authenticationState)
    }
``