package team.revolution.fyi.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatSeekBar;

import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.textview.MaterialTextView;
import com.mukesh.tinydb.TinyDB;

import java.util.Objects;

import team.revolution.fyi.R;
import team.revolution.fyi.utils.Constant;

public class SettingActivity extends AppCompatActivity {
    private MaterialTextView textView_sample, textView_count;
    private AppCompatSeekBar appCompatSeekBar;
    private RadioGroup radioGroup_theme;
    private MaterialRadioButton materialRadioButton_dark, materialRadioButton_light, materialRadioButton_system;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setTitle("Settings");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        defineId();

        TinyDB tinyDB = new TinyDB(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            appCompatSeekBar.setMin(12);
        }
        appCompatSeekBar.setMax(30);

        appCompatSeekBar.setProgress(tinyDB.getInt(Constant.FONT_SIZE));

        textView_count.setText(String.valueOf(tinyDB.getInt(Constant.FONT_SIZE)));

        appCompatSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress >= 12 && progress <= 30) {
                    textView_count.setText(String.valueOf(progress));
                    textView_sample.setTextSize(progress);
                    tinyDB.putInt(Constant.FONT_SIZE, progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        if (tinyDB.getString(Constant.THEME).equals(Constant.DARK_MODE)) {
            materialRadioButton_dark.setChecked(true);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else if (!tinyDB.getString(Constant.THEME).equals(Constant.LIGHT_MODE)) {
            materialRadioButton_light.setChecked(true);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        else if (tinyDB.getString(Constant.THEME).equals(Constant.SYSTEM_MODE)) {
            materialRadioButton_system.setChecked(true);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }

            radioGroup_theme.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.radioBtn_dark_mode:
                    tinyDB.putString(Constant.THEME, Constant.DARK_MODE);
                    startActivity(new Intent(this,SettingActivity.class));
                    finish();
                    break;
                case R.id.radioBtn_light_mode:
                    tinyDB.putString(Constant.THEME, Constant.LIGHT_MODE);
                    startActivity(new Intent(this,SettingActivity.class));
                    finish();
                    break;
                case R.id.radioBtn_system_mode:
                    tinyDB.putString(Constant.THEME, Constant.SYSTEM_MODE);
                    startActivity(new Intent(this,SettingActivity.class));
                    finish();
                    break;
            }
        });
    }

    private void defineId() {
        textView_sample = findViewById(R.id.txtView_sample);
        appCompatSeekBar = findViewById(R.id.seekBar);
        textView_count = findViewById(R.id.txt_count);
        radioGroup_theme = findViewById(R.id.radio_theme);
        materialRadioButton_dark = findViewById(R.id.radioBtn_dark_mode);
        materialRadioButton_system = findViewById(R.id.radioBtn_system_mode);
        materialRadioButton_light = findViewById(R.id.radioBtn_light_mode);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SettingActivity.this, NoteViewActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(0, R.anim.play_panel_close_background);
        finish();
    }
}
