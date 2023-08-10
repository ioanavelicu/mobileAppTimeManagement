package com.example.aplicatie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.collection.LLRBNode;

public class LoginActivity extends AppCompatActivity {

    EditText etLoginEmail, etLoginParola;
    TextInputLayout tilLoginEmail, tilLoginParola;
    Button btnLogin, btnInregistrateCont;
    FirebaseAuth mAuth;

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
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        initComponents();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email, parola;
                email = String.valueOf(etLoginEmail.getText());
                parola = String.valueOf(etLoginParola.getText());

                if(isValid()) {
                    autentificare(email, parola);
                }
            }
        });

        btnInregistrateCont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CreareContActivity.class);
                startActivity(intent);
            }
        });
    }

    private void autentificare(String email, String parola) {
        mAuth.signInWithEmailAndPassword(email, parola)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this,
                                    R.string.toastConectareSucces, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(LoginActivity.this,
                                    R.string.toastConectareFailed,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void initComponents() {
        etLoginEmail = findViewById(R.id.etLoginEmail);
        etLoginParola = findViewById(R.id.etLoginParola);
        btnLogin = findViewById(R.id.btnLogin);
        btnInregistrateCont = findViewById(R.id.btnInregistrare);
        tilLoginEmail = findViewById(R.id.tilLoginEmail);
        tilLoginParola = findViewById(R.id.tilLoginParola);
    }

    private boolean isValid() {
        if(etLoginEmail.getText().toString().trim().isEmpty()) {
            etLoginEmail.setError("INTRODUCETI ADRESA DE EMAIL");
            etLoginEmail.requestFocus();
            tilLoginEmail.setHelperTextEnabled(true);
            tilLoginEmail.setHelperText("Obligatori*");
            tilLoginEmail.setHelperTextColor(ColorStateList.valueOf(Color.RED));
            return false;
        }

        if(etLoginParola.getText().toString().trim().isEmpty()) {
            etLoginParola.setError("INTRODUCETI PAROLA");
            etLoginParola.requestFocus();
            tilLoginParola.setHelperTextEnabled(true);
            tilLoginParola.setHelperText("Obligatori*");
            tilLoginParola.setHelperTextColor(ColorStateList.valueOf(Color.RED));
            return false;
        }
        return true;
    }
}