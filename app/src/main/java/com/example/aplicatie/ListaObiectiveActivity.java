package com.example.aplicatie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aplicatie.Obiectiv.AdaptorRVObiective;
import com.example.aplicatie.Obiectiv.Obiectiv;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class ListaObiectiveActivity extends AppCompatActivity {
    private AdaptorRVObiective adaptorRVObiective;

    private FloatingActionButton btnAdaugaObiectiv;
    private RecyclerView recyclerViewListaObiective;
    private ProgressBar progressBarObiective;
    private TextView tvProgres;

    private EditText etAdaugaDescriereObiectiv;
    private Button btnInchideObiectiv, btnSalveazaObiectiv;
    private LinearLayout linearLayout;
    private ConstraintLayout constraintLayoutDialogObiectiv;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference utilizatori = db.collection("Utilizatori");
    private DocumentReference docUtilizatorCurent = utilizatori.document(FirebaseAuth.getInstance().getUid());

    private Dialog dialog;

    @Override
    protected void onStart() {
        super.onStart();
        adaptorRVObiective.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adaptorRVObiective.stopListening();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_obiective);

        initComponents();

        btnAdaugaObiectiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adaugaObiectiv();
            }
        });

        Query obiectiveFS = docUtilizatorCurent.collection("Obiective");
        FirestoreRecyclerOptions<Obiectiv> options = new FirestoreRecyclerOptions.Builder<Obiectiv>()
                .setQuery(obiectiveFS, Obiectiv.class)
                .build();

        adaptorRVObiective = new AdaptorRVObiective(options, progressBarObiective, tvProgres);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewListaObiective.setItemAnimator(null);
        recyclerViewListaObiective.setLayoutManager(layoutManager);
        recyclerViewListaObiective.setAdapter(adaptorRVObiective);



        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if(direction == ItemTouchHelper.LEFT) {
                    adaptorRVObiective.stergeObiectiv(viewHolder.getBindingAdapterPosition());
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(ListaObiectiveActivity.this, R.color.delete_red))
                        .addSwipeLeftActionIcon(R.drawable.sterge_rand_rv)
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }).attachToRecyclerView(recyclerViewListaObiective);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    private void adaugaObiectiv() {
        Dialog dialog = new Dialog(ListaObiectiveActivity.this);

        dialog.setContentView(R.layout.dialog_adauga_obiectiv);
        dialog.getWindow().setBackgroundDrawable(getApplicationContext().getDrawable(R.drawable.dialog_albastru));
        dialog.setCanceledOnTouchOutside(false);

        etAdaugaDescriereObiectiv = dialog.findViewById(R.id.etAdaugaDescriereOBiecitv);
        btnInchideObiectiv = dialog.findViewById(R.id.btnInchideObiectiv);
        btnSalveazaObiectiv = dialog.findViewById(R.id.btnSalveazaObiectiv);
        constraintLayoutDialogObiectiv = dialog.findViewById(R.id.constraintLayoutDialogObiectiv);

        btnSalveazaObiectiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Obiectiv obiectiv = creareObiectiv();
                if(obiectiv != null) {
                    docUtilizatorCurent.collection("Obiective").add(obiectiv)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(ListaObiectiveActivity.this, "OBIECTIV ADAUGAT CU SUCCES", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(ListaObiectiveActivity.this, "EROARE LA ADAUGARE OBIECTIV" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }


                dialog.cancel();
            }
        });

        btnInchideObiectiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        dialog.show();
    }

    private Obiectiv creareObiectiv() {
        String descriereObiectiv = etAdaugaDescriereObiectiv.getText().toString();

        return new Obiectiv(descriereObiectiv);
    }

    private void initComponents() {
        btnAdaugaObiectiv = findViewById(R.id.btnAdaugaObiectiv);
        recyclerViewListaObiective = findViewById(R.id.recyclerViewListaObiective);
        progressBarObiective = findViewById(R.id.progressBarObiective);
        tvProgres = findViewById(R.id.tvProgres);
    }
}