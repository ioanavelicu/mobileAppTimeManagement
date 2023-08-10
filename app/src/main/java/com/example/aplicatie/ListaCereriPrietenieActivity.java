package com.example.aplicatie;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.aplicatie.Utilizator.AdapterRVListaCereriPrietenie;
import com.example.aplicatie.Utilizator.Utilizator;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

public class ListaCereriPrietenieActivity extends AppCompatActivity {
    private AdapterRVListaCereriPrietenie adaptorRVCereriPrietenie;
    private RecyclerView recyclerViewListaDePrieteni;

    Intent intent;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference utilizatori = db.collection("Utilizatori");
    private DocumentReference docUtilizatorCurent = utilizatori.document(FirebaseAuth.getInstance().getUid());

    @Override
    protected void onStart() {
        super.onStart();
        adaptorRVCereriPrietenie.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adaptorRVCereriPrietenie.stopListening();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_cereri_prietenie);

        initComponent();

        intent = getIntent();
        String username = intent.getStringExtra(FragmentProfil.USERNAME);

        Query cereriPrietenie = utilizatori
                .whereArrayContains("listaCereriDePrietenieTrimise", username);

        FirestoreRecyclerOptions<Utilizator> options = new FirestoreRecyclerOptions.Builder<Utilizator>()
                .setQuery(cereriPrietenie, Utilizator.class)
                .build();

        adaptorRVCereriPrietenie = new AdapterRVListaCereriPrietenie(options);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewListaDePrieteni.setLayoutManager(layoutManager);
        recyclerViewListaDePrieteni.setItemAnimator(null);
        recyclerViewListaDePrieteni.setAdapter(adaptorRVCereriPrietenie);
    }

    private void initComponent() {
        recyclerViewListaDePrieteni = findViewById(R.id.recycleViewListaCereriDePrietenie);
    }
}