package com.example.aplicatie;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aplicatie.Utilizator.Utilizator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class FragmentProfil extends Fragment {
    private static final int COD_POZA = 1;
    public static final String USERNAME = "USERNAME";

    private ImageView imgProfil;
    private TextView tvUsername;
    private FloatingActionButton fabSchimbaPozaProfil;
    private Button btnTrimiteCerereDePritenie, btnListaCereriPrietenie, btnListaPrieteni,
            btnStatistici;

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storagePozeProfi = storage.getReference().child("imaginiProfil");

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference utilizatori = db.collection("Utilizatori");
    private DocumentReference docUtilizatorCurent = utilizatori.document(FirebaseAuth.getInstance().getUid());

    private String usernameCurent;

    public FragmentProfil() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profil, container, false);

        initComponents(view);
        updateazaImagine();

        docUtilizatorCurent.get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                Utilizator utilizator = task.getResult().toObject(Utilizator.class);
                                if (utilizator != null) {
                                    usernameCurent = utilizator.getUsername();
                                    tvUsername.setText(String.format("Bună ziua,\n %s!", utilizator.getNume()));

                                    btnStatistici.setEnabled(true);
                                    btnTrimiteCerereDePritenie.setEnabled(true);
                                    btnListaCereriPrietenie.setEnabled(true);
                                    btnListaPrieteni.setEnabled(true);
                                    fabSchimbaPozaProfil.setEnabled(true);
                                }
                            }
                        });

        fabSchimbaPozaProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                schimbaPozaProfil();
            }
        });


        btnTrimiteCerereDePritenie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AdaugaPrietenActivity.class);
                intent.putExtra(USERNAME, usernameCurent);
                startActivity(intent);
            }
        });

        btnListaCereriPrietenie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ListaCereriPrietenieActivity.class);
                intent.putExtra(USERNAME, usernameCurent);
                startActivity(intent);
            }
        });

        btnListaPrieteni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ListaPrieteniActivity.class);
                intent.putExtra(USERNAME, usernameCurent);
                startActivity(intent);
            }
        });

        btnStatistici.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), StatisticiActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    private void schimbaPozaProfil() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, COD_POZA);
    }

    private void initComponents(View view) {
        imgProfil = view.findViewById(R.id.imgProfil);
        tvUsername = view.findViewById(R.id.tvUsername);
        fabSchimbaPozaProfil = view.findViewById(R.id.fabSchimbaPozaProfil);
//        btnDelogare = view.findViewById(R.id.btnDelogare);
        btnTrimiteCerereDePritenie = view.findViewById(R.id.btnTrimiteCererePrietenie);
        btnListaCereriPrietenie = view.findViewById(R.id.btnListaCereriPrietenie);
        btnListaPrieteni = view.findViewById(R.id.btnListaPrieteni);
        btnStatistici = view.findViewById(R.id.btnStatistici);

        btnStatistici.setEnabled(false);
        btnTrimiteCerereDePritenie.setEnabled(false);
        btnListaCereriPrietenie.setEnabled(false);
        btnListaPrieteni.setEnabled(false);
        fabSchimbaPozaProfil.setEnabled(false);
    }

    private void adaugaImagineInBazaDeDate(String uriImagine) {
        docUtilizatorCurent.update("pozaProfil", uriImagine);
        updateazaImagine();
    }

    private void updateazaImagine() {
        docUtilizatorCurent.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Utilizator utilizator = documentSnapshot.toObject(Utilizator.class);
                        Uri uriImagine = Uri.parse(utilizator.getPozaProfil());
                        Picasso.get()
                                .load(uriImagine)
                                .into(imgProfil);
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == COD_POZA && resultCode == RESULT_OK) {
            Uri uriImAagine = data.getData();
            if(uriImAagine != null) {
                StorageReference pozaProfil = storagePozeProfi.child(uriImAagine.getLastPathSegment());
                pozaProfil.putFile(uriImAagine)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                pozaProfil.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String uriImagine = uri.toString();
                                        adaugaImagineInBazaDeDate(uriImagine);
                                    }
                                });
                            }
                        });
            }
        }
    }
}