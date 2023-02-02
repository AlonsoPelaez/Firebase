package com.dam2.m08.firebase;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Home extends AppCompatActivity {

    private Button btn_save;
    private EditText titulo;
    private EditText contenido;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        setTitle("Inicio");




        Intent intent = getIntent();
        String usuario= intent.getStringExtra("email");

        SharedPreferences prefer= getSharedPreferences(getString(R.string.prefer_file), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefer.edit();
        editor.putString("email",usuario);
        editor.apply();

        guardaDoc();

    }

    private void guardaDoc() {
        btn_save = findViewById(R.id.btn_save);
        titulo = findViewById(R.id.titulo_doc);
        contenido = findViewById(R.id.contenido_doc);

        HashMap map = new HashMap();
        List<String> docIds= new ArrayList<>();

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DocumentReference docRef = db.collection("documentos").document();
                map.put("titulo",titulo.getText().toString());
                map.put("contenido", contenido.getText().toString());
                docRef.set(map);
                String docId = docRef.getId();

                CollectionReference collectionReference = db.collection("documentos");

                collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            String id = documentSnapshot.getId();
                            docIds.add(id);
                        }
                    }
                });


                
//                docRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        List<String> documentsIds = new ArrayList<>();
//
//                    }
//                });
//
//
//
//                db.collection("documentos").document().set(map);
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
