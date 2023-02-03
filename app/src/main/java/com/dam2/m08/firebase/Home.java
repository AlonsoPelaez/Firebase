package com.dam2.m08.firebase;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Home extends AppCompatActivity {

    private Button btn_save;
    private EditText titulo;
    private EditText contenido;
    private String docId;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        btn_save = findViewById(R.id.btn_save);
        titulo = findViewById(R.id.titulo_doc);
        contenido = findViewById(R.id.contenido_doc);
        setTitle("Inicio");

        cargaDoc();




        Intent intent = getIntent();
        String usuario= intent.getStringExtra("email");

        SharedPreferences prefer= getSharedPreferences(getString(R.string.prefer_file), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefer.edit();
        editor.putString("email",usuario);
        editor.apply();

        guardaDoc();


    }

    private void cargaDoc() {
        if (docId!=null){
            db.collection("documentos").document(docId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){

                        System.out.println("id------------------->" + task.getResult().getId());
                        System.out.println("titulo------------------->" + task.getResult().getData().get("titulo"));
                        titulo.setText(task.getResult().getData().get("titulo").toString());
                        System.out.println("contenido--------------------->"+ task.getResult().getData().get("contenido"));
                        titulo.setText(task.getResult().getData().get("contenido").toString());
                    }
                    else {
                        System.out.println("ha ocurrido un error " + task.getException());
                    }
                }
            });
        }

    }

    private void guardaDoc() {

        HashMap map = new HashMap();


        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map.put("titulo",titulo.getText().toString());
                map.put("contenido", contenido.getText().toString());


                if (docId != null){
                    DocumentReference documentReference = db.collection("documentos").document(docId);
                    documentReference.set(map);

                }
                else {
                    // crea la bbdd con el nombre de documentos e introduce el titulo y contenido
                    DocumentReference docRef = db.collection("documentos").document();
                    docRef.set(map);


                    //obtiene el id del documento creado
                    docId = docRef.getId();
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
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);

    }
}
