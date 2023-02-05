package com.dam2.m08.firebase;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.TokenWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.HashMap;

public class Login extends AppCompatActivity {

    private ImageView btn_back;
    private EditText usuario;
    private EditText password;
    private Button btn_login;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG ="FIREBASE_ANDROID_STUDIO___LOGIN";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        btn_login = findViewById(R.id.btnLogin);
        usuario = findViewById(R.id.edtxt_Usuario_Login);
        password = findViewById(R.id.edtxt_Contraseña_Login);
        btn_back = findViewById(R.id.btn_back_login);


        setup();

    }


    private void setup(){

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(Login.this, MainActivity.class );
                startActivity(intent);
            }
        });

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
                                        Log.d(TAG, "onComplete: setup()" );
                                        //token
                                        getTokenAndPush();
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
    }
    //obtiene el token y lo sube a la base de datos junto con el email del usuario
    private void getTokenAndPush(){
        Log.d(TAG, "getTokenAndPush: ");
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
                        DocumentReference documentReference= db.collection("usuarios").document(usuario.getText().toString());
                        HashMap map = new HashMap<>();
                        map.put("token", token);
                        documentReference.set(map);
                        Log.d(TAG, "token :añadido ");

                    }
                });
    }

    public void showHome(){
        Intent intent = new Intent(this, Home.class);
        intent.putExtra("email",usuario.getText().toString());
        startActivity(intent);
        Log.d(TAG, "showHome: ");
    }
    private void showAlertError(String mensaje){
        AlertDialog.Builder alert= new AlertDialog.Builder(this);
        alert.setTitle("Error");
        alert.setMessage(mensaje);
        alert.setCancelable(false);
        alert.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Login.this, Login.class);
                startActivity(intent);
            }
        });
        alert.create().show();
    }
}
