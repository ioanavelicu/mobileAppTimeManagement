package com.example.aplicatie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.aplicatie.Categorie.Categorie;
import com.example.aplicatie.DateConverter.DateConverter;
import com.example.aplicatie.Sarcina.AdaptorRVSarcina;
import com.example.aplicatie.Sarcina.Sarcina;
import com.example.aplicatie.Sarcina.SarcinaDialog;
import com.example.aplicatie.Task.DeadlineTaskDialog;
import com.example.aplicatie.Reminder.Reminder;
import com.example.aplicatie.Notificare.NotificareTask;
import com.example.aplicatie.Task.Task;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

public class AddTaskActivity extends AppCompatActivity implements SarcinaDialog.SracinaDialogListener, DatePickerDialog.OnDateSetListener {
    public static final String TASK = "Task";
    public static final String MESAJ_REMINDER = "mesaj reminder";
    public static final String DATA_REMINDER = "data reminder";
    public static final String ARE_REMINDER = "are reminder";
    public static final String ID_REMINDER = "id";

    private Button btnAdaugaSarcina, btnSeteazaDeadline, btnAdaugaCategorieTask, btnSalveazaTaskNou,
                        btnSeteazaReminderTask;
    private RecyclerView recyclerViewListaSarcini;
    private EditText etDenumireTask, etDeadline, etReminderTask;
    private Spinner spinnerGradImportantaTask, spinnerCategorieTask;

    private ArrayList<Sarcina> listaSarcini;
    private AdaptorRVSarcina adaptorRVSarcina;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference utilizatori = db.collection("Utilizatori");
    private DocumentReference docUtilizatorCurent = utilizatori.document(FirebaseAuth.getInstance().getUid());

    private String taskId;
    private String stringReminder;

    private Dialog dialog;
    private Reminder reminder;

    private String categorieNoua;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        initComponents();

        String[] listaCategoriiBasic = getResources().getStringArray(R.array.spinnerCategorieTask);
        docUtilizatorCurent.collection("Categorii").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<QuerySnapshot> task) {
                        int nrCategoriiBD = task.getResult().getDocuments().size();
                        if(nrCategoriiBD > 0) {
                            docUtilizatorCurent.collection("Categorii").get()
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

                                            ArrayAdapter<String> adaptorSpinnerCategorieTask = new ArrayAdapter<String>(AddTaskActivity.this,
                                                    android.R.layout.simple_spinner_item, listaNoua);
                                            adaptorSpinnerCategorieTask.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                            spinnerCategorieTask.setAdapter(adaptorSpinnerCategorieTask);
                                        }
                                    });
                        } else {
                            ArrayAdapter<String> adaptorSpinnerCategorieTask = new ArrayAdapter<String>(AddTaskActivity.this,
                                    android.R.layout.simple_spinner_item, listaCategoriiBasic);
                            adaptorSpinnerCategorieTask.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerCategorieTask.setAdapter(adaptorSpinnerCategorieTask);
                        }
                    }
                });

        ArrayAdapter<String> adaptorSpinnerGradImportantaTask = new ArrayAdapter<String>(AddTaskActivity.this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.spinnerGradImportantaTask));
        adaptorSpinnerGradImportantaTask.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGradImportantaTask.setAdapter(adaptorSpinnerGradImportantaTask);

        listaSarcini = new ArrayList<>();
        adaptorRVSarcina = new AdaptorRVSarcina(listaSarcini);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewListaSarcini.setLayoutManager(layoutManager);
        recyclerViewListaSarcini.setItemAnimator(new DefaultItemAnimator());
        recyclerViewListaSarcini.setAdapter(adaptorRVSarcina);

        btnAdaugaSarcina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogSarcinaNoua();
            }
        });

        btnSeteazaDeadline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DeadlineTaskDialog();
                datePicker.show(getSupportFragmentManager(), "");
            }
        });

        btnSeteazaReminderTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adaugaReminder();
            }
        });

        btnAdaugaCategorieTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(AddTaskActivity.this);
                dialog.setContentView(R.layout.dialog_categorie_noua);
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                dialog.getWindow().setBackgroundDrawable(getApplicationContext().getDrawable(R.drawable.dialog_albastru));

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

                        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(AddTaskActivity.this,
                                android.R.layout.simple_spinner_item, listaNoua);
                        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerCategorieTask.setAdapter(spinnerArrayAdapter);

                        if(categorieNoua != null || !categorieNoua.trim().isEmpty()) {
                            Categorie categorie = new Categorie(categorieNoua);
                            docUtilizatorCurent.collection("Categorii").add(categorie);
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
        btnSalveazaTaskNou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValid()) {
                    Task task = creareTask();
                    if(task != null) {
                        adaugaTask(task);
                    }
                }
            }
        });
    }

    private void adaugaTask(Task task) {
        docUtilizatorCurent.collection("Taskuri").add(task)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        taskId = documentReference.getId();
                        Toast.makeText(AddTaskActivity.this,
                                R.string.toastTaskAddSucces, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddTaskActivity.this,
                                getString(R.string.toastTaskAddEroare) + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void adaugaReminder() {
        dialog = new Dialog(AddTaskActivity.this);
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
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddTaskActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        final Calendar data = Calendar.getInstance();
                        Calendar ora = Calendar.getInstance();
                        TimePickerDialog timePickerDialog = new TimePickerDialog(AddTaskActivity.this,
                                new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                data.set(year, month, dayOfMonth, hourOfDay, minute, 0);
                                Calendar momCurent = Calendar.getInstance();
                                if(data.getTimeInMillis() - momCurent.getTimeInMillis() > 0) {
                                    etDataReminderTask.setText(DateConverter.fromReminder(data.getTime()));
                                    stringReminder = data.getTime().toString().trim();
                                } else {
                                    Toast.makeText(AddTaskActivity.this, "INVALID",
                                            Toast.LENGTH_SHORT).show();
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
                reminder.setMesaj("Nu uita de task-ul " + etDenumireTask.getText() + " din data "
                        + etDeadline.getText().toString());
                Date dataReminder = new Date(stringReminder);
                reminder.setDataReminder(dataReminder);
                reminder.setId(new Random().nextInt());

                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+2:00"));
                calendar.setTime(dataReminder);
                calendar.set(Calendar.SECOND, 0);
                Intent intent = new Intent(AddTaskActivity.this, NotificareTask.class);
                intent.putExtra(MESAJ_REMINDER, reminder.getMesaj());
                intent.putExtra(DATA_REMINDER, reminder.getDataReminder());

                PendingIntent pendingIntent = PendingIntent.getBroadcast(AddTaskActivity.this,
                        reminder.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

                etReminderTask.setText(DateConverter.fromReminder(calendar.getTime()));
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

    private Task creareTask() {
        String denumireTask = String.valueOf(etDenumireTask.getText());
        Date deadline = DateConverter.fromString(String.valueOf(etDeadline.getText()));
        String gradImportanta = spinnerGradImportantaTask.getSelectedItem().toString();
        String categorie = spinnerCategorieTask.getSelectedItem().toString();

        return new Task(denumireTask, listaSarcini, deadline, gradImportanta, categorie, reminder);
    }

    private void openDialogSarcinaNoua() {
        SarcinaDialog sarcinaDialog = new SarcinaDialog();
        sarcinaDialog.show(getSupportFragmentManager(), "");
    }

    private void initComponents() {
        btnAdaugaSarcina = findViewById(R.id.btnAdaugaSarcina);
        btnAdaugaCategorieTask = findViewById(R.id.btnAdaugaCategorieTask);
        btnSeteazaReminderTask = findViewById(R.id.btnSeteazaReminder);
        btnSalveazaTaskNou = findViewById(R.id.btnSalveazaTaskNou);
        recyclerViewListaSarcini = findViewById(R.id.recycleViewListaSarcini);
        etDenumireTask = findViewById(R.id.etDenumireTask);
        btnSeteazaDeadline = findViewById(R.id.btnSeteazaDeadline);
        etDeadline = findViewById(R.id.etDeadline);
        etReminderTask = findViewById(R.id.etReminderTask);
        spinnerGradImportantaTask = findViewById(R.id.spinnerGradImportantaTask);
        spinnerCategorieTask = findViewById(R.id.spinnerCategorieTask);
    }

    @Override
    public void applyTexts(String descriereSarcina) {
        listaSarcini.add(new Sarcina(descriereSarcina));
        adaptorRVSarcina.notifyDataSetChanged();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        String dataSelectata = DateConverter.fromDate(calendar.getTime());
        etDeadline.setText(dataSelectata);
    }

    private boolean isValid() {
        if (etDenumireTask.getText().toString().trim().isEmpty()) {
            etDenumireTask.setError("INTRODUCETI DENUMIREA TASK-ULUI");
            etDenumireTask.requestFocus();
            return false;
        }
        if(etDeadline.getText().toString().trim().isEmpty()) {
            etDeadline.setError("INTRODUCETI DEADLINE");
            etDeadline.requestFocus();
            return false;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        if ((DateConverter.fromString(etDeadline.getText().toString())).before(calendar.getTime())) {
            etDeadline.setError("INTRODUCETI UN DEADLINE VALID");
            etDeadline.requestFocus();
            return false;
        }
        if(listaSarcini == null || listaSarcini.isEmpty()) {
            btnAdaugaSarcina.setError("INTRODUCETI CEL PUTIN O SARCINA");
            btnAdaugaSarcina.requestFocus();
            return false;
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        etDenumireTask.setError(null);
        etDeadline.setError(null);
        btnAdaugaSarcina.setError(null);
    }
}