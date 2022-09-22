package com.example.myapplication.cbe;

import android.content.Context;
import android.util.Log;

import com.example.myapplication.ForeGroundWindow;
import com.romellfudi.ussdlibrary.USSDApi;
import com.romellfudi.ussdlibrary.USSDController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class Balance {
    Context context;
    ForeGroundWindow window;
    String resMessage = "";
    public Balance(Context context, ForeGroundWindow window) {
        this.context = context;
        this.window = window;
    }

    public String getBalance() {
        HashMap<String, HashSet<String>> map = new HashMap<>();
        map.put("KEY_LOGIN", new HashSet<>(Arrays.asList("espere", "waiting", "loading", "esperando")));
        map.put("KEY_ERROR", new HashSet<>(Arrays.asList("problema", "problem", "error", "null")));
        USSDApi ussdApi = USSDController.getInstance(context);
        ussdApi.callUSSDInvoke("*889#", map, new USSDController.CallbackInvoke() {
            @Override
            public void responseInvoke(String message) {
                // message has the response string data
                resMessage = message;
                String dataToSend = "1"; // <- send "data" into USSD's input text
                ussdApi.send(dataToSend, new USSDController.CallbackMessage() {
                    @Override
                    public void responseMessage(String message) {
                        // message has the response string data from USSD
                        Log.println(Log.DEBUG, "ussd-result", message);
                        resMessage = message;
                        ussdApi.send("2288", new USSDController.CallbackMessage() {
                            @Override
                            public void responseMessage(String message) {
                                Log.println(Log.DEBUG, "ussd-result", message);
                                resMessage = message;
                                ussdApi.send("1", new USSDController.CallbackMessage() {
                                    @Override
                                    public void responseMessage(String message) {
                                        Log.println(Log.DEBUG, "ussd-result", message);
                                        resMessage = message;
                                        ussdApi.send("1", new USSDController.CallbackMessage() {
                                            @Override
                                            public void responseMessage(String message) {
                                                Log.println(Log.DEBUG, "ussd-result", message);
                                                resMessage = message;
                                                ussdApi.cancel();
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
                resMessage = message;
                if (window != null)
                    window.close();
                Log.println(Integer.valueOf(1), "USSD Response", "message");
            }
        });
        return resMessage;
    }
}
