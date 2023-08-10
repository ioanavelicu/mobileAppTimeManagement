package com.example.aplicatie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.aplicatie.Categorie.Categorie;
import com.example.aplicatie.DateConverter.DateConverter;
import com.example.aplicatie.Eveniment.Eveniment;
import com.example.aplicatie.Notificare.NotificareTask;
import com.example.aplicatie.Reminder.Reminder;
import com.example.aplicatie.Task.DeadlineTaskDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

public class AddEvenimentActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{
    public static final String EVENIMENT = "Eveniment";
    public static final String MESAJ_REMINDER = "mesaj reminder";
    public static final String DATA_REMINDER = "data reminder";

    private EditText etNumeEveniment, etDataEveniment, etReminderEveniment, etDescriereEveniment;
    private Spinner spinnerCategorieEveniment;
    private Button btnAlegeDataEveniment, btnSeteazaReminderEveniment, btnAdaugaCategorie, btnSalveazaEvenimentNou;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference utilizatori = db.collection("Utilizatori");
    private DocumentReference docUtilizatorCurent = utilizatori.document(FirebaseAuth.getInstance().getUid());

    private Dialog dialog;
    private Reminder reminder;
    private String categorieNoua;
    private String stringReminder;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_eveniment);

        initComponents();

        String[] listaCategoriiBasic = getResources().getStringArray(R.array.spinnerCategorieEveniment);
        docUtilizatorCurent.collection("CategoriiEveniment").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<QuerySnapshot> task) {
                        int nrCategoriiBD = task.getResult().getDocuments().size();
                        if(nrCategoriiBD > 0) {
                            docUtilizatorCurent.collection("CategoriiEveniment").get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull com.google.android.gms.tasks.Task<QuerySnapshot> task) {
                                            String[] listaCategoriiBD = new String[nrCategoriiBD];
                                            int i = 0;
                                            for(Categorie categorie : task.getResult().toObjects(Categorie.class)) {
                                                listaCategoriiBD[i++] = categorie.getDenumire();
                                            }

                                            String[] listaNoua = Arrays.copyOf(listaCategoriiBasic, listaCategoriiBD.length + listaCategoriiBasic.length - 1);
                                            for(int j = 0; j < listaCategoriiBD.length; j++) {
                                                listaNoua[listaCategoriiBasic.length + j - 1] = listaCategoriiBD[j];
                                            }

                                            ArrayAdapter<String> adaptorSpinnerCategorieEveniment = new ArrayAdapter<String>(AddEvenimentActivity.this,
                                                    android.R.layout.simple_spinner_item, listaNoua);
                                            adaptorSpinnerCategorieEveniment.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                            spinnerCategorieEveniment.setAdapter(adaptorSpinnerCategorieEveniment);
                                        }
                                    });
                        } else {
                            ArrayAdapter<String> adaptorSpinnerCategorieEveniment = new ArrayAdapter<String>(AddEvenimentActivity.this,
                                    android.R.layout.simple_spinner_item, listaCategoriiBasic);
                            adaptorSpinnerCategorieEveniment.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerCategorieEveniment.setAdapter(adaptorSpinnerCategorieEveniment);
                        }
                    }
                });

        btnAlegeDataEveniment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DeadlineTaskDialog();
                datePicker.show(getSupportFragmentManager(), "");
            }
        });

        btnSeteazaReminderEveniment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adaugaReminder();
            }
        });

        btnAdaugaCategorie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(AddEvenimentActivity.this);
                dialog.setContentView(R.layout.dialog_categorie_noua);
                dialog.getWindow().setBackgroundDrawable(getApplicationContext().getDrawable(R.drawable.dialog_albastru));
                dialog.setCanceledOnTouchOutside(false);

                Button btnSalveazaCategorie, btnInchideCategorie;
                EditText etDenumireCategorie;

                btnSalveazaCategorie = dialog.findViewById(R.id.btnSalveazaCategorie);
                btnInchideCategorie = dialog.findViewById(R.id.btnInchideCategorie);
                etDenumireCategorie = dialog.findViewById(R.id.etDenumireCategorie);

                btnSalveazaCategorie.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String[] listaCategorii = getResources().getStringArray(R.array.spinnerCategorieEveniment);
                        String[] listaNoua = Arrays.copyOf(listaCategorii, listaCategorii.length + 1);
                        categorieNoua = etDenumireCategorie.getText().toString();
                        listaNoua[listaNoua.length - 1] = categorieNoua;

                        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(AddEvenimentActivity.this,
                                android.R.layout.simple_spinner_item, listaNoua);
                        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerCategorieEveniment.setAdapter(spinnerArrayAdapter);

                        if(categorieNoua != null || !categorieNoua.trim().isEmpty()) {
                            Categorie categorie = new Categorie(categorieNoua);
                            docUtilizatorCurent.collection("CategoriiEveniment").add(categorie);
                        }

                        dialog.dismiss();
                    }
                });

                btnInchideCategorie.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });

                dialog.show();
            }
        });

        intent = getIntent();
        btnSalveazaEvenimentNou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValid()) {
                    Eveniment eveniment = creareEveniment();
                    if(eveniment != null) {
                        docUtilizatorCurent.collection("Evenimente").add(eveniment)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Toast.makeText(AddEvenimentActivity.this, "EVENIMENT ADAUGAT CU SUCCES", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(AddEvenimentActivity.this, "EROARE LA ADAUGARE EVENIMENT" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
            }
        });
    }

    private void adaugaReminder() {
        dialog = new Dialog(AddEvenimentActivity.this);
        dialog.setContentView(R.layout.dialog_seteaza_reminder);
        dialog.getWindow().setBackgroundDrawable(getApplicationContext().getDrawable(R.drawable.dialog_albastru));
        dialog.setCanceledOnTouchOutside(false);

        final EditText etDataReminderTask;
        Button btnAlegeDataSiOraReminderTask, btnSalveazaReminderTask, btnInchideReminderTask;

        etDataReminderTask = dialog.findViewById(R.id.etDataReminderTask);
        btnAlegeDataSiOraReminderTask = dialog.findViewById(R.id.btnAlegeDataSiOraReminderTask);
        btnSalveazaReminderTask = dialog.findViewById(R.id.btnSalveazaReminder);
        btnInchideReminderTask = dialog.findViewById(R.id.btnInchideReminder);

        final Calendar calendar = Calendar.getInstance();
        btnAlegeDataSiOraReminderTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddEvenimentActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        final Calendar data = Calendar.getInstance();
                        Calendar ora = Calendar.getInstance();
                        TimePickerDialog timePickerDialog = new TimePickerDialog(AddEvenimentActivity.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                data.set(year, month, dayOfMonth, hourOfDay, minute, 0);
                                Calendar momCurent = Calendar.getInstance();
                                if(data.getTimeInMillis() - momCurent.getTimeInMillis() > 0) {
                                    etDataReminderTask.setText(DateConverter.fromReminder(data.getTime()));
                                    stringReminder = data.getTime().toString().trim();
                                } else {
                                    Toast.makeText(AddEvenimentActivity.this, "INVALID", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, ora.get(Calendar.HOUR_OF_DAY), ora.get(Calendar.MINUTE), true);
                        timePickerDialog.show();
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });

        btnSalveazaReminderTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reminder = new Reminder();
                reminder.setMesaj("Nu uita de evenimentul " + etNumeEveniment.getText());
                Date dataReminder = new Date(stringReminder);
                reminder.setDataReminder(dataReminder);
                reminder.setId(new Random().nextInt());

                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+2:00"));
                calendar.setTime(dataReminder);
                calendar.set(Calendar.SECOND, 0);
                Intent intent = new Intent(AddEvenimentActivity.this, NotificareTask.class);
                intent.putExtra(MESAJ_REMINDER, reminder.getMesaj());
                intent.putExtra(DATA_REMINDER, reminder.getDataReminder());

                PendingIntent pendingIntent = PendingIntent.getBroadcast(AddEvenimentActivity.this, reminder.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

                etReminderEveniment.setText(DateConverter.fromReminder(calendar.getTime()));
                dialog.dismiss();
            }
        });

        btnInchideReminderTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        dialog.show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        String dataSelectata = DateConverter.fromDate(calendar.getTime());
        etDataEveniment.setText(dataSelectata);
    }

    private void initComponents() {
        etNumeEveniment = findViewById(R.id.etNumeEveniment);
        etDescriereEveniment = findViewById(R.id.etDescriereEveneiment);
        btnAlegeDataEveniment = findViewById(R.id.btnAlegeDataEveniment);
        etDataEveniment = findViewById(R.id.etDataEveniment);
        btnSeteazaReminderEveniment = findViewById(R.id.btnSeteazaReminderEveniment);
        etReminderEveniment = findViewById(R.id.etReminderEveniment);
        spinnerCategorieEveniment = findViewById(R.id.spinnerCategorieEveniment);
        btnAdaugaCategorie = findViewById(R.id.btnAdaugaCategorieEveniment);
        btnSalveazaEvenimentNou = findViewById(R.id.btnSalveazaEvenimentNou);
    }

    private Eveniment creareEveniment() {
        String denumireEveniment = etNumeEveniment.getText().toString();
        Date dataEveniment = DateConverter.fromString(etDataEveniment.getText().toString());
        String descriereEveniment = etDescriereEveniment.getText().toString();
        String categorieEveniment = spinnerCategorieEveniment.getSelectedItem().toString();

        return new Eveniment(denumireEveniment, dataEveniment, descriereEveniment, categorieEveniment, reminder);
    }

    private boolean isValid() {
        if(etNumeEveniment.getText().toString().trim().isEmpty()) {
            etNumeEveniment.setError("INTRODUCETI DENUMIRE");
            etNumeEveniment.requestFocus();
            return false;
        }

        if(etDataEveniment.getText().toString().trim().isEmpty()) {
            etDataEveniment.setError("INTRODUCETI DEADLINE");
            etDataEveniment.requestFocus();
            return false;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        if ((DateConverter.fromString(etDataEveniment.getText().toString())).before(calendar.getTime())) {
            etDataEveniment.setError("INTRODUCETI O DATA VALIDA");
            btnAlegeDataEveniment.requestFocus();
            return false;
        }

        if(etDescriereEveniment.getText().toString().trim().isEmpty()) {
            etDescriereEveniment.setError("INTRODUCETI O DESCRIERE");
            etDescriereEveniment.requestFocus();
            return false;
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        etNumeEveniment.setError(null);
        etDataEveniment.setError(null);
        etDescriereEveniment.setError(null);
    }
}