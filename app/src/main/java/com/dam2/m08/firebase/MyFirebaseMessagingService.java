package com.dam2.m08.firebase;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.nio.file.ClosedDirectoryStreamException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class MyFirebaseMessagingService extends FirebaseMessagingService{

    private static final String TAG = "FIREBASE_ANDROID__MYFIREBASE";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        Log.d(TAG, "onMessageReceived: "+message.getFrom());

        if (message.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + message.getNotification().getBody());
        }
    }

    public void generaToken(String usuario){
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {

                            Log.d(TAG, "Fetching FCM registration token failed " + task.getException());
                            return;
                        }
                        String token = task.getResult();

                        //codigo para llamar a la base de datos si el usuario esta registrado actualiza token
                        DocumentReference documentReference= db.collection("usuarios").document(usuario);
                        HashMap map = new HashMap<>();
                        map.put("token", token);
                        documentReference.set(map);
                        Log.d(TAG, "token :añadido ");

                    }
                });
    }

    public void sendMessage(String token, String usuario){

        Date fecha = new Date();
        long tiempo = fecha.getTime();

        SimpleDateFormat formateado = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String tiempoFormateado = formateado.format(tiempo);

        RemoteMessage message = new RemoteMessage.Builder(token)
                .addData("title","Documento modificado")
                .addData("body", usuario + " ha modificado el documento el "+ tiempoFormateado)
                .build();


        FirebaseMessaging.getInstance().send(message);
    }
}
