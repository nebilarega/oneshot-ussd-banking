package com.example.oneshot_ussd;

import android.content.Context;

import com.romellfudi.ussdlibrary.USSDController;

public class AccessibilityUtils {
    Context context;

    public AccessibilityUtils(Context context){
        this.context = context;
    }

    public boolean checkAccessibilityService(){
        return USSDController.verifyAccesibilityAccess(this.context);
    }
}
