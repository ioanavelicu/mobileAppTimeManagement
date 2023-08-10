package com.example.aplicatie;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.aplicatie.Utilizator.Utilizator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;


public class FragmentSetari extends Fragment {
    private Button btnDelogare, btnSchimbaUsername, btnSchimbaParola, btnSchimbaEmail;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference utilizatori = db.collection("Utilizatori");
    private DocumentReference docUtilizatorCurent = utilizatori.document(FirebaseAuth.getInstance().getUid());

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public FragmentSetari() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setari, container, false);

        initComponents(view);

        btnDelogare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        btnSchimbaUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                schimbaUsernameDialog();
            }
        });

        btnSchimbaParola.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                schimbaParolaDialog();
            }
        });

        btnSchimbaEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                schimbaEmailDialog();
            }
        });

        return view;
    }

    private void schimbaEmailDialog(){
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_schimba_email);
        dialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.dialog_albastru));
        dialog.setCanceledOnTouchOutside(false);

        Button btnSalveazaEmail, btnInchideEmail;
        EditText etEmailCurent, etSchimbaEmail;

        btnInchideEmail = dialog.findViewById(R.id.btnInchideEmail);
        btnSalveazaEmail =dialog.findViewById(R.id.btnSalveazaEmail);
        etEmailCurent = dialog.findViewById(R.id.etEmailCurent);
        etSchimbaEmail = dialog.findViewById(R.id.etSchimbaEmail);

        docUtilizatorCurent.get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                Utilizator u = task.getResult().toObject(Utilizator.class);
                                String emailCurent = u.getEmail();
                                String parola = u.getParola();

                                etEmailCurent.setText(emailCurent);

                                btnSalveazaEmail.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String emailNou = etSchimbaEmail.getText().toString();
                                        if(emailNou.isEmpty()) {
                                            etSchimbaEmail.requestFocus();
                                            etSchimbaEmail.setError("Introduceți o nouă adresă de e-mail");
                                        } else if(emailNou.equals(emailCurent)) {
                                            etSchimbaEmail.requestFocus();
                                            etSchimbaEmail.setError("Adresa nouă de e-mail nu poate fi aceeși cu cea veche");
                                        } else {
                                            schimbaEmail(dialog, parola, emailCurent, emailNou);
                                        }
                                    }
                                });
                            }
                        });

        btnInchideEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void schimbaEmail(Dialog dialog, String parola, String emailCurent, String emailNou) {
        FirebaseUser utilizator = mAuth.getCurrentUser();
        AuthCredential credential = EmailAuthProvider.getCredential(utilizator.getEmail(), parola);
        utilizator.reauthenticate(credential).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                utilizator.updateEmail(emailNou).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getContext(), "Adresă de e-mail schimbată cu succes", Toast.LENGTH_SHORT).show();
                        docUtilizatorCurent.update("email", emailNou);
                        dialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
            }
        });
    }

    private void schimbaParolaDialog() {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_schimba_parola);
        dialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.dialog_albastru));
        dialog.setCanceledOnTouchOutside(false);

        Button btnSalveazaParola, btnInchideParola;
        EditText etParolaCurenta, etSchimbaParola;

        btnSalveazaParola = dialog.findViewById(R.id.btnSalveazaParola);
        btnInchideParola = dialog.findViewById(R.id.btnInchideParola);
        etParolaCurenta = dialog.findViewById(R.id.etParolaCurenta);
        etSchimbaParola = dialog.findViewById(R.id.etSchimbaParola);

        docUtilizatorCurent.get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                btnSalveazaParola.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(task.getResult().toObject(Utilizator.class).getParola().equals(etParolaCurenta.getText().toString())) {
                                            if(etParolaCurenta.getText().toString().equals(etSchimbaParola.getText().toString())) {
                                                Toast.makeText(getContext(), "Parola este aceeși", Toast.LENGTH_SHORT).show();
                                                etSchimbaParola.setText("");
                                            } else {
                                                String parolaVeche = etParolaCurenta.getText().toString();
                                                String parolaNoua = etSchimbaParola.getText().toString();
                                                schimbaParola(dialog, parolaVeche, parolaNoua);
                                            }
                                        } else {
                                            Toast.makeText(getContext(), "Parola introdusă nu este corectă", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });

        btnInchideParola.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        dialog.show();
    }

    private void schimbaParola(Dialog dialog, String parolaVeche, String parolaNoua) {
        FirebaseUser utilizator = mAuth.getCurrentUser();
        AuthCredential authCredential = EmailAuthProvider.getCredential(utilizator.getEmail(), parolaVeche);
        utilizator.reauthenticate(authCredential)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        utilizator.updatePassword(parolaNoua)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(getContext(), "Parolă schimbată cu succes", Toast.LENGTH_SHORT).show();
                                        docUtilizatorCurent.update("parola", parolaNoua);
                                        dialog.dismiss();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
    }

    private void schimbaUsernameDialog() {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_schimba_username);
        dialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.dialog_albastru));
        dialog.setCanceledOnTouchOutside(false);

        Button btnSalveazaUsername, btnIchideUsername;
        EditText etUsernameCurent, etSchimbaUsername;

        btnSalveazaUsername = dialog.findViewById(R.id.btnSalveazaUsername);
        btnIchideUsername = dialog.findViewById(R.id.btnInchideUsername);
        etUsernameCurent = dialog.findViewById(R.id.etUsernameCurent);
        etSchimbaUsername = dialog.findViewById(R.id.etSchimbaUsername);

        docUtilizatorCurent.get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                etUsernameCurent.setEnabled(false);
                                etUsernameCurent.setText(Objects.requireNonNull(task.getResult().toObject(Utilizator.class)).getUsername());

                                btnSalveazaUsername.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String usernameNou = etSchimbaUsername.getText().toString();
                                        verficareUnicitateUsername(usernameNou, dialog);
                                        utilizatori.whereArrayContains("listaPrieteni", etUsernameCurent.getText().toString())
                                                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                        for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                                            documentSnapshot.getReference().update("listaPrieteni", FieldValue.arrayRemove(etUsernameCurent.getText().toString()));
                                                            documentSnapshot.getReference().update("listaPrieteni", FieldValue.arrayUnion(usernameNou));
                                                        }
                                                    }
                                                });

                                        utilizatori.whereArrayContains("listaCereriDePrietenieTrimise", etUsernameCurent.getText().toString())
                                                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                        for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                                            documentSnapshot.getReference().update("listaCereriDePrietenieTrimise", FieldValue.arrayRemove(etUsernameCurent.getText().toString()));
                                                            documentSnapshot.getReference().update("listaCereriDePrietenieTrimise", FieldValue.arrayUnion(usernameNou));
                                                        }
                                                    }
                                                });
                                    }
                                });
                            }
                        });

        btnIchideUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        dialog.show();
    }

    private void verficareUnicitateUsername(String username, Dialog dialog) {
        Query query = utilizatori.whereEqualTo("username", username);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if(querySnapshot.isEmpty()) {
                        docUtilizatorCurent.update("username", username);
                        Toast.makeText(getContext(), "Username modificat cu succes",
                                Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(getContext(),
                                R.string.toastUsernameulDejaExista, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    private void initComponents(View view) {
        btnDelogare = view.findViewById(R.id.btnDelogare);
        btnSchimbaUsername = view.findViewById(R.id.btnSchimbaUsername);
        btnSchimbaParola = view.findViewById(R.id.btnSchimbaParola);
        btnSchimbaEmail = view.findViewById(R.id.btnSchimbaEmail);
    }
}