package net.muellersites.depicture;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


import java.util.Map;


public class DepictureFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "Dev";
    public static final String INTENT_FILTER = "INTENT_FILTER";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {

        // TODO(developer): Handle FCM messages here.
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            final Map<String, String> remoteData = remoteMessage.getData();
            final String status = remoteData.get("status");

            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (status.equals("start")) {
                        Intent intent = new Intent(INTENT_FILTER);
                        intent.putExtra("msg", String.valueOf(remoteData.get("msg")));
                        sendBroadcast(intent);
                    } else if (status.equals("stage 1")) {
                        Toast.makeText(getApplicationContext(),
                                remoteData.get("msg"),
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        // [END_EXCLUDE]
    }
    // [END receive_message]


}
