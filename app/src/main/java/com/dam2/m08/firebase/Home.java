package com.dam2.m08.firebase;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.app.NotificationManagerCompat;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

public class Home extends AppCompatActivity  {

    private Button btn_save;
    private EditText titulo;
    private EditText contenido;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);


        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        if (manager.areNotificationsEnabled()) {
            FirebaseMessaging.getInstance().setNotificationDelegationEnabled(true);
        }


        setup();
        preferences();
        cargaDoc();
//        guardaDoc();


    }

    private void setup(){
        setTitle("Home");

        btn_save = findViewById(R.id.btn_save);
        titulo = findViewById(R.id.titulo_doc);
        contenido = findViewById(R.id.contenido_doc);

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardaDoc();
            }
        });
    }

    private void preferences(){
        Intent intent = getIntent();
        String usuario= intent.getStringExtra("email");

        SharedPreferences prefer= getSharedPreferences(getString(R.string.prefer_file), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefer.edit();
        editor.putString("email",usuario);
        editor.apply();
    }

    private void cargaDoc() {

        db.collection("documentos").document("kNniQPs2NdaKRd1LRXYX").addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documento, @Nullable FirebaseFirestoreException error) {

                    if (documento != null && documento.exists()){
                        titulo.setText(documento.getString("titulo"));
                        contenido.setText(documento.getString("contenido"));
                    }
                    else {
                        showAlertError(error.getMessage());
                    }
            }
        });

    }

    private void guardaDoc() {

                HashMap map = new HashMap();
                map.put("titulo",titulo.getText().toString());
                map.put("contenido", contenido.getText().toString());



                db.collection("documentos").document("kNniQPs2NdaKRd1LRXYX").set(map);


                CollectionReference usuarios = db.collection("usuarios");

                usuarios.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){

                            SharedPreferences preferences = getSharedPreferences(getString(R.string.prefer_file), Context.MODE_PRIVATE);
                            String usuario = preferences.getString("email","");

                            if (!documentSnapshot.getId().equals(usuario)) {

                                String token = documentSnapshot.getString("token");
                                MyFirebaseMessagingService myFirebaseMessagingService = new MyFirebaseMessagingService();
                                JsonObjectRequest request=myFirebaseMessagingService.sendMessage(token, usuario);
                                RequestQueue queue= Volley.newRequestQueue(Home.this);
                                queue.add(request);

                            }

                        }
                    }
                });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        int id= item.getItemId();
        if (id==R.id.btn_logout){
            SharedPreferences preferences = getSharedPreferences(getString(R.string.prefer_file),Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.apply();

            FirebaseAuth.getInstance().signOut();
            
            Intent intent = new Intent(this,Login.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);

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
}
