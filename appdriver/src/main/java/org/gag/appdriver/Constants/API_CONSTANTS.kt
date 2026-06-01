package org.gag.appdriver.Constants

enum class API_CONSTANTS(val fsURL : String){

    /**ACCOUNTS**/
    URL_BASE_SERVER("http://192.165.29.190/Masonry/app/"),
//    URL_BASE_SERVER("http://192.168.100.22/Masonry/app/"),
    URL_LOGIN_ACCOUNT("accounts/sign_in.php"),
    URL_CREATE_ACCOUNT("accounts/sign_up.php"),
    URL_DOWNLOAD_USER("accounts/user_info.php"),
    URL_UPDATE_USER("accounts/user_credential_update.php"),
    URL_CREATE_MEMBER("accounts/save_member_info.php"),
    URL_CREATE_ADDRESS("accounts/save_address.php"),
    URL_CREATE_CONTACT("accounts/save_contact.php"),
    URL_CREATE_EMAIL("accounts/save_email.php"),

    /**PARAMETERS**/
    URL_GET_LODGE("parameters/lodge_info.php"),
    URL_GET_POSITION("parameters/position_info.php"),
    URL_GET_TITLE("parameters/title_info.php"),
    URL_GET_PROVINCE("parameters/province_info.php"),
    URL_GET_TOWN("parameters/town_city_info.php")
}