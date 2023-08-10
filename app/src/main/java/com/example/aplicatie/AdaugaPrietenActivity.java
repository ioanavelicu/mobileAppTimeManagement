package com.example.aplicatie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.example.aplicatie.Utilizator.AutoCompleteListaUtilizatoriAdapter;
import com.example.aplicatie.Utilizator.Utilizator;
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
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class AdaugaPrietenActivity extends AppCompatActivity {

    private List<Utilizator> listaUtilizatori = new ArrayList<>();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference utilizatori = db.collection("Utilizatori");
    private DocumentReference docUtilizatorCurent = utilizatori.document(FirebaseAuth.getInstance().getUid());

    private String username;
    Intent intent;

    private AutoCompleteTextView tvCautaUtilizator;

    private AutoCompleteListaUtilizatoriAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adauga_prieten);

        docUtilizatorCurent.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                username = task.getResult().toObject(Utilizator.class).getUsername();
            }
        });
        adaugaListaUtilizatori();
    }

    private void adaugaListaUtilizatori() {

        utilizatori.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        tvCautaUtilizator = findViewById(R.id.tvCautaUtilizator);

                        listaUtilizatori = queryDocumentSnapshots.toObjects(Utilizator.class);

                        intent = getIntent();
                        username = intent.getStringExtra(FragmentProfil.USERNAME);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            listaUtilizatori = listaUtilizatori.stream().filter(u -> !u.getUsername().equals(username))
                                    .filter(u -> !u.getListaPrieteni().contains(username))
                                    .collect(Collectors.toList());
                        }

                        adapter = new AutoCompleteListaUtilizatoriAdapter(AdaugaPrietenActivity.this, AdaugaPrietenActivity.this.listaUtilizatori);
                        tvCautaUtilizator.setAdapter(adapter);
                    }
                });
    }
}