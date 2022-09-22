package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class PermissionHandler extends AppCompatActivity {
    private String[] PERMISSIONS;
    private int PERMISSION_CODE = 1;
    Context context;
    public PermissionHandler(Context context){
        this.context = context;
    }
    public void handlePermissions(){
        PERMISSIONS = new String[]{
                Manifest.permission.CALL_PHONE,
                Manifest.permission.READ_PHONE_STATE,
        };
        if (!hasPermissions(PERMISSIONS)) {
            ActivityCompat.requestPermissions((Activity) context, PERMISSIONS, 1);
        }
    }
    public boolean hasOverlayPermission(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(context)) {
                return true;
            }
        }
        return false;
    }
    public boolean hasPermissions(String... PERMISSIONS){
        if (context != null && PERMISSIONS != null) {

            for (String permission : PERMISSIONS) {

                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Calling Permission is granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Calling Permission is denied", Toast.LENGTH_SHORT).show();
            }

            if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Phone State Permission is Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Phone State Permission is denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
