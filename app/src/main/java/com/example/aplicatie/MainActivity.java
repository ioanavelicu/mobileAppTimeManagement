package com.example.aplicatie;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentResultListener;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.example.aplicatie.Jurnal.JurnalDialog;
import com.example.aplicatie.Jurnal.JurnalDialogParolaNoua;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements JurnalDialog.JurnalDialogListener, JurnalDialogParolaNoua.JurnalDialogParolaNouaListener {
    FirebaseAuth auth;
    FirebaseUser user;

    private BottomNavigationView bottomMeniu;
    private FragmentHome fragmentHome = new FragmentHome();
    private FragmentSetari fragmentSetari = new FragmentSetari();
    private FragmentProfil fragmentProfil = new FragmentProfil();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference utilizatori = db.collection("Utilizatori");
    private DocumentReference docUtilizatorCurent = utilizatori.document(FirebaseAuth.getInstance().getUid());

    String parolaJurnal;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initComponents();

        intent = getIntent();
        getSupportFragmentManager().setFragmentResultListener(FragmentHome.KEY, this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                parolaJurnal = bundle.getString(FragmentHome.JURNAL);
            }
        });

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentCurent, fragmentHome).commit();

        bottomMeniu.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.btnHome:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragmentCurent, fragmentHome)
                                .commit();
                        return true;
                    case R.id.btnProfil:
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.fragmentCurent, fragmentProfil)
                                    .commit();
                        return true;
                    case R.id.btnSetari:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragmentCurent, fragmentSetari)
                                .commit();
                        return true;
                }

                return false;
            }
        });
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if(user == null) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }
    }

    private void initComponents() {
        bottomMeniu = findViewById(R.id.bottomMeniu);
    }

    @Override
    public void applyTexts(String parola) {
        if(parola.equals(parolaJurnal)) {
            Intent intent = new Intent(getApplicationContext(), JurnalActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "PAROLA INCORECTA", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void adaugaParolaJurnal(String parola) {
        docUtilizatorCurent.update("parolaJurnal", parola);
        Intent intent = new Intent(getApplicationContext(), JurnalActivity.class);
        startActivity(intent);
    }
}