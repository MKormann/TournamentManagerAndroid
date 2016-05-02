package com.mattkormann.tournamentmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        LinearLayout welcomeLayout = (LinearLayout)findViewById(R.id.welcome_layout);
        welcomeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToMainActivity();
            }
        });

    }

    public void switchToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
