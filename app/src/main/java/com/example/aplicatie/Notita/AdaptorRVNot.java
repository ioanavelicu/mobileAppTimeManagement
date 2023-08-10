package com.example.aplicatie.Notita;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aplicatie.DateConverter.DateConverter;
import com.example.aplicatie.ModificaNotitaActivity;
import com.example.aplicatie.R;
import com.example.aplicatie.Utilizator.Utilizator;
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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AdaptorRVNot extends FirestoreRecyclerAdapter<Notita, AdaptorRVNot.MyViewHolder>{
    public static final String MODIFICA_NOTITA = "modifica notita";
    public static final String ID_NOTITA = "id notita";
    private String usernameUtilizatorCurent = "";

    Context context;

    private AdaptorRVAlegePartajare adaptorRVAlegePartajare;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference utilizatori = db.collection("Utilizatori");
    private DocumentReference docUtilizatorCurent = utilizatori.document(FirebaseAuth.getInstance().getUid());

    public AdaptorRVNot(@NonNull FirestoreRecyclerOptions<Notita> options) {
        super(options);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDenumireNotita,tvContinut, tvDataNotita, tvUserUltimaModificare;
        private CardView cvNotita;
        private ImageView estePartajata;

        public MyViewHolder(final View view) {
            super(view);

            tvDenumireNotita = view.findViewById(R.id.tvDenNotita);
            tvContinut = view.findViewById(R.id.tvCont);
            tvDataNotita = view.findViewById(R.id.tvDataNotita);
            tvUserUltimaModificare = view.findViewById(R.id.tvUserUltimaModificare);
            cvNotita = view.findViewById(R.id.cvNotita);
            estePartajata = view.findViewById(R.id.estePartajata);
            estePartajata.setVisibility(View.GONE);
        }
    }

    @NonNull
    @Override
    public AdaptorRVNot.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_rv_notite,
                parent, false);
        final AdaptorRVNot.MyViewHolder myViewHolder = new AdaptorRVNot.MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    protected void onBindViewHolder(@NonNull AdaptorRVNot.MyViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull Notita model) {
        docUtilizatorCurent.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        usernameUtilizatorCurent = task.getResult().toObject(Utilizator.class).getUsername();
                    }
                });

        holder.tvDenumireNotita.setText(model.getDenumireNotita());
        holder.tvDenumireNotita.setSelected(true);

        holder.tvContinut.setText(model.getContinut());

        holder.tvDataNotita.setText(DateConverter.fromReminder(model.getData()));

        context = holder.itemView.getContext();

        int culoare = culoareNotita(position, context);
        holder.cvNotita.setCardBackgroundColor(culoare);

        if(model.isPartajata()) {
            holder.estePartajata.setVisibility(View.VISIBLE);
            holder.tvUserUltimaModificare.setText("Ultima modificare: " + model.getUltimaModificare());
            holder.tvUserUltimaModificare.setSelected(true);
        }

        holder.estePartajata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.dialog_prieteni_partajare);
                dialog.getWindow().setBackgroundDrawable(context.getDrawable(R.drawable.dialog_albastru));
                dialog.setCanceledOnTouchOutside(false);

                RecyclerView recyclerViewPartajare;
                Button btnOKPartajare;

                recyclerViewPartajare = dialog.findViewById(R.id.recycleViewPartajare);
                btnOKPartajare = dialog.findViewById(R.id.btnOKPartajare);

                Query query = utilizatori.whereIn("username", model.getUtilizatoriPartajare());
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isComplete()) {
                            List<Utilizator> listaPartajare = task.getResult().toObjects(Utilizator.class);
                            AdaptorRVPartajare adaptorRVPartajare = new AdaptorRVPartajare((ArrayList<Utilizator>) listaPartajare);

                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
                            recyclerViewPartajare.setLayoutManager(layoutManager);
                            recyclerViewPartajare.setItemAnimator(null);
                            recyclerViewPartajare.setAdapter(adaptorRVPartajare);
                        }
                    }
                });

                btnOKPartajare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        Animation animation = AnimationUtils.loadAnimation(context, R.anim.animatie);
        long delay = position * 100;
        animation.setStartOffset(delay);
        holder.itemView.startAnimation(animation);

        String idNotita = getSnapshots().getSnapshot(holder.getAbsoluteAdapterPosition()).getReference().getId();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, holder.cvNotita);
                popupMenu.inflate(R.menu.meniu_notita);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getItemId() == R.id.modificaNotita) {
                            Intent intent = new Intent(context, ModificaNotitaActivity.class);
                            intent.putExtra(MODIFICA_NOTITA, model);
                            intent.putExtra(ID_NOTITA, idNotita);
                            context.startActivity(intent);
                            ((Activity)holder.itemView.getContext()).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            return true;
                        }
                        if(item.getItemId() == R.id.stergeNotita) {
                            if(model.isPartajata() && !model.getProprietar().equals(usernameUtilizatorCurent)) {
                                Toast.makeText(context, "Nu puteți șterge notița altui prieten", Toast.LENGTH_SHORT).show();
                                return true;
                            } else {
                                stergeNotita(position);
                                if(model.isPartajata()) {
                                    utilizatori.whereIn("username", model.getUtilizatoriPartajare())
                                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    for(DocumentSnapshot prietenPartajare : task.getResult().getDocuments()) {
                                                        prietenPartajare.getReference().collection("Notite")
                                                                .document(idNotita).delete();
                                                    }
                                                }
                                            });
                                }
                                Toast.makeText(context, "Notiță ștearsă cu succes", Toast.LENGTH_SHORT).show();
                                return true;
                            }
                        }
                        if(item.getItemId() == R.id.partajeazaNotita) {
                            if(model.isPartajata() && !model.getProprietar().equals(usernameUtilizatorCurent)) {
                                Toast.makeText(context, "Nu puteți partaja notița altui prieten", Toast.LENGTH_SHORT).show();
                            } else {
                                arataListaPrieteni(context, model, idNotita);
                            }
                        }
                        return false;
                    }
                });
                Object menuHelper;
                Class[] argTypes;
                try {
                    Field fMenuHelper = PopupMenu.class.getDeclaredField("mPopup");
                    fMenuHelper.setAccessible(true);
                    menuHelper = fMenuHelper.get(popupMenu);
                    argTypes = new Class[]{boolean.class};
                    menuHelper.getClass().getDeclaredMethod("setForceShowIcon", argTypes).invoke(menuHelper, true);
                } catch (Exception e) {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                popupMenu.show();
            }
        });
    }

    private void arataListaPrieteni(Context context, Notita notita, String idNotita) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_alege_prieteni_partajare);
        dialog.getWindow().setBackgroundDrawable(context.getDrawable(R.drawable.dialog_albastru));
        dialog.setCanceledOnTouchOutside(false);

        RecyclerView recycleViewListaPrieteniPartajare;
        Button btnSalveazaPrieteni, btnInchidePartajare;

        recycleViewListaPrieteniPartajare = dialog.findViewById(R.id.recycleViewListaPrieteniPartajare);
        btnSalveazaPrieteni = dialog.findViewById(R.id.btnSalveazaPrieteni);
        btnInchidePartajare = dialog.findViewById(R.id.btnInchidePartajare);

        Query prieteni;
        if(!notita.getUtilizatoriPartajare().isEmpty()) {
            prieteni = utilizatori
                    .whereArrayContains("listaPrieteni", usernameUtilizatorCurent)
                    .whereNotIn("username", notita.getUtilizatoriPartajare());
        } else {
            prieteni = utilizatori
                    .whereArrayContains("listaPrieteni", usernameUtilizatorCurent);
        }


        prieteni.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<Utilizator> listaPrieteni = task.getResult().toObjects(Utilizator.class);
                        if(listaPrieteni.size() == 0) {
                            Toast.makeText(context, "Nu aveti prieteni cu care să puteți partaja notița", Toast.LENGTH_LONG).show();
                        }
                        adaptorRVAlegePartajare = new AdaptorRVAlegePartajare((ArrayList<Utilizator>) listaPrieteni);

                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
                        recycleViewListaPrieteniPartajare.setLayoutManager(layoutManager);
                        recycleViewListaPrieteniPartajare.setItemAnimator(null);
                        recycleViewListaPrieteniPartajare
                                .setAdapter(adaptorRVAlegePartajare);


                        btnSalveazaPrieteni.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                notita.setUtilizatoriPartajare(adaptorRVAlegePartajare.getListaPrieteniAlesi());
                                notita.setPartajata(true);
                                notita.setUltimaModificare(usernameUtilizatorCurent);

                                docUtilizatorCurent.collection("Notite")
                                                .document(idNotita).set(notita);

                                for(String username : adaptorRVAlegePartajare.getListaPrieteniAlesi()) {
                                    partajeazaNotita(notita, idNotita, usernameUtilizatorCurent, username);

                                }

                                dialog.dismiss();
                            }
                        });
                    }
                });



        btnInchidePartajare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        dialog.show();
    }

    private int culoareNotita(int postion, Context context) {
        List<Integer> listaCulori = new ArrayList<>();
        listaCulori.add(ContextCompat.getColor(context, R.color.culoare1));
        listaCulori.add(ContextCompat.getColor(context, R.color.culoare2));
        listaCulori.add(ContextCompat.getColor(context, R.color.culoare3));
        listaCulori.add(ContextCompat.getColor(context, R.color.culoare4));
        listaCulori.add(ContextCompat.getColor(context, R.color.culoare5));
        listaCulori.add(ContextCompat.getColor(context, R.color.culoare6));
        listaCulori.add(ContextCompat.getColor(context, R.color.culoare7));
        listaCulori.add(ContextCompat.getColor(context, R.color.culoare8));

        int culoareRandom = listaCulori.get(postion % listaCulori.size());
        return culoareRandom;
    }

    public void stergeNotita(int position) {
        getSnapshots().getSnapshot(position).getReference()
                .delete();
    }

    private void partajeazaNotita(Notita notita, String idNotita, String usernameUtilizatorCurent, String username) {
        Query cerere = utilizatori.whereIn("username", notita.getUtilizatoriPartajare());
        cerere.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot documentUtilizatorPartajare : queryDocumentSnapshots.getDocuments()) {
                    Utilizator utilizator = documentUtilizatorPartajare.toObject(Utilizator.class);
                    DocumentReference refUtilzatorPartajare = documentUtilizatorPartajare.getReference();

                    if(utilizator.getUsername().equals(username)) {
                        Notita notitaNoua = new Notita();
                        List<String> utilizatoriPartajati = new ArrayList<>(notita.getUtilizatoriPartajare());
                        notitaNoua.setProprietar(notita.getProprietar());
                        notitaNoua.setPartajata(notita.isPartajata());
                        notitaNoua.setDenumireNotita(notita.getDenumireNotita());
                        notitaNoua.setContinut(notita.getContinut());
                        notitaNoua.setUtilizatoriPartajare(utilizatoriPartajati);
                        notitaNoua.setUltimaModificare(usernameUtilizatorCurent);

                        List<String> lista = notitaNoua.getUtilizatoriPartajare();
                        lista.remove(utilizator.getUsername());
                        lista.add(usernameUtilizatorCurent);
                        refUtilzatorPartajare.collection("Notite").document(idNotita).set(notitaNoua);
                    } else {
                        refUtilzatorPartajare.collection("Notite").document(idNotita)
                                .update("utilizatoriPartajare", FieldValue.arrayUnion(username));

                    }
                }
            }
        });
    }
}
