package com.dam2.m08.firebase;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingRegistrar;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.HashMap;

public class MyFirebaseMessagingService extends FirebaseMessagingService{


    private static final String TAG = "FIREBASE_ANDROID__MYFIREBASE";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());


        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        sendNotification(remoteMessage.getNotification().getBody());
    }
    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, Home.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_IMMUTABLE);

        String channelId = "My_channel_id";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_stat_notification)
                        .setContentTitle("Documento Modificado")
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }



    public void generaToken(String usuario){
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {

                        Log.d(TAG, "Fetching FCM registration token failed " + task.getException());
                        return;
                    }
                    String token = task.getResult();

                    //codigo para llamar a la base de datos si el usuario esta registrado actualiza token
                    DocumentReference documentReference= db.collection("usuarios").document(usuario);
                    HashMap<String, String> map = new HashMap<>();
                    map.put("token", token);
                    documentReference.set(map);
                    Log.d(TAG, "token :añadido ");

                });
    }
    public void sendMessage(String token, String usuario){

        Log.d(TAG, "token: "+ token);

        FirebaseMessaging messaging = FirebaseMessaging.getInstance();
        RemoteMessage message= new RemoteMessage.Builder(token)
                .addData("titulo","Documento Modificado")
                .addData("contenido", usuario + "ha modificado el documento")
                .build();

        messaging.send(message);
        Log.d(TAG, "FirebaseMessaging.getInstance().isNotificationDelegationEnabled(): "+ FirebaseMessaging.getInstance().isNotificationDelegationEnabled());
        Log.d(TAG, " FirebaseMessaging.getInstance().isAutoInitEnabled(): "+ FirebaseMessaging.getInstance().isAutoInitEnabled());

    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
    }
}
