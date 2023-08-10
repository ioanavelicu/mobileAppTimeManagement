package com.example.aplicatie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aplicatie.DateConverter.DateConverter;
import com.example.aplicatie.Sarcina.AdaptorRVSarcina;
import com.example.aplicatie.Sarcina.AdaptorRVSarcinaBif;
import com.example.aplicatie.Sarcina.Sarcina;
import com.example.aplicatie.Task.AdaptorRVTask;
import com.example.aplicatie.Task.Task;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;

public class VizualizareTaskActivity extends AppCompatActivity {
    private EditText etDenumireTaskVizualizare, etDeadlineVizualizare,
                etGradImportantaVizualizare, etCategorieVizualizare, etReminderTask;
    private TextView tvListaSarciniVizualizare, tvDeadlineVizualizare,
                tvGradImportantaVizualizare, tvCategorieVizualizare;
    private RecyclerView recyclerViewListaSarciniVizualizare, recyclerViewListaTaskuri;
    private Button btnSalveazaTask;

    private ArrayList<Sarcina> listaSarcini;
    private AdaptorRVSarcinaBif adaptorRVSarcinaBif;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference utilizatori = db.collection("Utilizatori");
    private DocumentReference docUtilizatorCurent = utilizatori.document(FirebaseAuth.getInstance().getUid());

    Intent intent;

    public Context getContext() {
        return this.getContext();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vizualizare_task);

        initComponents();

        getWindow().setSoftInputMode(WindowManager.
                LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        intent = getIntent();
        Task task = (Task) intent.getSerializableExtra(AdaptorRVTask.VIZUALIZEAZA_TASK);
        String idTask = (String) intent.getSerializableExtra(AdaptorRVTask.ID_TASK);
        if (task != null) {
            etDenumireTaskVizualizare.setText(task.getDenumireTask());

            listaSarcini = new ArrayList<>();
            listaSarcini = task.getListaSarcini();
            adaptorRVSarcinaBif = new AdaptorRVSarcinaBif(listaSarcini);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(VizualizareTaskActivity.this);
            recyclerViewListaSarciniVizualizare.setLayoutManager(layoutManager);
            recyclerViewListaSarciniVizualizare.setAdapter(adaptorRVSarcinaBif);

            if(task.getReminder() != null) {
                etReminderTask.setText(DateConverter.fromReminder(task.getReminder().getDataReminder()));
            } else {
                etReminderTask.setText(R.string.reminderTaskNestat);
            }
            etDeadlineVizualizare.setText(DateConverter.fromDate(task.getDeadline()));

            if(!task.getGradImportanta().equalsIgnoreCase("GRAD IMPORTANȚĂ")) {
                etGradImportantaVizualizare.setText(task.getGradImportanta());
            } else {
                etGradImportantaVizualizare.setText(R.string.gradImportantaTaskNesetat);
            }
            if(!task.getCategorie().equalsIgnoreCase("CATEGORIE")) {
                etCategorieVizualizare.setText(task.getCategorie());
            } else {
                etCategorieVizualizare.setText(R.string.categorieNesetataTask);
            }

            btnSalveazaTask.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<Sarcina> listaSarciniBifate = adaptorRVSarcinaBif.getListaSarcini();
                    int nrBif = nrSarciniBifate(listaSarciniBifate);

                    if(nrBif == listaSarciniBifate.size()) {
                        dialogTaskComplet(nrBif, listaSarciniBifate);
                        salveazaTask(idTask, listaSarciniBifate);
                    } else {
                        salveazaTask(idTask, listaSarciniBifate);
                        finish();
                    }

                    salveazaTask(idTask, listaSarciniBifate);
                }
            });
        }
    }

    private void salveazaTask(String idTask, ArrayList<Sarcina> listaSarciniBifate) {
        docUtilizatorCurent.collection("Taskuri")
                .document(idTask).update("listaSarcini", listaSarciniBifate)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(VizualizareTaskActivity.this,
                                R.string.toastActualizareTaskSucces, Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(VizualizareTaskActivity.this,
                                getString(R.string.toastActualizareTaskFail) + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private int nrSarciniBifate(ArrayList<Sarcina> listaSarciniBifate) {
        int nrBif = 0;
        for(Sarcina sarcina : listaSarciniBifate) {
            if(sarcina.getEsteBifata()) {
                nrBif++;
            }
        }

        return nrBif;
    }

    private void dialogTaskComplet(int nrBif, ArrayList<Sarcina> listaSarciniBifate) {
        Dialog dialog;
        dialog = new Dialog(VizualizareTaskActivity.this);
        dialog.setContentView(R.layout.dialog_task_complet);
        dialog.getWindow().setBackgroundDrawable(getApplicationContext().getDrawable(R.drawable.dialog_albastru));
        dialog.setCanceledOnTouchOutside(false);

        ImageView imgCloseTaskComplet = dialog.findViewById(R.id.imgCloseTaskComplet);
        imgCloseTaskComplet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), ListaTaskuriActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void initComponents() {
        etDenumireTaskVizualizare = findViewById(R.id.etDenumireTaskVizualizare);
        etDeadlineVizualizare = findViewById(R.id.etDeadlineVizualizare);
        etGradImportantaVizualizare = findViewById(R.id.etGradImportantaVizualizare);
        etCategorieVizualizare  = findViewById(R.id.etCategorieVizualizare);
        tvListaSarciniVizualizare = findViewById(R.id.tvListaSarciniVizualizare);
        tvDeadlineVizualizare = findViewById(R.id.tvDeadlineVizualizare);
        tvGradImportantaVizualizare = findViewById(R.id.tvGradImportantaVizualizare);
        tvCategorieVizualizare = findViewById(R.id.tvCategorieVizualizare);
        recyclerViewListaSarciniVizualizare = findViewById(R.id.recyclcerViewLayoutSarciniVizualizareTask);
        btnSalveazaTask = findViewById(R.id.btnSalveazaTask);
        etReminderTask = findViewById(R.id.etReminderTaskVizualizare);

        recyclerViewListaTaskuri = findViewById(R.id.recycleViewListaTaskuri);
    }
}