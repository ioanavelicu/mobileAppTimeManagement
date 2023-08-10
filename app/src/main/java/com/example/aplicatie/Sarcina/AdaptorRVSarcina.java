package com.example.aplicatie.Sarcina;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aplicatie.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class AdaptorRVSarcina extends RecyclerView.Adapter<AdaptorRVSarcina.MyViewHolder> {

    private ArrayList<Sarcina> listaSarcini;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference utilizatori = db.collection("Utilizatori");
    private DocumentReference docUtilizatorCurent = utilizatori.document(FirebaseAuth.getInstance().getUid());

    public AdaptorRVSarcina(ArrayList<Sarcina> listaSarcini) {
        this.listaSarcini = listaSarcini;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CheckBox checkBoxSarcina;

        public MyViewHolder(final View view) {
            super(view);
            checkBoxSarcina = view.findViewById(R.id.checkBoxSarcina);
        }
    }

    @NonNull
    @Override
    public AdaptorRVSarcina.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_rv_lista_sarcini,
                parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String descriere = listaSarcini.get(position).getDescriereSarcina();
        holder.checkBoxSarcina.setText(descriere);
        if(listaSarcini.get(position).getEsteBifata()) {
            holder.checkBoxSarcina.setChecked(true);
        }
    }

    @Override
    public int getItemCount() {
        return listaSarcini.size();
    }

    public void stergeSarcina(String idTask, ArrayList<Sarcina> listaSarcini) {
        docUtilizatorCurent.collection("taskuri")
                .document(idTask).update("listaSarcini", listaSarcini);
    }
}
