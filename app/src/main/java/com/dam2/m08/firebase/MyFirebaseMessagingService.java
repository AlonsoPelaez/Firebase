package com.dam2.m08.firebase;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

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
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
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
    private void showAlertError(String mensaje){
        AlertDialog.Builder alert= new AlertDialog.Builder(this);
        alert.setTitle("Error");
        alert.setMessage(mensaje);
        alert.setCancelable(false);
        alert.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        alert.create().show();
    }


    public void generaToken(String usuario){
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {

                        showAlertError(task.getException().getMessage());
                        return;
                    }
                    String token = task.getResult();

                    //codigo para llamar a la base de datos si el usuario esta registrado actualiza token
                    DocumentReference documentReference= db.collection("usuarios").document(usuario);
                    HashMap<String, String> map = new HashMap<>();
                    map.put("token", token);
                    documentReference.set(map);

                });
    }
    public JsonObjectRequest sendMessage(String token, String usuario){

        JSONObject notification = new JSONObject();
        try {

            notification.put("to",token);
            notification.put("notification", new JSONObject()
                    .put("title","Documento Modificado")
                    .put("body",usuario + " ha modificado el documento")
                    .put("icon",R.drawable.ic_stat_notification)
            );
        }catch (Exception e){
            e.printStackTrace();
        }

        String url="https://fcm.googleapis.com/fcm/send";
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                notification,
                response -> {
                    Log.d(TAG, "sendMessage: se ha enviado con exito");
                },
                error -> {
                    Log.d(TAG, "sendMessage: ha ocurrido un error");
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "key=AAAAN-mP0qE:APA91bF60PQG-U9t56PhPssaSRRikUYMv4kjyeZ8AHtt2FmZ1OM8REgCcHHxsUfgFpHVoU4T70e52JrMRCPr-lLKSu-W_KJHpPfNKuH9ZUYfBf-WP9-H9TYHssntITUBmAQBzczPHULr");
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        return request;
    }
}
