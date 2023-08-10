package com.example.aplicatie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.aplicatie.Notita.AdaptorRVNot;
import com.example.aplicatie.Notita.Notita;
import com.example.aplicatie.Utilizator.Utilizator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.List;

public class ModificaNotitaActivity extends AppCompatActivity {
    private EditText etDenumireNotitaModificata, etContinutModificat;
    private Button btnSalveazaNotitaModificata;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference utilizatori = db.collection("Utilizatori");
    private DocumentReference docUtilizatorCurent = utilizatori.document(FirebaseAuth.getInstance().getUid());

    private String usernameUtilizatorCurent = "";

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifica_notita);

        initComponents();

        docUtilizatorCurent.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        usernameUtilizatorCurent = task.getResult().toObject(Utilizator.class).getUsername();
                    }
                });

        intent = getIntent();
        Notita notita = (Notita) intent.getSerializableExtra(AdaptorRVNot.MODIFICA_NOTITA);
        String idNotita = (String) intent.getSerializableExtra(AdaptorRVNot.ID_NOTITA);
        if(notita != null) {
            etDenumireNotitaModificata.setText(notita.getDenumireNotita());;
            etContinutModificat.setText(notita.getContinut());

            btnSalveazaNotitaModificata.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isValid()) {
                        Notita notitaModificata = creareNotita(notita.getUtilizatoriPartajare(), notita.isPartajata(), notita.getProprietar());
                        if(notitaModificata != null) {
                            docUtilizatorCurent.collection("Notite")
                                    .document(idNotita).set(notitaModificata)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(ModificaNotitaActivity.this, "NOTITA MODIFICATA CU SUCCES", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(ModificaNotitaActivity.this, "EROARE LA MODIFICARE NOTITA" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });

                            for(String username: notita.getUtilizatoriPartajare()) {
                                Query cerere = utilizatori.whereEqualTo("username", username);
                                cerere.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if(task.isComplete()) {
                                            for(QueryDocumentSnapshot utilizator : task.getResult()) {
                                                DocumentReference utilizatorPartajare = utilizatori.document(utilizator.getId());
                                                utilizatorPartajare.collection("Notite").document(idNotita)
                                                        .update("continut", notitaModificata.getContinut(), "ultimaModificare", notitaModificata.getUltimaModificare());
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), ListaNotiteActivity.class);
        startActivity(intent);
    }

    private void initComponents() {
        etDenumireNotitaModificata = findViewById(R.id.etDenumireNotitaModificata);
        etContinutModificat = findViewById(R.id.etContinutModificat);
        btnSalveazaNotitaModificata = findViewById(R.id.btnSalveazaNotitaModificata);
    }

    private boolean isValid() {
        if(etContinutModificat.getText().toString().trim().isEmpty()) {
            etContinutModificat.setError("INTRODUCETI CONTINUTUL NOTITEI");
            etContinutModificat.requestFocus();
            return false;
        }

        return true;
    }

    private Notita creareNotita(List<String> listaUtilizatoriPartajare, boolean isPartajata, String proprietar) {
        String continut = etContinutModificat.getText().toString();

        return new Notita(etDenumireNotitaModificata.getText().toString(), continut, isPartajata,  listaUtilizatoriPartajare, proprietar, usernameUtilizatorCurent, new Date(System.currentTimeMillis()));
    }
}