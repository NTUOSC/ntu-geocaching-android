package org.ntuosc.geocaching;

public final class AppConfig {

    private AppConfig() {
        // Disallow instance creation
    }

    public static final String PACKAGE_NAME = "org.ntuosc.geocaching";

    public static final String PREF_NAME = "NTUOSC_Geo";

    public static final String PREF_ENDPOINT_NAME = "endpointName";

    public static final String PREF_ENDPOINT_KEY = "endpointKey";

    public static final int CODE_NFC_REQUEST = 200;

    public static final String URL_ENDPOINT = "https://ntu-geocaching.herokuapp.com/endpoint/%s";

    public static final int CODE_SUCCESS = 0;

    public static final int CODE_ENDPOINT_INCORRECT = 400;

    public static final int CODE_GENERIC_ERROR = 500;

    public static final int CODE_NETWORK_ERROR = 502;

    public static final String DEFAULT_ENCODING = "UTF-8";

}
