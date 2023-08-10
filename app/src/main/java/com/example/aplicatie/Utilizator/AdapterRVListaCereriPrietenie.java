package com.example.aplicatie.Utilizator;

import android.content.Context;
import android.net.Uri;
import android.view.ContentInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aplicatie.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

public class AdapterRVListaCereriPrietenie extends FirestoreRecyclerAdapter<Utilizator, AdapterRVListaCereriPrietenie.MyViewHolder> {

    String username;


    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference utilizatori = db.collection("Utilizatori");
    private DocumentReference docUtilizatorCurent = utilizatori.document(FirebaseAuth.getInstance().getUid());

    public AdapterRVListaCereriPrietenie(@NonNull FirestoreRecyclerOptions<Utilizator> options) {
        super(options);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvUsernameCerere;
        private Button btnAcceptaCerere, btnRespingeCerere;
        private ImageView imgCerePrietenie;

        public MyViewHolder(@NonNull View view) {
            super(view);

            tvUsernameCerere = view.findViewById(R.id.tvUsernameCerere);
            btnAcceptaCerere = view.findViewById(R.id.btnAcceptaCerere);
            btnRespingeCerere = view.findViewById(R.id.btnRespingeCerere);
            imgCerePrietenie = view.findViewById(R.id.imgCererePrietenie);
        }
    }


    @NonNull
    @Override
    public AdapterRVListaCereriPrietenie.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_rv_lista_cereri_prietenie,
                parent, false);
        final  MyViewHolder myViewHolder = new MyViewHolder(view);

        return myViewHolder;
    }

    @Override
    protected void onBindViewHolder(@NonNull AdapterRVListaCereriPrietenie.MyViewHolder holder, int position, @NonNull Utilizator model) {
        holder.tvUsernameCerere.setText(model.getUsername());

        docUtilizatorCurent.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                username = task.getResult().toObject(Utilizator.class).getUsername();
            }
        });

        holder.btnAcceptaCerere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptaCererePrietenie(model, username);
            }
        });

        holder.btnRespingeCerere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               respingeCererePrietenie(model, username);
            }
        });

        updateazaImagineProfil(model, holder.imgCerePrietenie);
    }

    private void updateazaImagineProfil(Utilizator uilizator, ImageView imgCererePrietenie) {
        Uri uriImagine = Uri.parse(uilizator.getPozaProfil());
        Picasso.get()
                .load(uriImagine)
                .into(imgCererePrietenie);
    }

    private void acceptaCererePrietenie(Utilizator model, String username) {
        Query cerere = utilizatori.whereEqualTo("username", model.getUsername());
        cerere.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isComplete()) {
                    for(QueryDocumentSnapshot utilizator : task.getResult()) {
                        docUtilizatorCurent.update("listaPrieteni",
                                FieldValue.arrayUnion(utilizator.toObject(Utilizator.class).getUsername()));
                        DocumentReference utilizatorCerere = utilizatori.document(utilizator.getId());
                        utilizatorCerere.update("listaPrieteni", FieldValue.arrayUnion(username));
                        utilizatorCerere.update("listaCereriDePrietenieTrimise", FieldValue.arrayRemove(username));
                    }
                }
            }
        });
    }

    private void respingeCererePrietenie(Utilizator model, String username) {
        Query cerere = utilizatori.whereEqualTo("username", model.getUsername());
        cerere.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isComplete()) {
                    for(QueryDocumentSnapshot utilizator : task.getResult()) {
                        DocumentReference utilizatorCerere = utilizatori.document(utilizator.getId());
                        utilizatorCerere.update("listaCereriDePrietenieTrimise", FieldValue.arrayRemove(username));
                    }
                }
            }
        });
    }
}
