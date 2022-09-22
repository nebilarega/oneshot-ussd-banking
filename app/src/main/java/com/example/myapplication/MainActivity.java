package com.example.myapplication;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import com.example.myapplication.cbe.Balance;
import com.romellfudi.ussdlibrary.USSDApi;
import com.romellfudi.ussdlibrary.USSDController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

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

        PERMISSIONS = new String[]{
                Manifest.permission.CALL_PHONE,
                Manifest.permission.READ_PHONE_STATE,
//                Manifest.permission.SYSTEM_ALERT_WINDOW,
        };

        if (!hasPermissions(MainActivity.this, PERMISSIONS)) {
//            for (String PERMISSION : PERMISSIONS) {
//                requestPhonePermission(PERMISSION);
//            }
            ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, 1);
        }
        checkOverlayPermission();
        startService();
        balanceCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newBalance.getBalance();
            }
        });
    }

    private boolean hasPermissions(Context context, String... PERMISSIONS) {
        if (context != null && PERMISSIONS != null) {

            for (String permission : PERMISSIONS) {

                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public void implementSubmit() {
        HashMap<String, HashSet<String>> map = new HashMap<>();
        map.put("KEY_LOGIN", new HashSet<>(Arrays.asList("espere", "waiting", "loading", "esperando")));
        map.put("KEY_ERROR", new HashSet<>(Arrays.asList("problema", "problem", "error", "null")));
        USSDApi ussdApi = USSDController.getInstance(this);
        ussdApi.callUSSDInvoke("*999#", map, new USSDController.CallbackInvoke() {
            @Override
            public void responseInvoke(String message) {
                // message has the response string data
                Log.println(Log.DEBUG, "ussd-result", message);
                String dataToSend = "1"; // <- send "data" into USSD's input text
                ussdApi.send(dataToSend, new USSDController.CallbackMessage() {
                    @Override
                    public void responseMessage(String message) {
                        // message has the response string data from USSD
                        Log.println(Log.DEBUG, "ussd-result", message);
                        ussdApi.send("1", new USSDController.CallbackMessage() {
                            @Override
                            public void responseMessage(String message) {
                                ussdApi.send("1", new USSDController.CallbackMessage() {
                                    @Override
                                    public void responseMessage(String message) {
                                        Log.println(Log.DEBUG, "ussd-result", message);
                                    }
                                });
                            }
                        });
                    }
                });
            }

            @Override
            public void over(String message) {
                Log.println(Integer.valueOf(1), "USSD Response", "message");
            }
        });
    }

    public void requestPhonePermission(String PERMISSION) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                PERMISSION)) {

            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed because of this and that")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{PERMISSION}, PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{PERMISSION}, PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Calling Permission is granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Calling Permission is denied", Toast.LENGTH_SHORT).show();
                balanceCard.setEnabled(false);
            }

            if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Phone State Permission is Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Phone State Permission is denied", Toast.LENGTH_SHORT).show();
                balanceCard.setEnabled(false);
            }

//            if (grantResults[2] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this, "System Alert com.example.myapplication.ForegroundService.Window Permission Granted", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(this, "System Alert com.example.myapplication.ForegroundService.Window Permission denied", Toast.LENGTH_SHORT).show();
//                submitButton.setEnabled(false);
//            }
        }
    }
    public void startService(){
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