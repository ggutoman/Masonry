package org.gag.appdriver.Constants

enum class API_CONSTANTS(val fsURL : String){

    URL_BASE_SERVER("http://192.165.10.9/Masonry/app/"),
    URL_LOGIN_ACCOUNT("accounts/sign_in.php"),
    URL_CREATE_ACCOUNT("accounts/sign_up.php"),
    URL_DOWNLOAD_USER("accounts/user_info.php"),

    URL_CREATE_MEMBER("member/create_member.php");
}