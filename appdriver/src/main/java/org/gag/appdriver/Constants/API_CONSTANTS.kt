package org.gag.appdriver.Constants

enum class API_CONSTANTS(val fsURL : String){

    /**ACCOUNTS**/
    URL_BASE_SERVER("http://192.165.29.78/Masonry/app/"),
//    URL_BASE_SERVER("http://192.168.100.22/Masonry/app/"),
    URL_LOGIN_ACCOUNT("accounts/sign_in.php"),
    URL_CREATE_ACCOUNT("accounts/sign_up.php"),
    URL_DOWNLOAD_USER("accounts/user_info.php"),
    URL_UPDATE_USER("accounts/user_credential_update.php"),

    /**MEMBER**/
    URL_CREATE_MEMBER("member/save_member_info.php"),
    URL_CREATE_ADDRESS("member/save_address.php"),
    URL_CREATE_CONTACT("member/save_contact.php"),
    URL_CREATE_EMAIL("member/save_email.php"),
    URL_GET_MEMBERS("member/member_list.php"),
    URL_GET_MEMBER_ADDRESS("member/address_info.php"),
    URL_GET_MEMBER_CONTACT("member/contact_info.php"),
    URL_GET_MEMBER_EMAIL("member/email_info.php"),
    URL_UPDATE_OFFICER("member/update_officer.php"),
    URL_GET_OFFICERS("member/officers_list.php"),
    URL_GET_OFFICER_HISTORY("member/officer_history.php"),

    /**PARAMETERS**/
    URL_GET_LODGE("parameters/lodge_info.php"),
    URL_GET_POSITION("parameters/position_info.php"),
    URL_GET_TITLE("parameters/title_info.php"),
    URL_GET_PROVINCE("parameters/province_info.php"),
    URL_GET_TOWN("parameters/town_city_info.php"),
    URL_CREATE_LODGE_CALENDAR("parameters/save_calendar_year.php"),
    URL_GET_LODGE_CALENDAR("parameters/calendar_year.php")
}