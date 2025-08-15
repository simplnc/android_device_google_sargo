package org.lineageos.trustextras;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.UserManager;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

public class TrustExtrasActivity extends Activity {
    private static final String RESTRICTION_INSTALL_UNKNOWN_SOURCES = UserManager.DISALLOW_INSTALL_UNKNOWN_SOURCES;
    private static final String RESTRICTION_INSTALL_UNKNOWN_SOURCES_GLOBALLY = UserManager.DISALLOW_INSTALL_UNKNOWN_SOURCES_GLOBALLY;

    private static final String PREFS_NAME = "googleparts_prefs"; // share prefs with GoogleParts
    private static final String PREF_PER_USER = "block_unknown_per_user";
    private static final String PREF_GLOBAL = "block_unknown_global";

    private UserManager userManager;
    private Switch blockPerUserSwitch;
    private Switch blockGlobalSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userManager = (UserManager) getSystemService(Context.USER_SERVICE);

        TextView title = new TextView(this);
        title.setText(Strings.APP_NAME);
        title.setPadding(48, 48, 48, 24);
        blockPerUserSwitch = new Switch(this);
        blockPerUserSwitch.setText(Strings.BLOCK_PER_USER);
        blockGlobalSwitch = new Switch(this);
        blockGlobalSwitch.setText(Strings.BLOCK_GLOBAL);
        Button apply = new Button(this);
        apply.setText(Strings.APPLY);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean hasSaved = prefs.contains(PREF_PER_USER) && prefs.contains(PREF_GLOBAL);
        boolean perUser = hasSaved ? prefs.getBoolean(PREF_PER_USER, true)
                                   : userManager.hasUserRestriction(RESTRICTION_INSTALL_UNKNOWN_SOURCES);
        boolean global = hasSaved ? prefs.getBoolean(PREF_GLOBAL, true)
                                  : userManager.hasUserRestriction(RESTRICTION_INSTALL_UNKNOWN_SOURCES_GLOBALLY);
        blockPerUserSwitch.setChecked(perUser);
        blockGlobalSwitch.setChecked(global);

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean perUserChecked = blockPerUserSwitch.isChecked();
                boolean globalChecked = blockGlobalSwitch.isChecked();
                SharedPreferences.Editor e = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
                e.putBoolean(PREF_PER_USER, perUserChecked);
                e.putBoolean(PREF_GLOBAL, globalChecked);
                e.apply();
                // Apply immediately
                userManager.setUserRestriction(RESTRICTION_INSTALL_UNKNOWN_SOURCES, perUserChecked);
                userManager.setUserRestriction(RESTRICTION_INSTALL_UNKNOWN_SOURCES_GLOBALLY, globalChecked);
                finish();
            }
        });

        android.widget.LinearLayout root = new android.widget.LinearLayout(this);
        root.setOrientation(android.widget.LinearLayout.VERTICAL);
        root.addView(title);
        root.addView(blockPerUserSwitch);
        root.addView(blockGlobalSwitch);
        root.addView(apply);
        setContentView(root);
    }
}