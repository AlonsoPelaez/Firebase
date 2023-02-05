package com.dam2.m08.firebase;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.Collections;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity{

    private Button btn_inicioSesion;
    private Button btn_registro;
    private View layoutPrincipal;
    private Boolean muestra_btn_registro=true;
    private static final String TAG ="FIREBASE_ANDROID_MAIN";
    private boolean ejecutado=false;
//    private static final String PREF_NAME = "MyPref";
//    private static final String SETTING_EXECUTED = "settingExecuted";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        layoutPrincipal = findViewById(R.id.layoutPrincipal);
        btn_inicioSesion = findViewById(R.id.iniciaSesion);
        btn_registro = findViewById(R.id.registro);



        compruebaEstadoDelBoton();
//        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
//        boolean settingExecuted = prefs.getBoolean(SETTING_EXECUTED, false);
//        if (!settingExecuted) {
//          setConfigSetting();
//        }
        setConfigSetting();
        setup();
        session();


    }
    private void setup(){

        btn_inicioSesion.setOnClickListener(view -> {
            Intent intent= new Intent(MainActivity.this, Login.class);
            startActivity(intent);
        });

        btn_registro.setOnClickListener(view -> {
            Intent intent= new Intent(MainActivity.this, Register.class);
            startActivity(intent);
        });

    }
    
    private void setConfigSetting() {
        Log.d(TAG, "setConfigSetting: dentro");

        //60 segundos de intervalo para recargar los datos
        FirebaseRemoteConfig firebaseConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(10)
                .build();
        firebaseConfig.setConfigSettingsAsync(configSettings);
//        firebaseConfig.setDefaultsAsync(Collections.singletonMap("muestra_btn_registro",true));
//        firebaseConfig.fetchAndActivate();

//
//        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
//        SharedPreferences.Editor editor = prefs.edit();
//        editor.putBoolean(SETTING_EXECUTED, true);
//        editor.apply();

    }

    private void compruebaEstadoDelBoton(){
        Log.d(TAG, "compruebaEstadoDelBoton: dentro");

        FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        firebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(new OnCompleteListener<Boolean>() {
            @Override
            public void onComplete(@NonNull Task<Boolean> task) {
                if (!task.isSuccessful()){
                    Log.d(TAG, "ERROR: "+task.getException().getMessage());
                }
                muestra_btn_registro = firebaseRemoteConfig.getBoolean("muestra_btn_registro");
                Log.d(TAG, "muestra_registro: "+ muestra_btn_registro);

                if (!muestra_btn_registro){
                    btn_registro.setVisibility(View.GONE);
                }else {
                    btn_registro.setVisibility(View.VISIBLE);
                }
            }
        });



    }


    private void session(){
        Log.d(TAG, "session: dentro");
        SharedPreferences prefer= getSharedPreferences(getString(R.string.prefer_file), Context.MODE_PRIVATE);
        String email = prefer.getString("email",null);
        if (email != null){
            layoutPrincipal.setVisibility(View.INVISIBLE);
            Intent intent = new Intent(this, Home.class);
            intent.putExtra("email",email);
            startActivity(intent);
        }
        else {
            layoutPrincipal.setVisibility(View.VISIBLE);
        }
    }


}