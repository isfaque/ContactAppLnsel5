package apps.lnsel.com.contactapp;

/**
 * Created by apps2 on 7/12/2017.
 */
public class WebServiceUrls {
    public static String MAIN_URL = "http://61.16.131.205/phonebook/";
    public static String BASE_URL = MAIN_URL + "Api/";

    //for signup
    public static String SIGNUP_URL = BASE_URL+"user_signup";

    //for login
    public static String LOGIN_URL = BASE_URL+"user_login";

    //for add contact
    public static String ADD_CONTACT_URL = BASE_URL+"add_contact";

    //for get contacts
    public static String GET_CONTACTS_URL = BASE_URL+"get_contacts";
}
