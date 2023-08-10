package com.example.aplicatie.Obiectiv;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aplicatie.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class AdaptorRVObiective extends FirestoreRecyclerAdapter<Obiectiv, AdaptorRVObiective.MyViewHolder> {

    private ProgressBar progressBar;
    private TextView tvProgres;
    Context context;
    Dialog dialog;

    private EditText etAdaugaDescriereObiectiv;
    private Button btnInchideObiectiv, btnSalveazaObiectiv;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference utilizatori = db.collection("Utilizatori");
    private DocumentReference docUtilizatorCurent = utilizatori.document(FirebaseAuth.getInstance().getUid());

    public AdaptorRVObiective(@NonNull FirestoreRecyclerOptions<Obiectiv> options, ProgressBar progressBar, TextView tvProgres) {
        super(options);
        this.progressBar = progressBar;
        this.progressBar.setMax(100);
        this.progressBar.setProgress(0);
        this.tvProgres = tvProgres;
    }

    private float calculeazaProgres() {
        int nrObiective = 0;
        for(int i = 0; i < getItemCount(); i++) {
            Obiectiv obiectiv = getItem(i);
            if(obiectiv != null && obiectiv.isBifat()) {
                nrObiective++;
            }
        }
        float progres = (nrObiective * 100.0f / getItemCount());

        return progres;
//        return Math.min(100, progres);
//        return Math.max(0, Math.min(100, progres));
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private CheckBox checkboxDescriereObiectiv;
        private ImageButton btnModificaObiectiv;

        public MyViewHolder(final View view) {
            super(view);

            checkboxDescriereObiectiv = view.findViewById(R.id.checkboxDescriereObiectiv);
            btnModificaObiectiv = view.findViewById(R.id.btnModificaObiectiv);
        }
    }

    @NonNull
    @Override
    public AdaptorRVObiective.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_rv_lista_obiective,
                parent, false);
        final MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    protected void onBindViewHolder(@NonNull AdaptorRVObiective.MyViewHolder holder, int position, @NonNull Obiectiv model) {
        holder.checkboxDescriereObiectiv.setText(model.getDescriere());
        Obiectiv obiectiv = getSnapshots().getSnapshot(holder.getAbsoluteAdapterPosition()).toObject(Obiectiv.class);
        holder.checkboxDescriereObiectiv.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    obiectiv.setBifat(true);
                    getSnapshots().getSnapshot(holder.getAbsoluteAdapterPosition()).getReference().set(obiectiv);
                    float progres = calculeazaProgres();
                    seteazaProgres(progres);

                    docUtilizatorCurent.collection("Obiective").get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    int nrObiectiveBifate = 0;
                                    for(Obiectiv obiectiv : task.getResult().toObjects(Obiectiv.class)) {
                                        if(obiectiv.isBifat()) {
                                            nrObiectiveBifate++;
                                        }
                                    }

                                    if(nrObiectiveBifate == task.getResult().getDocuments().size()) {
                                        dialog = new Dialog(context);
                                        dialog.setContentView(R.layout.dialog_obiective_complete);
                                        dialog.getWindow().setBackgroundDrawable(context.getDrawable(R.drawable.dialog_albastru));
                                        dialog.setCanceledOnTouchOutside(false);

                                        ImageView imgCloseObiectiveComplete = dialog.findViewById(R.id.imgCloseObiectiveComplete);
                                        imgCloseObiectiveComplete.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                progressBar.setProgress(0);
                                                tvProgres.setText("Progres");
                                                dialog.dismiss();
                                            }
                                        });
                                        dialog.show();

                                        docUtilizatorCurent.collection("Obiective").get()
                                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                                            queryDocumentSnapshots.getDocuments()
                                                                    .forEach(documentSnapshot -> documentSnapshot.getReference().delete());
                                                        }
                                                    }
                                                });
                                    }
                                }
                            });

                } else {
                    obiectiv.setBifat(false);
                    getSnapshots().getSnapshot(holder.getAbsoluteAdapterPosition()).getReference().set(obiectiv);
                    float progres = calculeazaProgres();
                    seteazaProgres(progres);
                }
            }
        });

        if(obiectiv.isBifat()) {
            holder.checkboxDescriereObiectiv.setChecked(true);
            float progres = calculeazaProgres();
            seteazaProgres(progres);
        } else {
            holder.checkboxDescriereObiectiv.setChecked(false);
            float progres = calculeazaProgres();
            seteazaProgres(progres);
        }

        context = holder.itemView.getContext();
        holder.btnModificaObiectiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modificaObiectiv(context, holder, model);
            }
        });

        tvProgres.setText(String.format("%s %d %%", "progres: ", progressBar.getProgress()));
    }

    private void seteazaProgres(float progres) {
        if(progres != 0) {
            progressBar.setProgress((int) progres);
        } else {
            progressBar.setProgress(0);
        }

    }

    public void stergeObiectiv(int position) {
        float progres = calculeazaProgres();
        seteazaProgres(progres);
        getSnapshots().getSnapshot(position).getReference()
                .delete();
    }

    private Obiectiv creareObiectiv(Obiectiv obiectiv) {
        String descriereObiectiv = etAdaugaDescriereObiectiv.getText().toString();
        Boolean isBifat = obiectiv.isBifat();

        return new Obiectiv(descriereObiectiv, isBifat);
    }

    private void modificaObiectiv(Context context, AdaptorRVObiective.MyViewHolder holder, Obiectiv obiectiv) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_adauga_obiectiv);

        etAdaugaDescriereObiectiv = dialog.findViewById(R.id.etAdaugaDescriereOBiecitv);
        btnInchideObiectiv = dialog.findViewById(R.id.btnInchideObiectiv);
        btnSalveazaObiectiv = dialog.findViewById(R.id.btnSalveazaObiectiv);

        btnSalveazaObiectiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Obiectiv obiectivModificat = creareObiectiv(obiectiv);
                if(obiectivModificat != null) {
                    getSnapshots().getSnapshot(holder.getAbsoluteAdapterPosition()).getReference().set(obiectivModificat)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(context, "OBIECTIV MODIFICAT CU SUCCES", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(context, "EROARE LA MODIFICARE OBIECTIV" + e.getMessage(), Toast.LENGTH_SHORT).show();
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

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.show();
    }
}
