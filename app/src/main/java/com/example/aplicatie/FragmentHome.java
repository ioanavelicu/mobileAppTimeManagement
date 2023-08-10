package com.example.aplicatie;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.TextView;

import com.example.aplicatie.Jurnal.JurnalDialog;
import com.example.aplicatie.Jurnal.JurnalDialogParolaNoua;
import com.example.aplicatie.Task.Task;
import com.example.aplicatie.Utilizator.Utilizator;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class FragmentHome extends Fragment {
    public static final String JURNAL = "jurnal";
    public static final String KEY = "key";
    public static final String TRIMITE_ID_NOTIFICARE = "id notificare";

    private FloatingActionButton btnPrincipal, fabTask, fabAdaugaTask,
        fabListaTaskuri, fabNotite, fabObiective, fabJurnal, fabEveniment,
        fabAdaugaEveniment, fabListaEvenimente;
    private TextView tvButonPricipal,tvTask, tvAdaugaTask, tvListaTaskuri,
        tvNotite, tvObiective, tvJurnal, tvEveniment, tvAdaugaEveniment,
            tvListaEvenimente;
    private EditText etParolaJurnal, etParolaNouaJurnal;

    private Boolean isTaskOpen = true;
    private Boolean isEvenimentOpen = true;
    private Boolean isButonPrincipalOpen = true;
    private String parolaJurnal;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference utilizatori = db.collection("Utilizatori");
    private DocumentReference docUtilizatorCurent = utilizatori.document(FirebaseAuth.getInstance().getUid());

    public FragmentHome() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initComponents(view);

        start();

        btnPrincipal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openButonPrincipal();
            }
        });

        fabTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isTaskOpen) {
                    closeTask();
                } else {
                    openTask();
                }
            }
        });

        fabEveniment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isEvenimentOpen) {
                    closeEveniment();
                } else {
                    openEveniment();
                }
            }
        });

        fabAdaugaTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddTaskActivity.class);
                startActivity(intent);
            }
        });

        fabListaTaskuri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ListaTaskuriActivity.class);
                startActivity(intent);
            }
        });

        fabAdaugaEveniment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddEvenimentActivity.class);
                startActivity(intent);
            }
        });

        fabListaEvenimente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ListaEvenimenteActivity.class);
                startActivity(intent);
            }
        });

        fabNotite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ListaNotiteActivity.class);
                startActivity(intent);
            }
        });

        fabJurnal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                docUtilizatorCurent.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Utilizator utilizator = documentSnapshot.toObject(Utilizator.class);
                        if(utilizator.getParolaJurnal().trim().isEmpty()) {
                            openDialogParolaNoua();
                        } else {
                            parolaJurnal = utilizator.getParolaJurnal();
                            Bundle bundle = new Bundle();
                            bundle.putString(JURNAL, parolaJurnal);
                            getParentFragmentManager().setFragmentResult(KEY, bundle);
                            openDialogParola();
                        }
                    }
                });
            }
        });

        fabObiective.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ListaObiectiveActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    private void initComponents(View view) {
        btnPrincipal = view.findViewById(R.id.btnPrincipal);
        fabTask = view.findViewById(R.id.fabTask);
        fabAdaugaTask = view.findViewById(R.id.fabAdaugaTask);
        fabListaTaskuri = view.findViewById(R.id.fabListaTaskuri);
        fabEveniment = view.findViewById(R.id.fabEveniment);
        fabAdaugaEveniment = view.findViewById(R.id.fabAdaugaEveniment);
        fabListaEvenimente = view.findViewById(R.id.fabListaEvenimente);
        fabNotite = view.findViewById(R.id.fabNotite);
        fabJurnal = view.findViewById(R.id.fabJurnal);
        fabObiective = view.findViewById(R.id.fabObiective);
        tvButonPricipal = view.findViewById(R.id.tvButonPrincipal);
        tvTask = view.findViewById(R.id.tvTask);
        tvAdaugaTask = view.findViewById(R.id.tvAdaugaTask);
        tvListaTaskuri = view.findViewById(R.id.tvListaTaskuri);
        tvEveniment = view.findViewById(R.id.tvEveniment);
        tvAdaugaEveniment = view.findViewById(R.id.tvAdaugaEveniment);
        tvListaEvenimente = view.findViewById(R.id.tvListaEvenimente);
        tvNotite = view.findViewById(R.id.tvNotite);
        tvJurnal = view.findViewById(R.id.tvJurnal);
        tvObiective = view.findViewById(R.id.tvObiective);
        etParolaJurnal = view.findViewById(R.id.etParolaJurnal);
        etParolaNouaJurnal = view.findViewById(R.id.etParolaNouaJurnal);
    }

    private void start() {
        fabTask.setVisibility(View.GONE);
        fabEveniment.setVisibility(View.GONE);
        fabNotite.setVisibility(View.GONE);
        fabJurnal.setVisibility(View.GONE);
        fabObiective.setVisibility(View.GONE);
        fabAdaugaTask.setVisibility(View.GONE);
        fabAdaugaEveniment.setVisibility(View.GONE);
        fabListaTaskuri.setVisibility(View.GONE);
        fabListaEvenimente.setVisibility(View.GONE);
        tvJurnal.setVisibility(View.GONE);
        tvAdaugaTask.setVisibility(View.GONE);
        tvListaTaskuri.setVisibility(View.GONE);
        tvAdaugaEveniment.setVisibility(View.GONE);
        tvListaEvenimente.setVisibility(View.GONE);
        tvEveniment.setVisibility(View.GONE);
        tvTask.setVisibility(View.GONE);
        tvNotite.setVisibility(View.GONE);
        tvObiective.setVisibility(View.GONE);
    }

    private void openButonPrincipal() {
        isButonPrincipalOpen = !isButonPrincipalOpen;
        fabTask.animate().translationY(-30f);
        fabTask.show();

        fabEveniment.animate().translationY(-30f);
        fabEveniment.show();

        fabJurnal.animate().translationY(100f);
        fabJurnal.show();

        fabObiective.animate().translationY(30f);
        fabObiective.show();

        fabNotite.animate().translationY(30f);
        fabNotite.show();

        tvJurnal.setVisibility(View.VISIBLE);
        tvTask.setVisibility(View.VISIBLE);
        tvEveniment.setVisibility(View.VISIBLE);
        tvNotite.setVisibility(View.VISIBLE);
        tvObiective.setVisibility(View.VISIBLE);
    }

    private void openTask() {
        isTaskOpen = !isTaskOpen;

        fabTask.animate().setInterpolator(new OvershootInterpolator())
                .rotationBy(360f).setDuration(300).start();

        fabAdaugaTask.animate().translationY(-10f).translationX(-90f).alpha(1f).setInterpolator(new OvershootInterpolator()).setDuration(300);
        fabAdaugaTask.show();
        fabListaTaskuri.animate().translationY(-10f).translationX(60f).alpha(1f).setInterpolator(new OvershootInterpolator()).setDuration(300);
        fabListaTaskuri.show();
        tvAdaugaTask.setVisibility(View.VISIBLE);
        tvListaTaskuri.setVisibility(View.VISIBLE);
    }

    private void closeTask() {
        isTaskOpen = !isTaskOpen;

        fabTask.animate().setInterpolator(new OvershootInterpolator())
                .rotationBy(0f).setDuration(300).start();

        fabAdaugaTask.animate().translationY(20f).alpha(0f).setInterpolator(new OvershootInterpolator()).setDuration(300).start();
        fabListaTaskuri.animate().translationY(10f).alpha(0f).setInterpolator(new OvershootInterpolator()).setDuration(300).start();
        tvAdaugaTask.setVisibility(View.INVISIBLE);
        tvListaTaskuri.setVisibility(View.INVISIBLE);
    }

    private void openEveniment() {
        isEvenimentOpen = !isEvenimentOpen;

        fabEveniment.animate().setInterpolator(new OvershootInterpolator())
                .rotationBy(360f).setDuration(300).start();

        fabAdaugaEveniment.animate().translationY(-10f).translationX(90f).alpha(1f).setInterpolator(new OvershootInterpolator()).setDuration(300);
        fabAdaugaEveniment.show();
        fabListaEvenimente.animate().translationY(-10f).translationX(-60f).alpha(1f).setInterpolator(new OvershootInterpolator()).setDuration(300);
        fabListaEvenimente.show();
        tvAdaugaEveniment.setVisibility(View.VISIBLE);
        tvListaEvenimente.setVisibility(View.VISIBLE);
    }

    private void closeEveniment() {
        isEvenimentOpen = !isEvenimentOpen;

        fabEveniment.animate().setInterpolator(new OvershootInterpolator())
                .rotationBy(0f).setDuration(300).start();

        fabAdaugaEveniment.animate().translationY(20f).alpha(0f).setInterpolator(new OvershootInterpolator()).setDuration(300).start();
        fabListaEvenimente.animate().translationY(10f).alpha(0f).setInterpolator(new OvershootInterpolator()).setDuration(300).start();
        tvAdaugaEveniment.setVisibility(View.INVISIBLE);
        tvListaEvenimente.setVisibility(View.INVISIBLE);
    }

    private void openDialogParola() {
        JurnalDialog jurnalDialog = new JurnalDialog();
        jurnalDialog.show(getFragmentManager(), "");
    }

    private void openDialogParolaNoua() {
        JurnalDialogParolaNoua jurnalDialogParolaNoua = new JurnalDialogParolaNoua();
        jurnalDialogParolaNoua.show(getFragmentManager(), "");
    }
}