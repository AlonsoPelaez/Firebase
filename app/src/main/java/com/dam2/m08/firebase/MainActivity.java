package com.dam2.m08.firebase;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

public class MainActivity extends AppCompatActivity {

    private Button btn_inicioSesion;
    private Button btn_registro;
    private View layoutPrincipal;
    private Boolean muestra_btn_registro;
    private Boolean usuario_A_registrado;
    private Boolean usuario_B_registrado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);






        layoutPrincipal = findViewById(R.id.layoutPrincipal);
        btn_inicioSesion = findViewById(R.id.iniciaSesion);
        btn_registro = findViewById(R.id.registro);

        btn_inicioSesion.setOnClickListener(view -> {
            Intent intent= new Intent(MainActivity.this, Login.class);
            startActivity(intent);
        });
        btn_registro.setOnClickListener(view -> {
            Intent intent= new Intent(MainActivity.this, Register.class);
            startActivity(intent);
        });
        compruebaEstadoDeUsuarios();
        session();

    }

    private void compruebaEstadoDeUsuarios(){


        //remoteconfig
        FirebaseRemoteConfig.getInstance().fetchAndActivate().addOnCompleteListener(new OnCompleteListener<Boolean>() {
            @Override
            public void onComplete(@NonNull Task<Boolean> task) {
                if (!task.isSuccessful()){
                    System.out.println("ha ocurrido un error en el remote config" + task.getException());
                    return;
                }
                muestra_btn_registro = FirebaseRemoteConfig.getInstance().getBoolean("muestra_btn_registro");
                usuario_A_registrado = FirebaseRemoteConfig.getInstance().getBoolean("usuario_A_registrado");
                usuario_B_registrado = FirebaseRemoteConfig.getInstance().getBoolean("usuario_B_registrado");

                //codigo llama a la base de datos si el primer y el segundo usuario esta registrado cambia #usuario_A_registrado,
                // #usuario_B_registrado a true y llama a un if que compruebe el estado de las variables de los botones
                // si ambos son true el #btn_registro se pone invisible al igual que la variable #muestra_btn_registro



                if (usuario_A_registrado && usuario_B_registrado){
                    muestra_btn_registro=false;
                }
                if (!muestra_btn_registro){
                    btn_registro.setVisibility(View.INVISIBLE);
                }


            }
        });
    }

    private void session(){
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