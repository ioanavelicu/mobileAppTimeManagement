package com.example.aplicatie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.os.Bundle;

import com.example.aplicatie.Sarcina.Sarcina;
import com.example.aplicatie.Statistici.AdapterRVStatistici;
import com.example.aplicatie.Statistici.DataItem;
import com.example.aplicatie.Utilizator.Utilizator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class StatisticiActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference utilizatori = db.collection("Utilizatori");
    private DocumentReference docUtilizatorCurent = utilizatori.document(FirebaseAuth.getInstance().getUid());

    private RecyclerView recycleViewStatistici;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistici);

        initComponents();

        docUtilizatorCurent.collection("Taskuri").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        int nrTotalTaskuri = task.getResult().getDocuments().size();
                        int nrTakuriComplete = 0;

                        int nrTotalSarcini = 0;
                        int nrSarciniComplete = 0;

                        for(com.example.aplicatie.Task.Task taskUtilizator : task.getResult().toObjects(com.example.aplicatie.Task.Task.class)) {
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                long nrt = taskUtilizator.getListaSarcini().stream()
                                        .map(Sarcina::getEsteBifata)
                                        .filter(esteBifata -> !esteBifata)
                                        .count();
                                long nrs = taskUtilizator.getListaSarcini().stream().count();
                                nrSarciniComplete += nrt;
                                nrTotalSarcini += nrs;
                                if(nrt == 0) {
                                    nrTakuriComplete++;
                                }
                            }
                        }
                        List<DataItem> dataList = new ArrayList<>();
                        dataList.add(new DataItem(nrTotalTaskuri-nrTakuriComplete,nrTakuriComplete,
                                "Număr taskuri neterminate", "Număr taskuri terminate", "Taskuri terminate"));
                        dataList.add(new DataItem(nrTotalSarcini-nrSarciniComplete,nrSarciniComplete,
                                "Număr sarcini neîndeplinite", "Număr sarcini îndeplinite", "Sarcini îndeplinite"));

                        AdapterRVStatistici adapter = new AdapterRVStatistici(dataList); // dataList reprezintă lista ta de date
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
                        recycleViewStatistici.setLayoutManager(layoutManager);
                        recycleViewStatistici.setItemAnimator(null);
                        recycleViewStatistici.setAdapter(adapter);

                        SnapHelper snapHelper = new PagerSnapHelper();
                        snapHelper.attachToRecyclerView(recycleViewStatistici);
                    }
                });
    }

    private void initComponents() {
        recycleViewStatistici = findViewById(R.id.recycleViewStatistici);
    }
}