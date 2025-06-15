package com.example.olaclass.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys; // Thêm thư viện mã hóa
// import android.preference.PreferenceManager; (loại bỏ dòng này)
import java.util.Locale;

public class LanguageManager {
    private static final String PREF_LANGUAGE = "app_language";

    public static void setLocale(Context context, String language) {
        persistLanguage(context, language);
        updateResources(context, language);
    }

    public static String getLanguage(Context context) {
        SharedPreferences prefs = getEncryptedPrefs(context);
        return prefs.getString(PREF_LANGUAGE, Locale.getDefault().getLanguage());
    }

    private static void persistLanguage(Context context, String language) {
        SharedPreferences prefs = getEncryptedPrefs(context);
        prefs.edit().putString(PREF_LANGUAGE, language).apply();
    }

    public static void updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(locale);
        } else {
            config.locale = locale;
        }
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }
}
