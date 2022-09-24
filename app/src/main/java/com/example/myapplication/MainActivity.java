package com.example.myapplication;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.myapplication.cbe.Balance;

public class MainActivity extends AppCompatActivity {
    CardView balanceCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        balanceCard = findViewById(R.id.balance);
        PermissionHandler permissionHandler = new PermissionHandler(MainActivity.this);
        permissionHandler.handlePermissions();
        if (permissionHandler.hasOverlayPermission()) {
            Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            startActivity(myIntent);
        }
        balanceCard.setOnClickListener(view -> {
            Balance newBalance = new Balance(this, ForegroundService.getWindow());
            newBalance.verifyAccessibility();
            boolean isAccessible = newBalance.getAccessibilityGiven();
            if (isAccessible) {
                startForeGroundService();
                String message = newBalance.getBalance();
                TextView textView = findViewById(R.id.bankInfo);
                textView.setText(message);
            }
        });
    }

    public void startForeGroundService() {
        if (Settings.canDrawOverlays(this)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(new Intent(this, ForegroundService.class));
            } else {
                startService(new Intent(this, ForegroundService.class));
            }
        }
    }
}