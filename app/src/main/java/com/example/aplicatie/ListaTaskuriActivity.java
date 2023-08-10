package com.example.aplicatie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aplicatie.Notificare.NotificareTask;
import com.example.aplicatie.Reminder.Reminder;
import com.example.aplicatie.Task.AdaptorRVTask;
import com.example.aplicatie.Task.Task;
import com.example.aplicatie.Utilizator.Utilizator;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class ListaTaskuriActivity extends AppCompatActivity {
    private AdaptorRVTask adaptorRVTask;
    private RecyclerView recyclerViewListaTaskuri;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference utilizatori = db.collection("Utilizatori");
    private DocumentReference docUtilizatorCurent = utilizatori.document(FirebaseAuth.getInstance().getUid());

    Intent intent;

    @Override
    protected void onStart() {
        super.onStart();
        adaptorRVTask.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adaptorRVTask.startListening();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_taskuri);

        recyclerViewListaTaskuri = findViewById(R.id.recycleViewListaTaskuri);

        intent = getIntent();

        Query taskuriFS = docUtilizatorCurent.collection("Taskuri").orderBy("deadline");
        FirestoreRecyclerOptions<Task> options = new FirestoreRecyclerOptions.Builder<Task>()
                .setQuery(taskuriFS, Task.class)
                .build();

        adaptorRVTask = new AdaptorRVTask(options);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewListaTaskuri.setLayoutManager(layoutManager);
        recyclerViewListaTaskuri.setItemAnimator(null);
        recyclerViewListaTaskuri.setAdapter(adaptorRVTask);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if(direction == ItemTouchHelper.LEFT) {
                    adaptorRVTask.stergeTask(viewHolder.getAbsoluteAdapterPosition());
                    Intent intent = new Intent(ListaTaskuriActivity.this, NotificareTask.class);
                    Reminder reminder = adaptorRVTask.getItem(viewHolder.getAbsoluteAdapterPosition()).getReminder();
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(ListaTaskuriActivity.this,
                            reminder.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                    alarmManager.cancel(pendingIntent);
                    pendingIntent.cancel();
                }
            }


            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(ListaTaskuriActivity.this, R.color.delete_red))
                        .addSwipeLeftActionIcon(R.drawable.sterge_rand_rv)
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }).attachToRecyclerView(recyclerViewListaTaskuri);
    }



    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}