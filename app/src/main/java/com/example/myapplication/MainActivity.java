package com.example.myapplication;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.myapplication.cbe.Balance;

public class MainActivity extends AppCompatActivity {
    private int PERMISSION_CODE = 1;
    private String[] PERMISSIONS;
    CardView balanceCard;
    Balance newBalance = new Balance(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        balanceCard = findViewById(R.id.balance);
        PermissionHandler permissionHandler = new PermissionHandler(MainActivity.this);
        permissionHandler.handlePermissions();
        if (permissionHandler.hasOverlayPermission()){
            Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            startActivity(myIntent);
        }
        startForeGroundService();
        balanceCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = newBalance.getBalance();
                TextView textView = findViewById(R.id.bankInfo);
                textView.setText(message);
            }
        });
    }
    public void startForeGroundService(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // check if the user has already granted
            // the Draw over other apps permission
            if(Settings.canDrawOverlays(this)) {
                // start the service based on the android version
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(new Intent(this, ForegroundService.class));
                } else {
                    startService(new Intent(this, ForegroundService.class));
                }
            }
        }else{
            startService(new Intent(this, ForegroundService.class));
        }
    }

    // method to ask user to grant the Overlay permission
    public void checkOverlayPermission(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                // send user to the device settings
                Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                startActivity(myIntent);
            }
        }
    }
}