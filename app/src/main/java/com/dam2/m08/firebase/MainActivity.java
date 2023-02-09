package com.dam2.m08.firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
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
//private final ActivityResultLauncher<String> requestPermissionLauncher =
//        registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
//            if (isGranted) {
//                // FCM SDK (and your app) can post notifications.
//            } else {
//                // TODO: Inform user that that your app will not show notifications.
//            }
//        });

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



//    private void askNotificationPermission() {
//        // This is only necessary for API level >= 33 (TIRAMISU)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
//                    PackageManager.PERMISSION_GRANTED) {
//                // FCM SDK (and your app) can post notifications.
//            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
//                // TODO: display an educational UI explaining to the user the features that will be enabled
//                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
//                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
//                //       If the user selects "No thanks," allow the user to continue without notifications.
//            } else {
//                // Directly ask for the permission
//                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
//            }
//        }
//    }



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