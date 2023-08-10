package com.example.aplicatie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
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
import com.example.aplicatie.Task.AdaptorRVTask;
import com.example.aplicatie.Task.DeadlineTaskDialog;
import com.example.aplicatie.Notificare.NotificareTask;
import com.example.aplicatie.Reminder.Reminder;
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

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class ModificaTaskActivity extends AppCompatActivity implements SarcinaDialog.SracinaDialogListener, DatePickerDialog.OnDateSetListener{
    private Button btnAdaugaSarcinaModificat, btnSeteazaDeadlineModificat, btnAdaugaCategorieTaskModificat,
            btnSalveazaTaskModificat, btnSeteazaReminderModificat;
    private RecyclerView recyclerViewListaSarciniModificate;
    private EditText etDenumireTaskModificat, etDeadlineModificat, etReminderTaskModificat;
    private Spinner spinnerGradImportantaTaskModificat, spinnerCategorieTaskModificat;

    private ArrayList<Sarcina> listaSarcini;
    private AdaptorRVSarcina adaptorRVSarcina;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference utilizatori = db.collection("Utilizatori");
    private DocumentReference docUtilizatorCurent = utilizatori.document(FirebaseAuth.getInstance().getUid());

    public static String MESAJ_REMINDER = "mesaj reminder";
    public static String DATA_REMINDER = "data reminder";

    private String categorieNoua;
    private Reminder reminder;
    private String stringReminder;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifica_task);

        getWindow().setSoftInputMode(WindowManager.
                LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        
        initComponents();

        btnAdaugaSarcinaModificat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogSarcinaNoua();
            }
        });

        btnSeteazaDeadlineModificat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DeadlineTaskDialog();
                datePicker.show(getSupportFragmentManager(), "");
            }
        });

        intent = getIntent();
        Task taskTrimis = (Task) intent.getSerializableExtra(AdaptorRVTask.MODIFICA_TASK);
        String idTask = (String) intent.getSerializableExtra(AdaptorRVTask.ID_TASK);

        btnSeteazaReminderModificat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adaugaReminder(taskTrimis);
            }
        });

        if(taskTrimis != null) {

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

                                                ArrayAdapter<String> adaptorSpinnerCategorieTask = new ArrayAdapter<String>(ModificaTaskActivity.this,
                                                        android.R.layout.simple_spinner_item, listaNoua);
                                                adaptorSpinnerCategorieTask.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                                spinnerCategorieTaskModificat.setAdapter(adaptorSpinnerCategorieTask);
                                                int pozCat = adaptorSpinnerCategorieTask.getPosition(taskTrimis.getCategorie());
                                                spinnerCategorieTaskModificat.setSelection(pozCat);
                                            }
                                        });
                            } else {
                                ArrayAdapter<String> adaptorSpinnerCategorieTask = new ArrayAdapter<String>(ModificaTaskActivity.this,
                                        android.R.layout.simple_spinner_item, listaCategoriiBasic);
                                adaptorSpinnerCategorieTask.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinnerCategorieTaskModificat.setAdapter(adaptorSpinnerCategorieTask);
                                int pozCat = adaptorSpinnerCategorieTask.getPosition(taskTrimis.getCategorie());
                                spinnerCategorieTaskModificat.setSelection(pozCat);
                            }
                        }
                    });

            ArrayAdapter<String> adaptorSpinnerGradImportantaTask = new ArrayAdapter<String>(ModificaTaskActivity.this,
                    android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.spinnerGradImportantaTask));
            adaptorSpinnerGradImportantaTask.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerGradImportantaTaskModificat.setAdapter(adaptorSpinnerGradImportantaTask);

            etDenumireTaskModificat.setText(taskTrimis.getDenumireTask());

            listaSarcini = new ArrayList<>();
            listaSarcini = taskTrimis.getListaSarcini();
            adaptorRVSarcina = new AdaptorRVSarcina(listaSarcini);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ModificaTaskActivity.this);
            recyclerViewListaSarciniModificate.setLayoutManager(layoutManager);
            recyclerViewListaSarciniModificate.setAdapter(adaptorRVSarcina);

            btnAdaugaCategorieTaskModificat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dialog dialog = new Dialog(ModificaTaskActivity.this);
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

                            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(ModificaTaskActivity.this,
                                    android.R.layout.simple_spinner_item, listaNoua);
                            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerCategorieTaskModificat.setAdapter(spinnerArrayAdapter);

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

            if(taskTrimis.getReminder() != null) {
                etReminderTaskModificat.setText(DateConverter.fromReminder(taskTrimis.getReminder().getDataReminder()));
            }
            etDeadlineModificat.setText(DateConverter.fromDate(taskTrimis.getDeadline()));
            int pozGrad = adaptorSpinnerGradImportantaTask.getPosition(taskTrimis.getGradImportanta());
            spinnerGradImportantaTaskModificat.setSelection(pozGrad);

            new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                    if(direction == ItemTouchHelper.LEFT) {
                        listaSarcini.remove(viewHolder.getAdapterPosition());
                        adaptorRVSarcina.stergeSarcina(idTask, listaSarcini);
                        adaptorRVSarcina.notifyDataSetChanged();
                    }
                }

                @Override
                public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX,
                                        float dY, int actionState, boolean isCurrentlyActive) {
                    new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                            .addSwipeLeftBackgroundColor(ContextCompat.getColor(recyclerViewListaSarciniModificate.getContext(), R.color.red))
                            .addSwipeLeftActionIcon(R.drawable.sterge_rand_rv)
                            .create()
                            .decorate();

                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
            }).attachToRecyclerView(recyclerViewListaSarciniModificate);
        }

        btnSalveazaTaskModificat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValid()) {
                    Task taskModificat = creareTask(taskTrimis);
                    if(taskModificat != null) {
                        docUtilizatorCurent.collection("Taskuri")
                                .document(idTask).set(taskModificat)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(ModificaTaskActivity.this,
                                                "Task modificat cu succes", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(ModificaTaskActivity.this, "EROARE LA MODIFICARE TASK" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
            }
        });
    }
    private void adaugaReminder(Task task) {
        Dialog dialog = new Dialog(ModificaTaskActivity.this);
        dialog.setContentView(R.layout.dialog_seteaza_reminder);

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
                DatePickerDialog datePickerDialog = new DatePickerDialog(ModificaTaskActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        final Calendar data = Calendar.getInstance();
                        Calendar ora = Calendar.getInstance();
                        TimePickerDialog timePickerDialog = new TimePickerDialog(ModificaTaskActivity.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                data.set(year, month, dayOfMonth, hourOfDay, minute, 0);
                                Calendar momCurent = Calendar.getInstance();
                                if(data.getTimeInMillis() - momCurent.getTimeInMillis() > 0) {
                                    etDataReminderTask.setText(DateConverter.fromReminder(data.getTime()));
                                    stringReminder = data.getTime().toString().trim();
                                } else {
                                    Toast.makeText(ModificaTaskActivity.this, "INVALID", Toast.LENGTH_SHORT).show();
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
                reminder = task.getReminder();
                if(reminder == null) {
                    reminder = new Reminder();
                    reminder.setId(new Random().nextInt());
                }
                reminder.setMesaj("Nu uita de task-ul " + etDenumireTaskModificat.getText() + " din data " +
                        etDeadlineModificat.getText().toString());
                Date dataReminder = new Date(stringReminder);
                reminder.setDataReminder(dataReminder);

                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+2:00"));
                calendar.setTime(dataReminder);
                calendar.set(Calendar.SECOND, 0);
                Intent intent = new Intent(ModificaTaskActivity.this, NotificareTask.class);
                intent.putExtra(MESAJ_REMINDER, reminder.getMesaj());
                intent.putExtra(DATA_REMINDER, reminder.getDataReminder());

                PendingIntent pendingIntent = PendingIntent.getBroadcast(ModificaTaskActivity.this,
                        reminder.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                alarmManager.cancel(pendingIntent);

                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

                etReminderTaskModificat.setText(DateConverter.fromReminder(calendar.getTime()));
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
        Intent intent = new Intent(getApplicationContext(), ListaTaskuriActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void initComponents() {
        btnAdaugaSarcinaModificat = findViewById(R.id.btnAdaugaSarcinaModificat);
        btnAdaugaCategorieTaskModificat = findViewById(R.id.btnAdaugaCategorieTaskModificat);
        btnSalveazaTaskModificat = findViewById(R.id.btnSalveazaTaskModificat);
        btnSeteazaReminderModificat = findViewById(R.id.btnSeteazaReminderModificat);
        recyclerViewListaSarciniModificate = findViewById(R.id.recycleViewListaSarciniModificate);
        etDenumireTaskModificat = findViewById(R.id.etDenumireTaskModificat);
        etReminderTaskModificat = findViewById(R.id.etReminderTaskModificat);
        btnSeteazaDeadlineModificat = findViewById(R.id.btnSeteazaDeadlineModificat);
        etDeadlineModificat = findViewById(R.id.etDeadlineModificat);
        spinnerGradImportantaTaskModificat = findViewById(R.id.spinnerGradImportantaTaskModificat);
        spinnerCategorieTaskModificat = findViewById(R.id.spinnerCategorieTaskModificat);
    }

    private Task creareTask(Task task) {
        Date deadline = DateConverter.fromString(String.valueOf(etDeadlineModificat.getText()));
        String gradImportanta = spinnerGradImportantaTaskModificat.getSelectedItem().toString();
        String categorie = spinnerCategorieTaskModificat.getSelectedItem().toString();
        if(task.getReminder() != null) {
            reminder = task.getReminder();

        }

        return new Task(etDenumireTaskModificat.getText().toString(), listaSarcini, deadline,
                gradImportanta, categorie, reminder);
    }

    private boolean isValid() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        if((DateConverter.fromString(etDeadlineModificat.getText().toString())).before(calendar.getTime())) {
            etDeadlineModificat.setError("INTRODUCETI UN DEADLINE VALID");
            etDeadlineModificat.requestFocus();
            return false;
        }
        if(listaSarcini == null || listaSarcini.isEmpty()) {
            btnAdaugaSarcinaModificat.setError("INTRODUCETI CEL PUTIN O SARCINA");
            btnAdaugaSarcinaModificat.requestFocus();
            return false;
        }
        return true;
    }

    private void openDialogSarcinaNoua() {
        SarcinaDialog sarcinaDialog = new SarcinaDialog();
        sarcinaDialog.show(getSupportFragmentManager(), "");
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
        etDeadlineModificat.setText(dataSelectata);
    }

    @Override
    protected void onPause() {
        super.onPause();
        etDeadlineModificat.setError(null);
        btnAdaugaSarcinaModificat.setError(null);
    }
}