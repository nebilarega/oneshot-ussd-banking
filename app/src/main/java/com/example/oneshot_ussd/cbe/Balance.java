package com.example.oneshot_ussd.cbe;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import com.example.oneshot_ussd.ForeGroundWindow;
import com.example.oneshot_ussd.ForegroundService;
import com.romellfudi.ussdlibrary.USSDApi;
import com.romellfudi.ussdlibrary.USSDController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;


class Accessible {
    // Private field
    private boolean isAccessible;

    // Getter method for isAccessible
    public boolean isAccessible() {
        return isAccessible;
    }

    // Setter method for isAccessible
    public void setAccessible(boolean isAccessible) {
        this.isAccessible = isAccessible;
    }
}

public class Balance {
    Context context;
    ForeGroundWindow window;
    String resMessage = "";
    Boolean isAccessibilityGiven = false;

    Accessible accessible = new Accessible();

    public Balance(Context context) {
        this.context = context;
        this.window = ForegroundService.getWindow();
    }

    public void setResMessage(String resMessage) {
        this.resMessage = resMessage;
    }

    public String getResMessage() {
        return resMessage;
    }

    public boolean getAccessibilityGiven() {
        return accessible.isAccessible();
    }

    public void setAccessibilityGiven(boolean isAccessibilityGiven) {
        accessible.setAccessible(isAccessibilityGiven);
    }

    public boolean verifyAccessibility() {
        return USSDController.verifyAccesibilityAccess(context);
    }

    public String getBalance() {
        HashMap<String, HashSet<String>> map = new HashMap<>();
        map.put("KEY_LOGIN", new HashSet<>(Arrays.asList("espere", "waiting", "loading", "esperando")));
        map.put("KEY_ERROR", new HashSet<>(Arrays.asList("problema", "problem", "error", "null")));
        USSDApi ussdApi = USSDController.getInstance(context);
        startForeGroundService(context);
        // check if window is running if not open window
        if (window != null) {
            Log.d("USSDRequest", "Window Is not Null");
            window.open();
        } else {
            Log.d("USSDRequest", "Top Window Is Null");
        }
        ussdApi.callUSSDInvoke("*999#", map, new USSDController.CallbackInvoke() {
            @Override
            public void responseInvoke(String message) {
                // message has the response string data
                setResMessage(message);
                String dataToSend = "1"; // <- send "data" into USSD's input text
                Log.d("message1", message);
                ussdApi.send(dataToSend, new USSDController.CallbackMessage() {
                    @Override
                    public void responseMessage(String message) {
                        // message has the response string data from USSD
                        setResMessage(message);
                        Log.d("message2", message);
                        ussdApi.send("1", new USSDController.CallbackMessage() {
                            @Override
                            public void responseMessage(String message) {
                                setResMessage(message);
                                Log.d("message3", message);
                                ussdApi.send("1", new USSDController.CallbackMessage() {
                                    @Override
                                    public void responseMessage(String message) {
                                        setResMessage(message);
                                        Log.d("message4", message);
                                        ussdApi.send("1", new USSDController.CallbackMessage() {
                                            @Override
                                            public void responseMessage(String message) {
                                                Log.d("message5", message);
                                                setResMessage(message);
                                                ussdApi.send("1", new USSDController.CallbackMessage() {
                                                    @Override
                                                    public void responseMessage(String message) {
                                                    }
                                                });
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                });
            }

            @Override
            public void over(String message) {
                setResMessage(message);

                if (window != null) {
                    window.close();
                }
                ussdApi.cancel();
                openTelegram();
            }
        });
        return resMessage;
    }

    public void startForeGroundService(Context context) {
        if (Settings.canDrawOverlays(context)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(new Intent(context, ForegroundService.class));
            } else {
                context.startService(new Intent(context, ForegroundService.class));
            }
        }
    }

    private void openTelegram() {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage("org.telegram.messenger");
        if (intent != null) {
            Log.d("telegramView", "Telegram app is installed");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else {
            // Telegram app is not installed, open in Play Store
            Log.d("telegramView", "Telegram is not installed");
            Intent playStoreIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=org.telegram.messenger"));
            context.startActivity(playStoreIntent);
        }
    }
}
