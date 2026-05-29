package org.gag.appdriver.Constants

enum class API_CONSTANTS(val fsURL : String){

    /**ACCOUNTS**/
    URL_BASE_SERVER("http://192.165.29.248/Masonry/app/"),
    URL_LOGIN_ACCOUNT("accounts/sign_in.php"),
    URL_CREATE_ACCOUNT("accounts/sign_up.php"),
    URL_DOWNLOAD_USER("accounts/user_info.php"),
    URL_UPDATE_USER("accounts/user_credential_update.php"),

    /**PARAMETERS**/
    URL_GET_LODGE("parameters/lodge_info.php"),
    URL_GET_POSITION("parameters/position_info.php")
}