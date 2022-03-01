package app.mynta.console.android.constants;

public class API {
    // api endpoint
    public static final String API_URL = "https://api.mynta.app/v1/service/";

    // api requests
    public static final String REQUEST_REGISTER = "credentials/request_register.inc.php";
    public static final String REQUEST_LOGIN = "credentials/request_login.inc.php";
    public static final String REQUEST_LOGIN_WITH_QR = "credentials/request_login_with_qr.inc.php";
    public static final String REQUEST_TWO_FACTOR_AUTH = "credentials/request_verify_two_factor_auth.inc.php";
    public static final String REQUEST_SEND_RESET_EMAIL = "credentials/reset_password/request_send_email.inc.php";
    public static final String REQUEST_VERIFY_CODE = "credentials/reset_password/request_verify_code.inc.php";
    public static final String REQUEST_RESET_PASSWORD = "credentials/reset_password/request_reset_password.inc.php";

    public static final String REQUEST_VERIFY_PROJECT = "projects/request_verify_project.inc.php";
    public static final String REQUEST_CREATE_PROJECT = "projects/request_create_project.inc.php";
    public static final String REQUEST_PROJECTS = "projects/request_projects.inc.php";

    public static final String REQUEST_HC_SECTIONS = "help_centre/request_sections.inc.php";
    public static final String REQUEST_HC_ARTICLES = "help_centre/request_articles.inc.php";
    public static final String REQUEST_HC_ARTICLE = "help_centre/request_article.inc.php";
    public static final String REQUEST_HC_COMMENTS = "help_centre/request_comments.inc.php";
    public static final String REQUEST_POST_HC_COMMENT = "help_centre/request_post_comment.inc.php";
    public static final String REQUEST_REMOVE_HC_COMMENT = "help_centre/request_remove_comment.inc.php";
    public static final String REQUEST_UPDATE_HC_COMMENT = "help_centre/request_update_comment.inc.php";

    public static final String REQUEST_MEMBERS = "members/request_members.inc.php";
    public static final String REQUEST_MEMBER_DETAILS = "members/request_member_details.inc.php";
    public static final String REQUEST_DELETE_MEMBER = "members/request_remove_member.inc.php";

    public static final String REQUEST_THEME_STYLE = "theme_style/request_theme_style.inc.php";
    public static final String REQUEST_UPDATE_THEME_STYLE = "theme_style/request_update_theme_style.inc.php";

    public static final String REQUEST_WALKTHROUGH = "walkthrough/request_walkthrough.inc.php";
    public static final String REQUEST_ADD_WALKTHROUGH = "walkthrough/request_add_walkthrough.inc.php";
    public static final String REQUEST_UPDATE_WALKTHROUGH = "walkthrough/request_update_walkthrough.inc.php";
    public static final String REQUEST_REMOVE_WALKTHROUGH = "walkthrough/request_remove_walkthrough.inc.php";
    public static final String REQUEST_UPDATE_WALKTHROUGH_OPTION = "walkthrough/request_update_walkthrough_option.inc.php";

    public static final String REQUEST_ACCOUNT_DETAILS = "credentials/manage_account/request_account_details.inc.php";
    public static final String REQUEST_UPDATE_ACCOUNT = "credentials/manage_account/request_update_account.inc.php";
    public static final String REQUEST_UPDATE_PASSWORD = "credentials/manage_account/request_update_password.inc.php";

    public static final String REQUEST_ONESIGNAL_SETTINGS = "push_notifications/request_onesignal_settings.inc.php";
    public static final String REQUEST_SAVE_ONESIGNAL_SETTINGS = "push_notifications/request_save_onesignal_settings.inc.php";



    public static final String REQUEST_REMOVE_PROVIDER = "providers/request_remove_provider.inc.php";
    public static final String REQUEST_SAVE_FIREBASE_SETTINGS = "push_notifications/request_save_firebase_settings.inc.php";
    public static final String REQUEST_CONFIGURATION = "request_configuration.inc.php";
    public static final String REQUEST_SEND_FIREBASE_NOTIFICATION = "push_notifications/request_send_firebase_notification.inc.php";
    public static final String REQUEST_SEND_ONESIGNAL_NOTIFICATION = "push_notifications/request_send_onesignal_notification.inc.php";
    public static final String REQUEST_PRIVACY_POLICY = "app_information/request_privacy_policy.inc.php";
    public static final String REQUEST_TERMS_OF_USE = "app_information/request_terms_of_use.inc.php";
    public static final String REQUEST_UPDATE_PRIVACY_POLICY = "app_information/request_update_privacy_policy.inc.php";
    public static final String REQUEST_UPDATE_TERMS_OF_USE = "app_information/request_update_terms_of_use.inc.php";
    public static final String REQUEST_CUSTOM_CSS = "request_custom_css.inc.php";
    public static final String REQUEST_DARK_MODE = "request_darkmode.inc.php";
    public static final String REQUEST_UPDATE_CUSTOM_CSS = "request_update_custom_css.inc.php";
    public static final String REQUEST_UPDATE_DARK_MODE = "request_update_darkmode.inc.php";
    public static final String REQUEST_UPDATE_DARK_MODE_OPTION = "configuration/request_update_darkmode_option.inc.php";

    public static final String REQUEST_UPDATE_NOTIFICATION_PREFERENCES_OPTION = "configuration/request_update_notifications_preferences_option.inc.php";
    public static final String REQUEST_NOTIFICATIONS_PREFERENCES = "request_notifications_preferences.inc.php";
    public static final String REQUEST_PROVIDER_MENUS = "providers/request_provider_menus.inc.php";
    public static final String REQUEST_WEBVIEW_DETAILS = "providers/request_webview_provider.inc.php";
    public static final String REQUEST_ADD_WEBVIEW_PROVIDER = "providers/request_add_webview_provider.inc.php";
    public static final String REQUEST_LOGIN_PROVIDERS = "members/login_providers/request_login_providers.inc.php";
    public static final String REQUEST_UPDATE_LOGIN_PROVIDERS = "app/request_update_login_providers.inc.php";
    public static final String REQUEST_UPDATE_WEBVIEW = "providers/request_update_webview_provider.inc.php";
    public static final String REQUEST_FACEBOOK_DETAILS = "providers/request_facebook_provider.inc.php";
    public static final String REQUEST_UPDATE_FACEBOOK_PROVIDER = "providers/request_update_facebook_provider.inc.php";
    public static final String REQUEST_ADD_FACEBOOK_PROVIDER = "providers/request_add_facebook_provider.inc.php";
    public static final String REQUEST_YOUTUBE_DETAILS = "providers/request_youtube_provider.inc.php";
    public static final String REQUEST_UPDATE_YOUTUBE_PROVIDER = "providers/request_update_youtube_provider.inc.php";
    public static final String REQUEST_ADD_YOUTUBE_PROVIDER = "providers/request_add_youtube_provider.inc.php";
    public static final String REQUEST_VIMEO_DETAILS = "providers/request_vimeo_provider.inc.php";
    public static final String REQUEST_UPDATE_VIMEO_PROVIDER = "providers/request_update_vimeo_provider.inc.php";
    public static final String REQUEST_ADD_VIMEO_PROVIDER = "providers/request_add_vimeo_provider.inc.php";
    public static final String REQUEST_PINTEREST_DETAILS = "providers/request_pinterest_provider.inc.php";
    public static final String REQUEST_UPDATE_PINTEREST_PROVIDER = "providers/request_update_pinterest_provider.inc.php";
    public static final String REQUEST_ADD_PINTEREST_PROVIDER = "providers/request_add_pinterest_provider.inc.php";
    public static final String REQUEST_IMGUR_DETAILS = "providers/request_imgur_provider.inc.php";
    public static final String REQUEST_UPDATE_IMGUR_PROVIDER = "providers/request_update_imgur_provider.inc.php";
    public static final String REQUEST_ADD_IMGUR_PROVIDER = "providers/request_add_imgur_provider.inc.php";
    public static final String REQUEST_MAPS_DETAILS = "providers/request_google_maps_provider.inc.php";
    public static final String REQUEST_UPDATE_MAPS_PROVIDER = "providers/request_update_google_maps_provider.inc.php";
    public static final String REQUEST_ADD_MAPS_PROVIDER = "providers/request_add_google_maps_provider.inc.php";
    public static final String REQUEST_UPDATE_PROVIDERS = "providers/request_update_providers.inc.php";
    public static final String REQUEST_WORDPRESS_DETAILS = "providers/request_wordpress_provider.inc.php";
    public static final String REQUEST_MANAGE_UPDATES = "manage_updates/request_manage_updates.inc.php";
    public static final String REQUEST_UPDATE_WORDPRESS = "providers/request_update_wordpress_provider.inc.php";
    public static final String REQUEST_ADD_WORDPRESS_PROVIDER = "providers/request_add_wordpress_provider.inc.php";
    public static final String REQUEST_UPDATE_MANAGE_UPDATES = "manage_updates/request_update_manage_updates.inc.php";

    public static final String REQUEST_SUBMIT_TICKET = "help_centre/request_submit_request.inc.php";
    public static final String REQUEST_OPENED_TICKETS = "help_centre/request_opened_tickets.inc.php";
    public static final String REQUEST_CLOSED_TICKETS = "help_centre/request_closed_tickets.inc.php";
    public static final String REQUEST_UPDATE_TICKET = "help_centre/request_update_ticket.inc.php";
    public static final String REQUEST_CLOSE_TICKET = "help_centre/request_close_ticket.inc.php";
    public static final String REQUEST_TICKET = "help_centre/request_ticket.inc.php";
    public static final String REQUEST_SEARCH_ARTICLES = "help_centre/request_search_articles.inc.php";
    public static final String REQUEST_UPDATE_PROJECT = "projects/request_update_project.inc.php";
    public static final String REQUEST_UPDATE_AD_PROVIDER = "manage_ads/request_update_ad_provider.inc.php";
    public static final String REQUEST_VERSION = "app/request_version.inc.php";
    public static final String REQUEST_PROMOTION = "app/request_promotion.inc.php";
    public static final String REQUEST_UPDATE_PROJECT_VISITS = "app/request_update_project_visits.inc.php";
    public static final String REQUEST_UPDATE_EMAIL = "app/request_update_email.inc.php";
    public static final String REQUEST_UPDATE_PROVIDER = "providers/request_update_default_provider.inc.php";
    public static final String REQUEST_PRODUCTS = "store/request_products.inc.php";
    public static final String REQUEST_RECOMMENDED_PRODUCTS = "store/request_recommended_products.inc.php";
    public static final String REQUEST_STORE_REVIEWS = "store/request_reviews.inc.php";
    public static final String REQUEST_PRODUCT = "store/request_product.inc.php";
    public static final String REQUEST_TICKET_RESPONDS = "help_centre/request_ticket_respond.inc.php";
    public static final String REQUEST_PURCHASE_PRODUCT = "store/request_purchase_product.inc.php";
    public static final String REQUEST_PURCHASES = "store/request_purchases.inc.php";
    public static final String REQUEST_SERVER_STATUS = "status/request_check_status.inc.php";
    public static final String REQUEST_POST_REVIEW = "store/request_post_review.inc.php";
    public static final String REQUEST_DELETE_REVIEW = "store/request_delete_review.inc.php";
    public static final String REQUEST_SETUP_POLICIES = "store/powerups/policies/request_setup_policies.inc.php";
    public static final String REQUEST_REDEEM_PROMO_CODE = "store/request_redeem_promo_code.inc.php";
    public static final String REQUEST_ABOUT_APP = "app_information/request_about_app.inc.php";
    public static final String REQUEST_UPDATE_ABOUT_APP = "app_information/request_update_about_app.inc.php";
    public static final String REQUEST_SHARE_CONTENT = "app_information/request_share_content.inc.php";
    public static final String REQUEST_UPDATE_SHARE_CONTENT = "app_information/request_update_share_content.inc.php";
}