package com.example.aplicatie.Notita;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aplicatie.R;
import com.example.aplicatie.Utilizator.Utilizator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdaptorRVPartajare extends RecyclerView.Adapter<AdaptorRVPartajare.MyViewHolder>{

    private ArrayList<Utilizator> listaPrieteniPartajare;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference utilizatori = db.collection("Utilizatori");
    private DocumentReference docUtilizatorCurent = utilizatori.document(FirebaseAuth.getInstance().getUid());

    public AdaptorRVPartajare(ArrayList<Utilizator> listaPrieteniPartajare) {
        this.listaPrieteniPartajare = listaPrieteniPartajare;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgPartajare;
        private TextView tvUsernamePartajare, tvNumePartajare;

        public MyViewHolder(final View view) {
            super(view);
            imgPartajare = view.findViewById(R.id.imgPartajare);
            tvUsernamePartajare = view.findViewById(R.id.tvUsernamePartajare);
            tvNumePartajare = view.findViewById(R.id.tvNumePartajare);
        }
    }

    @NonNull
    @Override
    public AdaptorRVPartajare.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_rv_prieteni_partajare,
                parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptorRVPartajare.MyViewHolder holder, int position) {
        Utilizator utilizator = listaPrieteniPartajare.get(position);

        String username = utilizator.getUsername();
        holder.tvUsernamePartajare.setText(username);

        String nume = utilizator.getNume();
        holder.tvNumePartajare.setText(nume);

        updateazaImagineProfil(utilizator, holder.imgPartajare);
    }

    @Override
    public int getItemCount() {
        return listaPrieteniPartajare.size();
    }

    private void updateazaImagineProfil(Utilizator uilizator, ImageView imgPrieten) {
        Uri uriImagine = Uri.parse(uilizator.getPozaProfil());
        Picasso.get()
                .load(uriImagine)
                .into(imgPrieten);
    }
}
