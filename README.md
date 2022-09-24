# Oneshot-ussd-banking

Unfortunately the API that Google added in Oreo only works for USSD services where you can dial the entire USSD code at the start and get back the response without entering anything into the session. What they apparently don't realize is that most telcos prevent this for security reasons.

So how can we bypass this restriction?

1. First use accessibility controls of android to get the ussd response and act on it on behalf of the user
2. Second step will be overlay splash screen to hide background processes.

# Libraries used
[USSDApi library](https://github.com/romellfudi/VoIpUSSD)

## Permission Handling
* <b>Permission dependecy</b>
  Add dependecies in the androidmanifest.xml file as follows
```
    <uses-permission android:name="android.permission.CALL_PHONE" />
    <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
```
* <b> Services </b>
Add Services in the androidmanifest.xml file as follows
```
    <service
        android:name="com.romellfudi.ussdlibrary.USSDService"
        android:permission="android.permission.BIND_ACCESSIBILITY_SERVICE">
        <intent-filter>
            <action android:name="android.accessibilityservice.AccessibilityService" />
        </intent-filter>
        <meta-data
            android:name="android.accessibilityservice"
            android:resource="@xml/ussd_service" />
    </service>
```


## How to Use library

* <b>Create hashmap</b>
```
map = new HashMap<>();
map.put("KEY_LOGIN",new HashSet<>(Arrays.asList("espere", "waiting", "loading", "esperando")));
map.put("KEY_ERROR",new HashSet<>(Arrays.asList("problema", "problem", "error", "null")));
```

Create instance of USSDApi and use it
```
USSDApi ussdApi = USSDController.getInstance(context);
ussdApi.callUSSDInvoke(phoneNumber, map, new USSDController.CallbackInvoke() {
    @Override
    public void responseInvoke(String message) {
        // message has the response string data
        String dataToSend = "data"// <- send "data" into USSD's input text
        ussdApi.send(dataToSend,new USSDController.CallbackMessage(){
            @Override
            public void responseMessage(String message) {
                // message has the response string data from USSD
            }
        });
    }

    @Override
    public void over(String message) {
        // message has the response string data from USSD or error
        // response no have input text, NOT SEND ANY DATA
    }
});
```
