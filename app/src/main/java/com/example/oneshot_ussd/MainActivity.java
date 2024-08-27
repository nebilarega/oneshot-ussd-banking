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
import com.romellfudi.ussdlibrary.USSDController;

public class MainActivity extends AppCompatActivity implements PermissionResultCallback {
    CardView balanceCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        balanceCard = findViewById(R.id.balance);
        PermissionHandler permissionHandler = new PermissionHandler(MainActivity.this);
        if (!permissionHandler.hasOverlayPermission()) {
            Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            startActivity(myIntent);
            permissionHandler.handlePermissions();
        }
        balanceCard.setOnClickListener(view -> {
            Balance newBalance = new Balance(this);
            boolean isAccessible  = newBalance.verifyAccessibility();
            boolean permissions = permissionHandler.hasAllPermissions();
            String message = "";
            Log.d("PermissionsHere", ""+isAccessible);
            if (permissions && isAccessible) {
                newBalance.getBalance();
                message = newBalance.getResMessage();
                Log.d("Output", message);
            }
        });
    }

    @Override
    public void onSuccessfulPermissionResult() {
        onSuccessfulPermissions();
    }

    public void onSuccessfulPermissions() {

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
}