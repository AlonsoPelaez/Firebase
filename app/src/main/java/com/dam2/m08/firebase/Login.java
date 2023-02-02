package com.dam2.m08.firebase;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.google.firebase.messaging.FirebaseMessaging;

public class Login extends AppCompatActivity {

    private ImageView btn_back;
    private EditText usuario;
    private EditText password;
    private Button btn_login;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        btn_back = findViewById(R.id.btn_back_login);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(Login.this, MainActivity.class );
                startActivity(intent);
            }
        });
        setup();

    }


    private void setup(){
        btn_login = findViewById(R.id.btnLogin);
        usuario = findViewById(R.id.edtxt_Usuario_Login);
        password = findViewById(R.id.edtxt_Contraseña_Login);


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
                                        showHome();
                                    }
                                    else {
                                        showAlertError();
                                    }
                                }
                            });
                }
            }
        });
    }
    //obtener el token
    private void notification(){

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            System.out.println("Fetching FCM registration token failed"+ task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();


                        Toast.makeText(Login.this, token, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void showHome(){
        Intent intent = new Intent(this, Home.class);
        intent.putExtra("email",usuario.getText().toString());
        startActivity(intent);
    }
    private void showAlertError(){
        AlertDialog.Builder alert= new AlertDialog.Builder(this);
        alert.setTitle("Error");
        alert.setMessage("Ha ocurrido un error ");
        alert.setCancelable(false);
        alert.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Login.this, MainActivity.class);
                startActivity(intent);
            }
        });
        alert.create().show();
    }
}
