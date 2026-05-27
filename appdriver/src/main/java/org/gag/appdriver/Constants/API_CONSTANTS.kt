package org.gag.appdriver.Constants

enum class API_CONSTANTS(val fsURL : String){

    URL_BASE_SERVER("http://192.168.100.22/Masonry/app/"),
    URL_LOGIN_ACCOUNT("accounts/sign_in.php"),
    URL_CREATE_ACCOUNT("accounts/sign_up.php"),
    URL_DOWNLOAD_USER("accounts/user_info.php"),
    URL_UPDATE_ACCOUNT("accounts/user_credential_update.php")
}