package com.example.aplicatie;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.text.HtmlCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spanned;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.aplicatie.DateConverter.DateConverter;
import com.example.aplicatie.Utilizator.Utilizator;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class JurnalActivity extends AppCompatActivity {
    private EditText etContinutJurnal;
    private FloatingActionButton btnAdaugaInJurnal, btnSalveazaJurnal;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference utilizatori = db.collection("Utilizatori");
    private DocumentReference docUtilizatorCurent = utilizatori.document(FirebaseAuth.getInstance().getUid());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jurnal);

        etContinutJurnal = findViewById(R.id.etContinutJurnal);
        btnAdaugaInJurnal = findViewById(R.id.btnAdaugaInJurnal);
        btnSalveazaJurnal = findViewById(R.id.btnSalveazaJurnal);

        docUtilizatorCurent.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Utilizator utilizator = documentSnapshot.toObject(Utilizator.class);
                assert utilizator != null;
                if(utilizator.getCotinutJurnal() == null || utilizator.getCotinutJurnal().isEmpty()){
                    etContinutJurnal.setText("Apăsți pe plus pentru a scrie în jurnal");
                } else {
                    etContinutJurnal.setText(utilizator.getCotinutJurnal());
                }
            }
        });

        btnSalveazaJurnal.setVisibility(View.GONE);
        etContinutJurnal.setEnabled(false);
        getWindow().setSoftInputMode(WindowManager.
                LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        btnAdaugaInJurnal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etContinutJurnal.setEnabled(true);
                if(etContinutJurnal.getText().toString().equalsIgnoreCase("Apăsți pe plus pentru a scrie în jurnal")) {
                    etContinutJurnal.setText("");
                }
                etContinutJurnal.requestFocus();
                InputMethodManager imm = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(etContinutJurnal, InputMethodManager.SHOW_IMPLICIT);

                btnSalveazaJurnal.setVisibility(View.VISIBLE);
                btnAdaugaInJurnal.setVisibility(View.GONE);
            }
        });

        btnSalveazaJurnal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date data = new Date(System.currentTimeMillis());
                String dataCurenta = DateConverter.fromReminder(data);
                String continut = etContinutJurnal.getText().toString();
                String textJurnal = "\n\n" + dataCurenta + "\n" + continut;
                etContinutJurnal.setText(textJurnal);
                etContinutJurnal.setEnabled(false);

                btnSalveazaJurnal.setVisibility(View.GONE);
                btnAdaugaInJurnal.setVisibility(View.VISIBLE);

                getWindow().setSoftInputMode(WindowManager.
                        LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                docUtilizatorCurent.update("cotinutJurnal", "");
                docUtilizatorCurent.update("cotinutJurnal", textJurnal);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}