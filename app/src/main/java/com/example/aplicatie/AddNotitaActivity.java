package com.example.aplicatie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

import java.util.Date;

public class AddNotitaActivity extends AppCompatActivity {
    private EditText etDenumireNotita, etContinut;
    private Button btnSalveazaNotitaNoua;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference utilizatori = db.collection("Utilizatori");
    private DocumentReference docUtilizatorCurent = utilizatori.document(FirebaseAuth.getInstance().getUid());

    private String idNotita;

    private String usernameUtilizatorCurent = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notita);

        initComponents();

        docUtilizatorCurent.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                usernameUtilizatorCurent = task.getResult().toObject(Utilizator.class).getUsername();
            }
        });

        btnSalveazaNotitaNoua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValid()) {
                    Notita notita = creareNotita();
                    if(notita != null) {
                        docUtilizatorCurent.collection("Notite").add(notita)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Toast.makeText(AddNotitaActivity.this, "NOTITA ADAUGATA CU SUCCES", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(AddNotitaActivity.this, "EROARE LA ADAUGARE NOTITA" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), ListaNotiteActivity.class);
        startActivity(intent);
    }

    private void initComponents() {
        etDenumireNotita = findViewById(R.id.etDenumireNotita);
        etContinut = findViewById(R.id.etContinut);
        btnSalveazaNotitaNoua = findViewById(R.id.btnSalveazaNotitaNoua);
    }

    private Notita creareNotita() {
        String denumireNotita = etDenumireNotita.getText().toString();
        String continut = etContinut.getText().toString().toLowerCase();

        return new Notita(denumireNotita, continut, false, null, usernameUtilizatorCurent," " , new Date(System.currentTimeMillis()));
    }

    private boolean isValid() {
        if(etDenumireNotita.getText().toString().trim().isEmpty()) {
            etDenumireNotita.setError("INTRODUCETI DENUMIREA");
            etDenumireNotita.requestFocus();
            return false;
        }

        if(etContinut.getText().toString().trim().isEmpty()) {
            etContinut.setError("INTRODUCETI CONTINUTUL NOTITEI");
            etContinut.requestFocus();
            return false;
        }

        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        etDenumireNotita.setError(null);
        etContinut.setError(null);
    }
}