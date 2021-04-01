package org.asl19.paskoocheh;

/**
 * Paskoocheh Constants.
 */
public final class Constants {

    /**
     * Cognito Pool Id.
     */
    public static final String COGNITO_POOL_ID = "us-east-1:e1593dd6-856d-49e7-a690-7787213ecfac";

    /**
     * Bucket name.
     */
    public static final String BUCKET_NAME = "paskoocheh-repo";

    /**
     * Platform.
     */
    public static final String ANDROID = "android";

    /**
     * Paskoocheh Preferences.
     */
    public static final String PASKOOCHEH_PREFS = "PASKOOCHEH_KEY";

    /**
     * Wifi Preferences Key.
     */
    public static final String DOWNLOAD_WIFI = "DOWNLOAD_ONLY_WIFI";

    /**
     * Wifi Preferences Key.
     */
    public static final String UPDATE_NOTIFICATION = "UPDATE_NOTIFIATION";
    /**
     * Ouinet Preferences Key.
     */
    public static final String OUINET_PREF = "OUINET";
    /**
     * Paskoocheh UUID.
     */
    public static final String PASKOOCHEH_UUID = "PASKOOCHEH_UUID";

    /**
     * Configuration Directory
     */
    public static final String CONFIG_DIRECTORY = "/config";

    /**
     * Version File.
     */
    public static final String APPS = "apps.json";

    /**
     * Guide And Tutorials File
     */
    public static final String GUIDES_AND_TUTORIALS = "gnt.json";

    /**
     * Faqs File
     */
    public static final String FAQS = "faqs.json";

    /**
     * Downloads and Ratings File.
     */
    public static final String DOWNLOADS_AND_RATINGS = "dnr.json";

    /**
     * Reviews File.
     */
    public static final String REVIEWS = "reviews.json";

    /**
     * Texts File.
     */
    public static final String TEXTS = "texts.json";


    /**
     * Android App Dynamo“
     */
    public static final String ANDROID_APP = "Android App";


    /**
     * Paskoocheh.
     */
    public static final String PASKOOCHEH_PACKAGE = "org.asl19.paskoocheh";


    /**
     * API
     */
    public static final String API = "bEYU3raKC03q9KUH9UJEA8O97oCabXhtv3qRawZb";

    /**
     * URL
     */
    public static final String URL = "https://api.paskoocheh.com/v1/";

    /**
     * Amazon URL
     */
    public static final String AMAZON_URL = "https://paskoocheh-api.s3.amazonaws.com";

    /**
     * Rating
     */
    public static final String RATING = "pask_rating/";

    /**
     * Download
     */
    public static final String DOWNLOAD = "pask_download/";

    /**
     * Analytics
     */
    public static final String SCREEN = "screen_name";

    public static final String OPEN_PAGE = "open_page";

    //Types
    public static final String SHARE = "share";
    public static final String TOOL_ID = "tool_id";
    public static final String INSTALL = "install_tool";
    public static final String UNINSTALL = "uninstall" ;
    public static final String PLAY_STORE = "play_store";
    public static final String REVIEW = "review";
    public static final String EMAIL = "email";
    public static final String GALLERY = "gallery";
    public static final String FEEDBACK = "feedback";
    public static final String TOOL_SELECT = "tool_selected";
    public static final String SUPPORT = "support";

    // Form Data Request
    public static final String ACL = "authenticated-read";
    public static final String X_AMZ_ALGORITHM = "AWS4-HMAC-SHA256";
    public static final String X_AMZ_CREDENTIAL = "AKIAJDNVMWXENNJFBILA/20250101/us-east-1/s3/aws4_request";
    public static final String X_AMZ_DATE = "20250101T000000Z";
    public static final String X_AMZ_SIGNATURE = "ae9924ede59673d350a7f31a5abd740327866eba096dc402e088903b177bd792";


    //AESCrypt-ObjC uses CBC and PKCS7Padding
    public static final String POLICY = "ewogICAgImV4cGlyYXRpb24iOiAiMjAyNS0wMS0wMlQwMDowMDowMFoiLAogICAgImNvbmRpdGlv" +
            "bnMiOiBbCiAgICAgICAgeyJidWNrZXQiOiAicGFza29vY2hlaC1hcGkifSwKICAgICAgICBbInN0YXJ0cy13aXRoIiwgIiRrZXkiLCAi" +
            "WndjOS9NZmVra1ZZV0JCUFpQaTZJYXBNTS9iSVQ2ZVR6dWs0VzBnZEJyMzhZd25nUnRQUktBZVJHeHRHNmpCK1h1NStyWEtoTnNUemRV" +
            "ZVZxSTdBR3BqWHN6Q1E1MU8zdHQ4WUxJdk1QTDQ9Il0sCiAgICAgICAgeyJhY2wiOiAiYXV0aGVudGljYXRlZC1yZWFkIn0sCiAgICAg" +
            "ICAgeyJ4LWFtei1jcmVkZW50aWFsIjogIkFLSUFKRE5WTVdYRU5OSkZCSUxBLzIwMjUwMTAxL3VzLWVhc3QtMS9zMy9hd3M0X3JlcXVl" +
            "c3QifSwKICAgICAgICB7IngtYW16LWFsZ29yaXRobSI6ICJBV1M0LUhNQUMtU0hBMjU2In0sCiAgICAgICAgeyJ4LWFtei1kYXRlIjog" +
            "IjIwMjUwMTAxVDAwMDAwMFoifSwKICAgICAgICBbImNvbnRlbnQtbGVuZ3RoLXJhbmdlIiwgMCwgMjA0OF0KICAgIF0KfQ==";
    public static final String AES_MODE = "AES/CBC/PKCS7Padding";
    public static final int IV_LENGTH = 16;
    public static final String KEY = "NTM2ZDQ2N2E2MjMy";
    public static final String FILENAME_PREFIX = "Zwc9/MfekkVYWBBPZPi6IapMM/bIT6eTzuk4W0gdBr38YwngRtPRKAeRGxtG6jB+Xu5" +
            "+rXKhNsTzdUeVqI7AGpjXszCQ51O3tt8YLIvMPL4=%s%s";

    // LOCALE CONSTANTS
    public static final String EN = "en";
    public static final String FA = "fa";

    // SERIVCE CONSTANTS
    public static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".provider";
    public static final String ASC = ".asc";
    public static final String TEMP = ".temp";

    public static final String PRIMARY_CHANNEL = "pask";

    private Constants() {
    }
}
