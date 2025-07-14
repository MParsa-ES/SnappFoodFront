package org.example.snappfoodfront.Utils;

import java.util.prefs.Preferences;

public class TokenManager {

    private static final Preferences prefs = Preferences.userNodeForPackage(TokenManager.class);
    private static final String TOKEN_KEY = "auth_token";

    public static void saveToken(String token) {
        if (token != null) {
            prefs.put(TOKEN_KEY, token);
        }
    }

    public static String getToken() {
        return prefs.get(TOKEN_KEY, null);
    }

    public static void clearToken() {
        prefs.remove(TOKEN_KEY);
    }

}
