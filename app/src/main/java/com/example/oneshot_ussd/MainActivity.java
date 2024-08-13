package com.example.oneshot_ussd;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.oneshot_ussd.cbe.Balance;

public class MainActivity extends AppCompatActivity {
    CardView balanceCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        balanceCard = findViewById(R.id.balance);
        AccessibilityUtils accessibilityUtils = new AccessibilityUtils(this);
        boolean isAccessibilityServiceAvailable = accessibilityUtils.checkAccessibilityService();
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
            String message = "";
            TextView textView = findViewById(R.id.bankInfo);
            if (isAccessible) {
                startForeGroundService();
                newBalance.getBalance();
                message = newBalance.getResMessage();
//                stopForegroundService();
                Log.d("Output", message);
            }
            if (message != null && !message.equals("")){
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

    public void stopForegroundService() {
        if (Settings.canDrawOverlays(this)) {
            stopService(new Intent(this, ForegroundService.class));
        }
    }

    private Runnable setBalanceToView = new Runnable() {
        @Override
        public void run() {

        }
    };
}