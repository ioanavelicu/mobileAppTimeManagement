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
import com.example.aplicatie.Eveniment.AdaptorRVEveniment;
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

public class ModificaEvenimentActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private EditText etDenumireEvenimentModificat, etDataEvenimentModificat,
            etReminderEvenimentModificat, etDescriereEvenimentModificat;
    private Button btnSeteazaDataModificata, btnAdaugaCategorieEvenimentModificat,
            btnSeteazaReminderEvenimentModificat, btnSalveazaEvenimentModificat;
    private TextView tvDescriereEvenimentModificat;
    private Spinner spinnerCategorieEveneimentModificat;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference utilizatori = db.collection("Utilizatori");
    private DocumentReference docUtilizatorCurent = utilizatori.document(FirebaseAuth.getInstance().getUid());

    public static String MESAJ_REMINDER = "mesaj reminder";
    public static String DATA_REMINDER = "data reminder";

    private String categorieNoua;
    private String stringReminder;
    private Reminder reminder;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifica_eveniment);

        initComponents();

        btnSeteazaDataModificata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DeadlineTaskDialog();
                datePicker.show(getSupportFragmentManager(), "");
            }
        });

        intent = getIntent();
        Eveniment eveniment = (Eveniment) intent.getSerializableExtra(AdaptorRVEveniment.MODIFICA_EVENIMENT);
        String idEveniment = (String) intent.getSerializableExtra(AdaptorRVEveniment.ID_EVENIMENT);

        btnSeteazaReminderEvenimentModificat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adaugaReminder(eveniment);
            }
        });

        if(eveniment != null) {

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

                                                ArrayAdapter<String> adaptorSpinnerCategorieEveniment = new ArrayAdapter<String>(ModificaEvenimentActivity.this,
                                                        android.R.layout.simple_spinner_item, listaNoua);
                                                adaptorSpinnerCategorieEveniment.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                                spinnerCategorieEveneimentModificat.setAdapter(adaptorSpinnerCategorieEveniment);
                                                int pozCat = adaptorSpinnerCategorieEveniment.getPosition(eveniment.getCategorieEveneiment());
                                                spinnerCategorieEveneimentModificat.setSelection(pozCat);
                                            }
                                        });
                            } else {
                                ArrayAdapter<String> adaptorSpinnerCategorieEveniment = new ArrayAdapter<String>(ModificaEvenimentActivity.this,
                                        android.R.layout.simple_spinner_item, listaCategoriiBasic);
                                adaptorSpinnerCategorieEveniment.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinnerCategorieEveneimentModificat.setAdapter(adaptorSpinnerCategorieEveniment);
                                int pozCat = adaptorSpinnerCategorieEveniment.getPosition(eveniment.getCategorieEveneiment());
                                spinnerCategorieEveneimentModificat.setSelection(pozCat);
                            }
                        }
                    });

            btnAdaugaCategorieEvenimentModificat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dialog dialog = new Dialog(ModificaEvenimentActivity.this);
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
                            String[] listaCategorii = getResources().getStringArray(R.array.spinnerCategorieTask);
                            String[] listaNoua = Arrays.copyOf(listaCategorii, listaCategorii.length + 1);
                            categorieNoua = etDenumireCategorie.getText().toString();
                            listaNoua[listaNoua.length - 1] = categorieNoua;

                            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(ModificaEvenimentActivity.this,
                                    android.R.layout.simple_spinner_item, listaNoua);
                            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerCategorieEveneimentModificat.setAdapter(spinnerArrayAdapter);

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


            if(eveniment.getReminder() != null) {
                etReminderEvenimentModificat.setText(eveniment.getReminder().getDataReminder().toString());
            }
            etDenumireEvenimentModificat.setText(eveniment.getDenumireEveniment());
            etDataEvenimentModificat.setText(DateConverter.fromDate(eveniment.getDataEveniment()));
            etDescriereEvenimentModificat.setText(eveniment.getDescriereEveniment());

            btnSalveazaEvenimentModificat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isValid()) {
                        Eveniment evenimentModificat = creareEveniment(eveniment);
                        if(evenimentModificat != null) {
                            docUtilizatorCurent.collection("Evenimente")
                                    .document(idEveniment).set(evenimentModificat)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(ModificaEvenimentActivity.this, "EVENIMENT MODIFICAT CU SUCCES", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(ModificaEvenimentActivity.this, "EROARE LA MODIFICARE EVENIMENT" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                }
            });
        }
    }

    private void adaugaReminder(Eveniment eveniment) {
        Dialog dialog = new Dialog(ModificaEvenimentActivity.this);
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
                DatePickerDialog datePickerDialog = new DatePickerDialog(ModificaEvenimentActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        final Calendar data = Calendar.getInstance();
                        Calendar ora = Calendar.getInstance();
                        TimePickerDialog timePickerDialog = new TimePickerDialog(ModificaEvenimentActivity.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                data.set(year, month, dayOfMonth, hourOfDay, minute, 0);
                                Calendar momCurent = Calendar.getInstance();
                                if(data.getTimeInMillis() - momCurent.getTimeInMillis() > 0) {
                                    etDataReminderTask.setText(DateConverter.fromReminder(data.getTime()));
                                    stringReminder = data.getTime().toString().trim();
                                } else {
                                    Toast.makeText(ModificaEvenimentActivity.this, "INVALID", Toast.LENGTH_SHORT).show();
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
                reminder = eveniment.getReminder();
                if(reminder == null) {
                    reminder = new Reminder();
                    reminder.setId(new Random().nextInt());
                }
                reminder.setMesaj("Nu uita de evenimentul " + etDenumireEvenimentModificat.getText());
                Date dataReminder = new Date(stringReminder);
                reminder.setDataReminder(dataReminder);

                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+2:00"));
                calendar.setTime(dataReminder);
                calendar.set(Calendar.SECOND, 0);
                Intent intent = new Intent(ModificaEvenimentActivity.this, NotificareTask.class);
                intent.putExtra(MESAJ_REMINDER, reminder.getMesaj());
                intent.putExtra(DATA_REMINDER, reminder.getDataReminder());

                PendingIntent pendingIntent = PendingIntent.getBroadcast(ModificaEvenimentActivity.this, reminder.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                alarmManager.cancel(pendingIntent);

                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

                etReminderEvenimentModificat.setText(DateConverter.fromReminder(calendar.getTime()));
                dialog.dismiss();
            }
        });

        btnInchideReminderTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), ListaEvenimenteActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void initComponents() {
        etDenumireEvenimentModificat = findViewById(R.id.etDenumireEvenimentModificat);
        etDataEvenimentModificat = findViewById(R.id.etDataEvenimentModificat);
        etDescriereEvenimentModificat = findViewById(R.id.etDescriereEveneimentModificat);
        etReminderEvenimentModificat = findViewById(R.id.etReminderEvenimentModificat);
        btnSeteazaReminderEvenimentModificat = findViewById(R.id.btnSeteazaReminderEvenimentModificat);
        btnSeteazaDataModificata = findViewById(R.id.btnSeteazaDataModificat);
        btnAdaugaCategorieEvenimentModificat = findViewById(R.id.btnAdaugaCategorieEvenimentModificat);
        btnSalveazaEvenimentModificat = findViewById(R.id.btnSalveazaEvenimentModificat);
        tvDescriereEvenimentModificat = findViewById(R.id.tvDescriereEvenimentModificat);
        spinnerCategorieEveneimentModificat = findViewById(R.id.spinnerCategorieEvenimentModificat);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        String dataSelectata = DateConverter.fromDate(calendar.getTime());
        etDataEvenimentModificat.setText(dataSelectata);
    }

    private boolean isValid() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        if ((DateConverter.fromString(etDataEvenimentModificat.getText().toString())).before(calendar.getTime())) {
            etDataEvenimentModificat.setError("INTRODUCETI O DATA VALIDA");
            btnSeteazaDataModificata.requestFocus();
            return false;
        }

        if(etDescriereEvenimentModificat.getText().toString().trim().isEmpty()) {
            etDescriereEvenimentModificat.setError("INTRODUCETI O DESCRIERE");
            etDescriereEvenimentModificat.requestFocus();
            return false;
        }
        return true;
    }

    private Eveniment creareEveniment(Eveniment eveniment) {
        Date dataEveniment = DateConverter.fromString(String.valueOf(etDataEvenimentModificat.getText().toString()));
        String descriere = etDescriereEvenimentModificat.getText().toString();
        String categorie = spinnerCategorieEveneimentModificat.getSelectedItem().toString();
        if(eveniment.getReminder() != null) {
            reminder = eveniment.getReminder();

        }

        return new Eveniment(etDenumireEvenimentModificat.getText().toString(), dataEveniment, descriere, categorie, reminder);
    }

    @Override
    protected void onPause() {
        super.onPause();
        etDataEvenimentModificat.setError(null);
        etDescriereEvenimentModificat.setError(null);
        btnSeteazaDataModificata.setError(null);
    }
}