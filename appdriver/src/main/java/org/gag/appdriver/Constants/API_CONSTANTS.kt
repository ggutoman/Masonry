package org.gag.appdriver.Constants

enum class API_CONSTANTS(val fsURL : String){

    /**ACCOUNTS**/
    URL_BASE_SERVER("http://192.165.29.135/Masonry/app/"),
//    URL_BASE_SERVER("http://192.168.0.49/Masonry/app/"),
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
    URL_GET_OFFICER_INFO("member/officer_info.php"),
    URL_GET_OFFICERS("member/officers_list.php"),
    URL_GET_OFFICER_HISTORY("member/officer_history.php"),

    /**PARAMETERS**/
    URL_GET_LODGE("parameters/lodge_info.php"),
    URL_GET_POSITION("parameters/position_info.php"),
    URL_GET_TITLE("parameters/title_info.php"),
    URL_GET_PROVINCE("parameters/province_info.php"),
    URL_GET_TOWN("parameters/town_city_info.php"),
    URL_CREATE_LODGE("parameters/save_lodge_info.php"),
    URL_CREATE_LODGE_CALENDAR("parameters/save_calendar_year.php"),
    URL_GET_LODGE_CALENDAR("parameters/calendar_year.php"),

    /**ACCOUNTING**/
    URL_CREATE_FUND_TURNOVER("accounting/funds/new_fund_turnover.php"),
    URL_UPDATE_FUND_TURNOVER("accounting/funds/update_fund_turnover.php"),
    URL_APPROVE_FUND_TURNOVER("accounting/funds/approve_fund_turnover.php"),
    URL_DOWNLOAD_FUND_INFO("accounting/funds/lodge_fund_info.php"),
    URL_DOWNLOAD_LODGE_FUNDS("accounting/funds/lodge_fund_list.php"),
    URL_CREATE_FUND_HISTORY("accounting/funds/fund_turnover_info.php"),
    URL_DOWNLOAD_ANNUAL_DUES("accounting/annual/annual_dues_info.php"),
    URL_SAVE_ANNUAL_DUES("accounting/annual/save_annual_dues.php"),
    URL_APPROVE_ANNUAL_DUES("accounting/annual/approve_annual_dues.php"),
    URL_DOWNLOAD_MEMBER_ANNUALS("accounting/annual/member_annual_due.php"),
    URL_SAVE_PROJECT("accounting/projects/save_project.php"),
    URL_DOWNLOAD_PROJECTS("accounting/projects/project_list.php"),
    URL_DOWNLOAD_LODGE_PROJECTS("accounting/projects/project_lodge_list.php"),
    URL_DOWNLOAD_PROJECT_INFO("accounting/projects/project_info.php"),
    URL_DOWNLOAD_PROJECT_DETAILS("accounting/projects/project_pledges_info.php"),
    URL_DOWNLOAD_PROJECT_MEMBER("accounting/projects/member_pledges_info.php"),
    URL_UPDATE_PROJECT_STATUS("accounting/projects/project_status_update.php"),
    URL_DOWNLOAD_RECEIPT("accounting/receipts/receipt_info.php"),
    URL_DOWNLOAD_RECEIPT_LIST("accounting/receipts/receipt_list_info.php"),
    URL_SAVE_RECEIPT("accounting/receipts/save_receipt.php"),
    URL_UPDATE_RECEIPT_STATUS("accounting/receipts/update_receipt_status.php"),
    URL_SAVE_DISBURSEMENT("accounting/disbursement/save_disbursement.php"),
    URL_DOWNLOAD_DISBURSEMENT_LIST("accounting/disbursement/disbursement_list_info.php"),
    URL_DOWNLOAD_DISBURSEMENT_INFO("accounting/disbursement/disbursement_info.php"),
    URL_UPDATE_DISBURSEMENT_STATUS("accounting/disbursement/update_disbursement_status.php"),
    URL_UPLOAD_DV_ATTACHMENT("accounting/disbursement/attach_document.php"),
    URL_DOWNLOAD_DV_ATTACHMENT("accounting/disbursement/download_attach_document.php"),
    URL_DOWNLOAD_DV_ATTACHMENT_LIST("accounting/disbursement/view_attach_document.php"),
    URL_DOWNLOAD_SOA("reports/soa.php"),
    URL_DOWNLOAD_PROJ_BALANCE("reports/outstanding_balances.php")
}