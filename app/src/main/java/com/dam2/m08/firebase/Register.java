package com.dam2.m08.firebase;

import android.content.DialogInterface;
import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.HashMap;


public class Register extends AppCompatActivity {

    private ImageView btn_back;
    private Button btn_register;
    private EditText usuario;
    private EditText password;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        btn_back = findViewById(R.id.btn_back_register);
        btn_register = findViewById(R.id.btn_Register);
        usuario = findViewById(R.id.edtxt_Usuario_Register);
        password = findViewById(R.id.edtxt_Contraseña_Register);


        setup();
    }
    private void setup(){
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(Register.this, Login.class );
                startActivity(intent);
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!usuario.getText().toString().isEmpty() && !password.getText().toString().isEmpty()){
                    FirebaseAuth.getInstance()
                            .createUserWithEmailAndPassword(usuario.getText().toString(), password.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                            //crea el usuario y lo sube a la base de datos firestore
                                            DocumentReference documentReference = db.collection("usuarios").document(usuario.getText().toString());
                                            HashMap map = new HashMap();
                                            map.put("token","");
                                            documentReference.set(map);
                                            Intent intent = new Intent(Register.this, Login.class);
                                            startActivity(intent);
                                            compruebaUsuariosRegistrados();
                                    }
                                    else {
                                        showAlertError(task.getException().getMessage());
                                    }
                                }
                            });
                }
            }
        });
    }

    private void compruebaUsuariosRegistrados(){



        CollectionReference usuarios = db.collection("usuarios");
        Task<QuerySnapshot> task =usuarios.get();
        task.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int cantidadUsuarios = queryDocumentSnapshots.size();


                FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
                firebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        if (task.isSuccessful()){
                            if (cantidadUsuarios==2) {
                                HashMap map = new HashMap();
                                map.put("muestra_btn_registro", false);
                                firebaseRemoteConfig.setDefaultsAsync(map);

                            }
                        }
                        else {
                            showAlertError(task.getException().getMessage());
                        }

                    }
                });
            }
        });


    }

    private void showAlertError(String mensaje){
        AlertDialog.Builder alert= new AlertDialog.Builder(this);
        alert.setTitle("Error");
        alert.setMessage(mensaje);
        alert.setCancelable(false);
        alert.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alert.create().show();
    }
}
