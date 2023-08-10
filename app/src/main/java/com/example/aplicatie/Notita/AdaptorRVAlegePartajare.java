package com.example.aplicatie.Notita;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

public class AdaptorRVAlegePartajare extends RecyclerView.Adapter<AdaptorRVAlegePartajare.MyViewHolder>{

    private ArrayList<Utilizator> listaPrieteni;
    private ArrayList<String> listaPrieteniAlesi = new ArrayList<>();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference utilizatori = db.collection("Utilizatori");
    private DocumentReference docUtilizatorCurent = utilizatori.document(FirebaseAuth.getInstance().getUid());

    public AdaptorRVAlegePartajare(ArrayList<Utilizator> listaPrieteni) {
        this.listaPrieteni = listaPrieteni;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CheckBox cbAlegePrieten;
        private ImageView imgPrietenPartajare;
        private TextView tvUsernamePrietenPartajare, tvNumePrietenPartajare;

        public MyViewHolder(final View view) {
            super(view);
            cbAlegePrieten = view.findViewById(R.id.cbAlegePrieten);
            imgPrietenPartajare = view.findViewById(R.id.imgPrietenPartajare);
            tvUsernamePrietenPartajare = view.findViewById(R.id.tvUsernamePrietenPartajare);
            tvNumePrietenPartajare = view.findViewById(R.id.tvNumePrietenPartajare);
        }
    }

    @NonNull
    @Override
    public AdaptorRVAlegePartajare.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_rv_alege_prieten_partajare,
                parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptorRVAlegePartajare.MyViewHolder holder, int position) {
        Utilizator utilizator = listaPrieteni.get(position);

        String username = utilizator.getUsername();
        holder.tvUsernamePrietenPartajare.setText(username);

        String nume = utilizator.getNume();
        holder.tvNumePrietenPartajare.setText(nume);

        updateazaImagineProfil(utilizator, holder.imgPrietenPartajare);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.cbAlegePrieten.setChecked(!holder.cbAlegePrieten.isChecked());
            }
        });

        holder.cbAlegePrieten.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    listaPrieteniAlesi.add(utilizator.getUsername());
                } else {
                    listaPrieteniAlesi.remove(utilizator.getUsername());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaPrieteni.size();
    }

    private void updateazaImagineProfil(Utilizator uilizator, ImageView imgPrieten) {
        Uri uriImagine = Uri.parse(uilizator.getPozaProfil());
        Picasso.get()
                .load(uriImagine)
                .into(imgPrieten);
    }

    public ArrayList<String> getListaPrieteniAlesi() {
        return this.listaPrieteniAlesi;
    }
}
