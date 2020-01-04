/*
 * Copyright (C) 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.smsmessaging;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;



public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;
    private static final int REQUEST_READ_PHONE_STATE = 1;
    private static String phoneNumber;
    /**
     * Creates the activity, sets the view, and checks for SMS permission.
     *
     * @param savedInstanceState Instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Check to see if SMS is enabled.
        checkForSmsPermission();
    }


    private void checkForSmsPermission() {
        int smspermissionCheck = ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        int phonepermissionCheck = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);

        if ( smspermissionCheck!= PackageManager.PERMISSION_GRANTED)
        {
            Log.d(TAG, getString(R.string.permission_not_granted));

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    MY_PERMISSIONS_REQUEST_SEND_SMS);


        }

        else {
            // Permission already granted. Enable the SMS button.
            enableSmsButton();


        }

        if (phonepermissionCheck!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    REQUEST_READ_PHONE_STATE);
        }else{

            //get the phone details
            TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            String simID = tm.getSimSerialNumber();;
            String phoneNumber = tm.getLine1Number();
            Toast.makeText(this, "your phone number: "+simID, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Processes permission request codes.
     *
     * @param requestCode  The request code passed in requestPermissions()
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        // For the requestCode, check if permission was granted or not.
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission was granted. Enable sms button.
                    enableSmsButton();
                } else {
                    // Permission denied.
                    Log.d(TAG, getString(R.string.failure_permission));
                    Toast.makeText(this, getString(R.string.failure_permission),
                            Toast.LENGTH_LONG).show();
                    // Disable the sms button.
                    disableSmsButton();
                }
            }

        }
    }

    /**
     * Defines a string (destinationAddress) for the phone number
     * and gets the input text for the SMS message.
     * Uses SmsManager.sendTextMessage to send the message.
     * Before sending, checks to see if permission is granted.
     *
     * @param view View (message_icon) that was clicked.
     *
     *
     *
     */



    public void smsSendMessage(View view) {
        try {



            Toast.makeText(this, "\"your simID: \"+simID+\"\\nyour phone Number: \"+phoneNumber", Toast.LENGTH_SHORT).show();
            EditText editText = (EditText) findViewById(R.id.editText_main);

            // Set the destination phone number to the string in editText.
            String destinationAddress = editText.getText().toString();

            // Find the sms_message view.
            EditText smsEditText = (EditText) findViewById(R.id.sms_message);

            // Get the text of the sms message.
            String smsMessage = smsEditText.getText().toString();

            // Set the service center address if needed, otherwise null.
            String scAddress = "+251911299708";
            // Set pending intents to broadcast
            // when message sent and when delivered, or set to null.
            PendingIntent sentIntent = null, deliveryIntent = null;
            // Check for permission first.
            checkForSmsPermission();
            // Use SmsManager.
            SmsManager smsManager = SmsManager.getDefault();

            smsManager.sendTextMessage(destinationAddress, scAddress, smsMessage,
                    null, null);
        } catch (Exception e) {
            Toast.makeText(this, "Zed Error: " + e, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Makes the sms button (message icon) invisible so that it can't be used,
     * and makes the Retry button visible.
     */
    private void disableSmsButton() {
        Toast.makeText(this, R.string.sms_disabled, Toast.LENGTH_LONG).show();
        ImageButton smsButton = (ImageButton) findViewById(R.id.message_icon);
        smsButton.setVisibility(View.INVISIBLE);
        Button retryButton = (Button) findViewById(R.id.button_retry);
        retryButton.setVisibility(View.VISIBLE);
    }

    /**
     * Makes the sms button (message icon) visible so that it can be used.
     */
    private void enableSmsButton() {
        ImageButton smsButton = (ImageButton) findViewById(R.id.message_icon);
        smsButton.setVisibility(View.VISIBLE);
    }

    /**
     * Sends an intent to start the activity.
     *
     * @param view View (Retry button) that was clicked.
     */
    public void retryApp(View view) {
        Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
        startActivity(intent);
    }
}
