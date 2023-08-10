package com.example.aplicatie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.aplicatie.Utilizator.Utilizator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;

public class CreareContActivity extends AppCompatActivity {

    EditText etCreareContNume, etCreareContUsername, etCreareContEmail, etCreareContParola;
    Button btnCreareCont;
    FirebaseAuth mAuth;

    String nume, username, email, parola;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference utilizatori = db.collection("Utilizatori");

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creare_cont);

        mAuth = FirebaseAuth.getInstance();

        initComponents();

        btnCreareCont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nume = String.valueOf(etCreareContNume.getText());
                username = String.valueOf(etCreareContUsername.getText());
                email = String.valueOf(etCreareContEmail.getText());
                parola = String.valueOf(etCreareContParola.getText());

                if(isValid()) {
                    verificaUnicitateUsername();
                }
            }
        });
    }

    private void verificaUnicitateUsername() {
        Query query = utilizatori.whereEqualTo("username", username);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if(querySnapshot.isEmpty()) {
                        creareCont();
                    } else {
                        Toast.makeText(CreareContActivity.this,
                                R.string.toastUsernameulDejaExista, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void creareCont() {
        mAuth.createUserWithEmailAndPassword(email, parola)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(CreareContActivity.this,
                                    R.string.toastCreareContSucces, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);
                            Utilizator utilizator = new Utilizator(nume, username, email, parola);
                            utilizatori.document(mAuth.getUid()).set(utilizator);
                        } else {
                            Toast.makeText(CreareContActivity.this,
                                    R.string.toastCreareContFailed, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void initComponents() {
        etCreareContEmail = findViewById(R.id.etCreareContEmail);
        etCreareContParola = findViewById(R.id.etCreareContParola);
        etCreareContNume = findViewById(R.id.etCreareContNume);
        etCreareContUsername = findViewById(R.id.etCreareContUsername);
        btnCreareCont = findViewById(R.id.btnContCreat);
    }

    private boolean isValid() {
        if(etCreareContNume.getText().toString().trim().isEmpty()) {
            etCreareContNume.setError("INTRODUCETI NUMELE");
            etCreareContNume.requestFocus();
            return false;
        }

        if(etCreareContUsername.getText().toString().trim().isEmpty()) {
            etCreareContUsername.setError("INTRODUCETI USERNAME");
            etCreareContUsername.requestFocus();
            return false;
        }

        if(etCreareContEmail.getText().toString().trim().isEmpty()) {
            etCreareContEmail.setError("INTRODUCETI ADRESA DE EMAIL");
            etCreareContEmail.requestFocus();
            return false;
        }

        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        etCreareContNume.setError(null);
        etCreareContEmail.setError(null);
        etCreareContUsername.setError(null);
        etCreareContParola.setError(null);
    }
}