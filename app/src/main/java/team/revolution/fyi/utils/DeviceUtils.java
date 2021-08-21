package team.revolution.fyi.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

public class DeviceUtils {
    public static boolean isNetworkConnected = false;

    public static boolean isGetAccount = false;

    public static boolean isTablet(@NonNull Context context) {
        return ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE);
    }

    public static void hideKeyboard(Activity activity) {
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public static void showKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public static boolean isDarkTheme(Context context) {
        return (context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)  == Configuration.UI_MODE_NIGHT_YES;
    }

    public static boolean isGetConnection(Context context) {
        ConnectivityManager mgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert mgr != null;
        NetworkInfo networkInfo = mgr.getActiveNetworkInfo();
        if (networkInfo != null) {
            return networkInfo.isConnected();
        } else {
            return false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static boolean isTimeAutomatic(Context context) {
        return Settings.Global.getInt(context.getContentResolver(), Settings.Global.AUTO_TIME, 0) == 1;
    }

    @SuppressLint("HardwareIds")
    public static String android_ID(Activity activity) {
        return Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}
