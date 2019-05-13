package com.meuvesti.cliente.utils;

/**
 * Created by hersonrodrigues on 24/01/17.
 */
public class Globals {

    // MEXA AQUI
    public static final String SCHEME_NAME = "brascol";
    // MEXA AQUI

    public static final String HOMOLOG_SOCIAL_PREFIX_URL = "hapisocial.homolog.vesti.mobi";
    public static final String HOMOLOG_SOCIAL_GOOGLE_URL = "https://" + HOMOLOG_SOCIAL_PREFIX_URL + "/google?scheme_url=" + SCHEME_NAME;
    public static final String HOMOLOG_SOCIAL_FACEBOOK_URL = "https://" + HOMOLOG_SOCIAL_PREFIX_URL + "/facebook?scheme_url=" + SCHEME_NAME;
    public static final String PRODUCAO_SOCIAL_PREFIX_URL = "apisocial.vesti.mobi";
    public static final String PRODUCAO_SOCIAL_GOOGLE_URL = "https://" + PRODUCAO_SOCIAL_PREFIX_URL + "/google?scheme_url=" + SCHEME_NAME;
    public static final String PRODUCAO_SOCIAL_FACEBOOK_URL = "https://" + PRODUCAO_SOCIAL_PREFIX_URL + "/facebook?scheme_url=" + SCHEME_NAME;

    public static final String DATABASE_NAME = "compras.realm";
    public static final long DATABASE_VERSION = 0;
    // MEXA AQUI
    public static final String URL_SOCIAL_PREFIX = PRODUCAO_SOCIAL_PREFIX_URL;
    public static final String URL_SOCIAL_GOOGLE_URL = PRODUCAO_SOCIAL_GOOGLE_URL;
    public static final String URL_SOCIAL_FACEBOOK_URL = PRODUCAO_SOCIAL_FACEBOOK_URL;
    private static final String HOMOLOG_URL = "https://hapi.meuvesti.com";
    private static final String PRODUCAO_URL = "https://api.meuvesti.com";
    public static final String URL_APP = PRODUCAO_URL;
    // MEXA AQUI

    public static boolean isHomolog() {
        return URL_APP.equals(HOMOLOG_URL);
    }
}
