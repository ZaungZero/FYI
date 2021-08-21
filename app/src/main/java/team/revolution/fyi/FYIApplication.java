package team.revolution.fyi;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.database.FirebaseDatabase;
import com.mukesh.tinydb.TinyDB;

import team.revolution.fyi.utils.Constant;

public class FYIApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        TinyDB tinyDB = new TinyDB(this);
        if (tinyDB.getString(Constant.THEME).equals(Constant.DARK_MODE))
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else if (!tinyDB.getString(Constant.THEME).equals(Constant.LIGHT_MODE))
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        else if (tinyDB.getString(Constant.THEME).equals(Constant.SYSTEM_MODE))
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }
}
