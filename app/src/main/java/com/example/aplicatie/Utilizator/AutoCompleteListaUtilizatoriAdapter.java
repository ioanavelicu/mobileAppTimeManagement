package com.example.aplicatie.Utilizator;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.aplicatie.FragmentProfil;
import com.example.aplicatie.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AutoCompleteListaUtilizatoriAdapter extends ArrayAdapter<Utilizator> {

    private List<Utilizator> listaCompletaUtilizatori;

    private ImageView imgUtilizatorCautat;

    private Context context;
    Utilizator utilizatorCurent;


    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference utilizatori = db.collection("Utilizatori");
    private DocumentReference docUtilizatorCurent = utilizatori.document(FirebaseAuth.getInstance().getUid());

    public AutoCompleteListaUtilizatoriAdapter(@NonNull Context context, @NonNull List<Utilizator> listaUtilizatori) {
        super(context, 0, listaUtilizatori);
        listaCompletaUtilizatori = new ArrayList<>(listaUtilizatori);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return filtruUtilizatori;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(getContext()).inflate(
                R.layout.rand_utilizator, parent, false);

        TextView tvUsernameUtilizatorCautat = convertView.findViewById(R.id.tvUsernameUtilizatorCautat);
        imgUtilizatorCautat = convertView.findViewById(R.id.imgUtilizatorCautat);
        Button btnTrimiteCerereDePrietenie = convertView.findViewById(R.id.btnTrimiteCerereDePrietenie);

        context = convertView.getContext();

        Utilizator utilizator = getItem(position);

        docUtilizatorCurent.get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                utilizatorCurent = task.getResult().toObject(Utilizator.class);
                                if(utilizatorCurent.getListaCereriDePrietenieTrimise().contains(utilizator.getUsername())) {
                                    btnTrimiteCerereDePrietenie.setBackgroundResource(R.drawable.buton_roz);
                                    btnTrimiteCerereDePrietenie.setText(R.string.btnCerereDePrietenieTrimisa);
                                }

                                utilizatori.whereArrayContains("listaCereriDePrietenieTrimise", utilizatorCurent.getUsername())
                                        .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                                    Utilizator u = documentSnapshot.toObject(Utilizator.class);
                                                    if(u.getUsername().equals(utilizator.getUsername())) {
                                                        btnTrimiteCerereDePrietenie.setBackgroundResource(R.drawable.buton_mov);
                                                        btnTrimiteCerereDePrietenie.setClickable(false);
                                                        btnTrimiteCerereDePrietenie.setText("Aveți deja o cerere de la acest utilziator");
                                                    }
                                                }
                                            }
                                        });
                            }
                        });

        if(utilizator != null) {
            tvUsernameUtilizatorCautat.setText(utilizator.getUsername());
            updateazaImagineProfil(utilizator);

            btnTrimiteCerereDePrietenie.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                     trimiteCerere(context, utilizator);
                     btnTrimiteCerereDePrietenie.setBackgroundResource(R.drawable.buton_roz);
                     btnTrimiteCerereDePrietenie.setText(R.string.btnCerereDePrietenieTrimisa);
                }
            });
        }

        return convertView;
    }

    private void trimiteCerere(Context context, Utilizator utilizatorCerere) {
        docUtilizatorCurent.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        Utilizator utilizator = task.getResult().toObject(Utilizator.class);
                        if(utilizator != null) {
                            utilizator.trimiteCererePrietenie(utilizatorCerere.getUsername());
                        }
                    }
                });
        Toast.makeText(context, "Cerere trimisa cu succes", Toast.LENGTH_SHORT).show();
    }

    private void updateazaImagineProfil(Utilizator uilizator) {
        Uri uriImagine = Uri.parse(uilizator.getPozaProfil());
        Picasso.get()
                .load(uriImagine)
                .into(imgUtilizatorCautat);
    }

    private Filter filtruUtilizatori = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<Utilizator> sugestii = new ArrayList<>();

            if(constraint == null || constraint.length() == 0) {
                sugestii.addAll(listaCompletaUtilizatori);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for(Utilizator utilizator : listaCompletaUtilizatori) {
                    if(utilizator.getUsername().toLowerCase().contains(filterPattern)) {
                        sugestii.add(utilizator);
                    }
                }
            }

            results.values = sugestii;
            results.count = sugestii.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();

            docUtilizatorCurent.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    Utilizator u = task.getResult().toObject(Utilizator.class);

//                    results.

                }
            });


            addAll((List)results.values);
            notifyDataSetChanged();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return ((Utilizator)resultValue).getUsername();
        }
    };
}
