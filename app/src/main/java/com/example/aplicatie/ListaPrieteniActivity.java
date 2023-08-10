package com.example.aplicatie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;

import com.example.aplicatie.Utilizator.AdapterRVListaCereriPrietenie;
import com.example.aplicatie.Utilizator.AdapterRVListaPrieteni;
import com.example.aplicatie.Utilizator.Utilizator;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class ListaPrieteniActivity extends AppCompatActivity {
    private AdapterRVListaPrieteni adapterRVListaPrieteni;
    private RecyclerView recyclerViewListaPrieteni;

    Intent intent;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference utilizatori = db.collection("Utilizatori");
    private DocumentReference docUtilizatorCurent = utilizatori.document(FirebaseAuth.getInstance().getUid());

    @Override
    protected void onStart() {
        super.onStart();
        adapterRVListaPrieteni.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapterRVListaPrieteni.stopListening();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_prieteni);

        initComponents();

        intent = getIntent();
        String username = intent.getStringExtra(FragmentProfil.USERNAME);

        Query cereriPrietenie = utilizatori
                .whereArrayContains("listaPrieteni", username);
        FirestoreRecyclerOptions<Utilizator> options = new FirestoreRecyclerOptions.Builder<Utilizator>()
                .setQuery(cereriPrietenie, Utilizator.class)
                .build();

        adapterRVListaPrieteni = new AdapterRVListaPrieteni(options);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewListaPrieteni.setLayoutManager(layoutManager);
        recyclerViewListaPrieteni.setItemAnimator(null);
        recyclerViewListaPrieteni.setAdapter(adapterRVListaPrieteni);


    }

    private void initComponents() {
        recyclerViewListaPrieteni = findViewById(R.id.recycleViewListaPrieteni);
    }
}