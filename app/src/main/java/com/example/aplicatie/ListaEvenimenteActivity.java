package com.example.aplicatie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.widget.Toast;

import com.example.aplicatie.Eveniment.AdaptorRVEveniment;
import com.example.aplicatie.Eveniment.Eveniment;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class ListaEvenimenteActivity extends AppCompatActivity {
    private AdaptorRVEveniment adaptorRVEveniment;

    private RecyclerView recyclerViewListaEvenimente;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference utilizatori = db.collection("Utilizatori");
    private DocumentReference docUtilizatorCurent = utilizatori.document(FirebaseAuth.getInstance().getUid());

    @Override
    protected void onStart() {
        super.onStart();
        adaptorRVEveniment.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adaptorRVEveniment.stopListening();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_evenimente);

        recyclerViewListaEvenimente = findViewById(R.id.recycleViewListaEvenimente);

        Query evenimenteFS = docUtilizatorCurent.collection("Evenimente").orderBy("dataEveniment");
        FirestoreRecyclerOptions<Eveniment> options = new FirestoreRecyclerOptions.Builder<Eveniment>()
                .setQuery(evenimenteFS, Eveniment.class)
                .build();

        adaptorRVEveniment = new AdaptorRVEveniment(options);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewListaEvenimente.setLayoutManager(layoutManager);
        recyclerViewListaEvenimente.setItemAnimator(null);
        recyclerViewListaEvenimente.setAdapter(adaptorRVEveniment);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if(direction == ItemTouchHelper.LEFT) {
                    adaptorRVEveniment.stergeEveniment(viewHolder.getAdapterPosition());
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(ListaEvenimenteActivity.this, R.color.delete_red))
                        .addSwipeLeftActionIcon(R.drawable.sterge_rand_rv)
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }).attachToRecyclerView(recyclerViewListaEvenimente);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}