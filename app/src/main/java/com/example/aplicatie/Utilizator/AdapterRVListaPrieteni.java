package com.example.aplicatie.Utilizator;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aplicatie.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
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

public class AdapterRVListaPrieteni extends FirestoreRecyclerAdapter<Utilizator, AdapterRVListaPrieteni.MyViewHolder> {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference utilizatori = db.collection("Utilizatori");
    private DocumentReference docUtilizatorCurent = utilizatori.document(FirebaseAuth.getInstance().getUid());

    String username;

    public AdapterRVListaPrieteni(@NonNull FirestoreRecyclerOptions<Utilizator> options) {
        super(options);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvUsernamePrieten, tvNumePrieten;
        private ImageView imgPrieten;
        private ImageButton btnStergePrieten;

        public MyViewHolder(@NonNull View view) {
            super(view);

            tvUsernamePrieten = view.findViewById(R.id.tvUsernamePrieten);
            tvNumePrieten = view.findViewById(R.id.tvNumePrieten);
            imgPrieten = view.findViewById(R.id.imgPrieten);
            btnStergePrieten = view.findViewById(R.id.btnStergePrieten);
        }
    }

    @Override
    protected void onBindViewHolder(@NonNull AdapterRVListaPrieteni.MyViewHolder holder, int position, @NonNull Utilizator model) {
        docUtilizatorCurent.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                username = task.getResult().toObject(Utilizator.class).getUsername();
            }
        });

        holder.tvUsernamePrieten.setText(model.getUsername());
        holder.tvNumePrieten.setText(model.getNume());

        updateazaImagineProfil(model, holder.imgPrieten);

        holder.btnStergePrieten.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stergePrieten(model);
                Toast.makeText(v.getContext(), "PRIETEN STERS CU SUCCES", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @NonNull
    @Override
    public AdapterRVListaPrieteni.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_rv_lista_prieteni,
                parent, false);
        final AdapterRVListaPrieteni.MyViewHolder myViewHolder = new MyViewHolder(view);

        return myViewHolder;
    }

    private void updateazaImagineProfil(Utilizator uilizator, ImageView imgPrieten) {
        Uri uriImagine = Uri.parse(uilizator.getPozaProfil());
        Picasso.get()
                .load(uriImagine)
                .fit()
                .into(imgPrieten);
    }


    private void stergePrieten(Utilizator model) {
        Query qPrieten = utilizatori.whereEqualTo("username", model.getUsername());
        qPrieten.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isComplete()) {
                    for(QueryDocumentSnapshot utilizator : task.getResult()) {
                        DocumentReference prietenDeSters = utilizatori.document(utilizator.getId());
                        prietenDeSters.update("listaPrieteni", FieldValue.arrayRemove(username));
                        docUtilizatorCurent.update("listaPrieteni", FieldValue.arrayRemove(utilizator.toObject(Utilizator.class).getUsername()));
                    }
                }
            }
        });
    }
}
