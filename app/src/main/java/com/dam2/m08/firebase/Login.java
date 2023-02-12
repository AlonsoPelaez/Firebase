package com.dam2.m08.firebase;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.Collections;
import java.util.HashMap;


public class Login extends AppCompatActivity {


    private EditText usuario;
    private EditText password;
    private Button btn_login;
    private Button btn_register;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        btn_login = findViewById(R.id.btnLogin);
        usuario = findViewById(R.id.edtxt_Usuario_Login);
        password = findViewById(R.id.edtxt_Contraseña_Login);
        btn_register = findViewById(R.id.sendToRegister);

        compruebaEstadoDelBoton();
        session();
        setConfigSetting();
        setup();

    }


    private void setup(){

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!usuario.getText().toString().isEmpty() && !password.getText().toString().isEmpty()){
                    FirebaseAuth.getInstance()
                            .signInWithEmailAndPassword(usuario.getText().toString(), password.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        //token
                                        MyFirebaseMessagingService myFirebaseMessagingService = new MyFirebaseMessagingService();
                                        myFirebaseMessagingService.generaToken(usuario.getText().toString());

                                        //envia al home de la app
                                        showHome();
                                    }
                                    else {
                                        //muestra error
                                        showAlertError(task.getException().getMessage());
                                    }
                                }
                            });
                }
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });
    }

    public void showHome(){
        Intent intent = new Intent(this, Home.class);
        intent.putExtra("email",usuario.getText().toString());
        startActivity(intent);
    }

    private void setConfigSetting() {

        //60 segundos de intervalo para recargar los datos
        FirebaseRemoteConfig firebaseConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(10)
                .build();
        firebaseConfig.setConfigSettingsAsync(configSettings);

        HashMap map = new HashMap();
        map.put("muestra_btn_registro",true);
        firebaseConfig.setDefaultsAsync(map);
        firebaseConfig.fetchAndActivate();

    }

    private void compruebaEstadoDelBoton(){

        FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        firebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(new OnCompleteListener<Boolean>() {
            @Override
            public void onComplete(@NonNull Task<Boolean> task) {
                if (!task.isSuccessful()){
                }
                else {
                    boolean muestra_btn_registro = firebaseRemoteConfig.getBoolean("muestra_btn_registro");

                    if (!muestra_btn_registro){
                        btn_register.setVisibility(View.GONE);
                    }else {
                        btn_register.setVisibility(View.VISIBLE);
                    }
                }
            }
        });



    }


    private void session(){
        SharedPreferences prefer= getSharedPreferences(getString(R.string.prefer_file), Context.MODE_PRIVATE);
        String email = prefer.getString("email",null);
        Log.d("firebase-android", "session: "+ email);
        if (email != null){
            Intent intent = new Intent(this, Home.class);
            intent.putExtra("email",email);
            Log.d("firebase-android", "dentro if: "+email);
            startActivity(intent);
        }
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
