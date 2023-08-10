package com.example.aplicatie.Task;

import static android.media.CamcorderProfile.get;
import static android.media.CamcorderProfile.hasProfile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aplicatie.DateConverter.DateConverter;
import com.example.aplicatie.ModificaTaskActivity;
import com.example.aplicatie.R;
import com.example.aplicatie.Sarcina.Sarcina;
import com.example.aplicatie.VizualizareTaskActivity;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.Calendar;

public class AdaptorRVTask extends FirestoreRecyclerAdapter<Task, AdaptorRVTask.MyViewHolder> {
    public static final String MODIFICA_TASK = "modifica task";
    public static final String VIZUALIZEAZA_TASK = "vizualizaeaza_task";
    public static final String ID_TASK = "id_task";

    private Context context;

    public AdaptorRVTask(@NonNull FirestoreRecyclerOptions<Task> options) {
        super(options);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDenumireTask, tvDeadline, tvGradImportanta, tvCategorie, tvNumarSarcini;
        private ImageButton btnVizualizareTask, btnModificaTask, btnReminderTask;
        private ConstraintLayout item;
        private CardView cvTask;

        public MyViewHolder(final View view) {
            super(view);

            item = view.findViewById(R.id.constraintLayoutRVTaskuri);
            cvTask = view.findViewById(R.id.cvTask);

            tvDenumireTask = view.findViewById(R.id.tvDenumireTask);
            tvDeadline = view.findViewById(R.id.tvDeadline);
            tvGradImportanta = view.findViewById(R.id.tvGradImportanta);
            tvCategorie = view.findViewById(R.id.tvCategorie);
            tvNumarSarcini = view.findViewById(R.id.tvNrSarcini);
            btnVizualizareTask = view.findViewById(R.id.btnVizualizareTask);
            btnModificaTask = view.findViewById(R.id.btnModificaTask);
            btnReminderTask = view.findViewById(R.id.btnReminderTask);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_rv_lista_taskuri,
                parent, false);
        final MyViewHolder myViewHolder = new MyViewHolder(view);

        return myViewHolder;
    }


    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull Task model) {
        Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.animatie);
        long delay = position * 100;
        animation.setStartOffset(delay);
        holder.itemView.startAnimation(animation);

        holder.tvDenumireTask.setText(model.getDenumireTask());
        holder.tvDenumireTask.setSelected(true);
        holder.tvDeadline.setText(DateConverter.fromDate(model.getDeadline()));

        if(!model.getGradImportanta().equalsIgnoreCase("GRAD IMPORTANȚĂ")) {
            holder.tvGradImportanta.setText(String.format("Grad importanta\n%s", model.getGradImportanta()));
        } else {
            holder.tvGradImportanta.setText("");
        }

        if(!model.getCategorie().equalsIgnoreCase("CATEGORIE")) {
            holder.tvCategorie.setText(String.format("Categorie\n%s", model.getCategorie()));
        } else {
            holder.tvCategorie.setText("");
        }

        String textNrSarcini = "Numar de sarcini ramase:";
        String nrSarciniInitial = String.valueOf(model.getListaSarcini().size());
        String nrSarciniRamase = "";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            long nr = model.getListaSarcini().stream()
                    .map(Sarcina::getEsteBifata)
                    .filter(esteBifata -> !esteBifata)
                    .count();
            if(nr==0) {
                holder.cvTask.setVisibility(View.GONE);
                model.setGata(true);
            }
            nrSarciniRamase = String.valueOf(nr);
        }
        holder.tvNumarSarcini.setText(String.format("%s%s/%s", textNrSarcini, nrSarciniRamase, nrSarciniInitial));

        context = holder.itemView.getContext();

        holder.btnVizualizareTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, VizualizareTaskActivity.class);
                intent.putExtra(VIZUALIZEAZA_TASK, model);
                intent.putExtra(ID_TASK, getSnapshots().getSnapshot(position).getReference().getId());
                context.startActivity(intent);
                ((Activity)holder.itemView.getContext()).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        holder.btnModificaTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ModificaTaskActivity.class);
                intent.putExtra(MODIFICA_TASK, model);
                intent.putExtra(ID_TASK, getSnapshots().getSnapshot(position).getReference().getId());
                context.startActivity(intent);
                ((Activity)holder.itemView.getContext()).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//                holder.getBindingAdapter().notifyDataSetChanged();
            }
        });

        if(model.getReminder() == null) {
            holder.btnReminderTask.setBackground(new ColorDrawable(Color.parseColor("#E6777575")));
        } else {
            holder.btnReminderTask.setBackground(context.getDrawable(R.drawable.btn_mic));
        }

        holder.btnReminderTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(model.getReminder() != null) {
                    arataReminder(context, DateConverter.fromReminder(model.getReminder().getDataReminder()));
                } else  {
                    Toast.makeText(context, R.string.toastResminderTask, Toast.LENGTH_SHORT).show();
                }
            }
        });

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        if(model.getDeadline().before(calendar.getTime())) {
            holder.item.setBackground(new ColorDrawable(Color.parseColor("#AE777575")));
            holder.btnVizualizareTask.setBackground(new ColorDrawable(Color.parseColor("#E6777575")));
            holder.btnVizualizareTask.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "A fost depășit deadlinul", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            holder.item.setBackground(new ColorDrawable(Color.parseColor("#4DF48CB0")));
            holder.btnVizualizareTask.setBackground(context.getDrawable(R.drawable.btn_mic));
        }
    }

    private void arataReminder(Context context, String reminder) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_reminder);
        dialog.getWindow().setBackgroundDrawable(context.getDrawable(R.drawable.dialog_albastru));

        EditText etDialogReminder;
        etDialogReminder = dialog.findViewById(R.id.etDialogReminder);
        etDialogReminder.setText(reminder);
        dialog.show();
    }

    public void stergeTask(int position) {
        getSnapshots().getSnapshot(position).getReference()
                .delete();
    }
}
