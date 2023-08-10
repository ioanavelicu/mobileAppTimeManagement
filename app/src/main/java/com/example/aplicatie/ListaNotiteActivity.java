package com.example.aplicatie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.example.aplicatie.Notita.AdaptorRVNot;
import com.example.aplicatie.Notita.Notita;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ListaNotiteActivity extends AppCompatActivity {
    private AdaptorRVNot adaptorRVNot;

    private FloatingActionButton btnAdaugaNotita;
    private RecyclerView recyclerViewListaNotite;
    private EditText etCautaNotita;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference utilizatori = db.collection("Utilizatori");
    private DocumentReference docUtilizatorCurent = utilizatori.document(FirebaseAuth.getInstance().getUid());

    @Override
    protected void onStart() {
        super.onStart();
        adaptorRVNot.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adaptorRVNot.stopListening();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notite);

        initComponents();

        getWindow().setSoftInputMode(WindowManager.
                LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getWindow().setSoftInputMode(WindowManager.
                LayoutParams.SOFT_INPUT_ADJUST_PAN);

        btnAdaugaNotita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddNotitaActivity.class);
                startActivity(intent);
            }
        });

        Query notiteFS = docUtilizatorCurent.collection("Notite");
        FirestoreRecyclerOptions<Notita> options = new FirestoreRecyclerOptions.Builder<Notita>()
                .setQuery(notiteFS, Notita.class)
                .build();

        populeazaRecyclerView(options);

        etCautaNotita.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtru(s.toString().toLowerCase().trim());
                adaptorRVNot.startListening();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void filtru(String cuvant) {
        if(cuvant.isEmpty()) {
            Query notiteFS = docUtilizatorCurent.collection("Notite");
            FirestoreRecyclerOptions<Notita> options = new FirestoreRecyclerOptions.Builder<Notita>()
                    .setQuery(notiteFS, Notita.class)
                    .build();

            populeazaRecyclerView(options);
        } else {
            Query filtrare = docUtilizatorCurent.collection("Notite")
                    .whereGreaterThanOrEqualTo("denumireNotita", cuvant)
                    .whereLessThanOrEqualTo("denumireNotita", cuvant + "\uf8ff");
            FirestoreRecyclerOptions<Notita> options = new FirestoreRecyclerOptions.Builder<Notita>()
                    .setQuery(filtrare, Notita.class)
                    .build();

            populeazaRecyclerView(options);
        }
    }

    private void populeazaRecyclerView(FirestoreRecyclerOptions<Notita> options) {
        adaptorRVNot = new AdaptorRVNot(options);
        RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        recyclerViewListaNotite.setItemAnimator(null);
        recyclerViewListaNotite.setLayoutManager(layoutManager);
        recyclerViewListaNotite.setAdapter(adaptorRVNot);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void initComponents() {
        btnAdaugaNotita = findViewById(R.id.btnAdaugaNotita);
        recyclerViewListaNotite = findViewById(R.id.recycleViewListaNotite);
        etCautaNotita = findViewById(R.id.etCautaNotita);
    }
}