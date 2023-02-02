package com.dam2.m08.firebase;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button btn_inicioSesion;
    private Button btn_registro;
    private View layoutPrincipal;
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
        session();
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