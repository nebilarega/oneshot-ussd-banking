package com.example.oneshot_ussd.cbe;

import android.content.Context;
import android.util.Log;

import com.example.oneshot_ussd.ForeGroundWindow;
import com.romellfudi.ussdlibrary.USSDApi;
import com.romellfudi.ussdlibrary.USSDController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class Balance {
    Context context;
    ForeGroundWindow window;
    String resMessage = "";
    boolean isAccessibilityGiven = false;
    public Balance(Context context, ForeGroundWindow window) {
        this.context = context;
        this.window = window;
    }

    public void setResMessage(String resMessage) {
        this.resMessage = resMessage;
    }

    public String getResMessage() {
        return resMessage;
    }

    public boolean getAccessibilityGiven(){
        return isAccessibilityGiven;
    }
    public void setAccessibilityGiven(boolean isAccessibilityGiven){
        this.isAccessibilityGiven = isAccessibilityGiven;
    }
    public void verifyAccessibility(){
        setAccessibilityGiven(USSDController.verifyAccesibilityAccess(context));
    }
    public String getBalance() {
        HashMap<String, HashSet<String>> map = new HashMap<>();
        map.put("KEY_LOGIN", new HashSet<>(Arrays.asList("espere", "waiting", "loading", "esperando")));
        map.put("KEY_ERROR", new HashSet<>(Arrays.asList("problema", "problem", "error", "null")));
        USSDApi ussdApi = USSDController.getInstance(context);

        // check if window is running if not open window
        if (window != null){
            window.open();
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
                                                ussdApi.send("1", new USSDController.CallbackMessage(){
                                                    @Override
                                                    public void responseMessage(String message) {
                                                        if (window != null){
                                                            window.close();
                                                        }
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
                Log.d("USSDRequest", getResMessage());
                if (window != null)
                    window.close();
                ussdApi.cancel();
            }
        });
        return resMessage;
    }
}
