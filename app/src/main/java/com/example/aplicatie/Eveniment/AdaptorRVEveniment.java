package com.example.aplicatie.Eveniment;

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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aplicatie.DateConverter.DateConverter;
import com.example.aplicatie.ModificaEvenimentActivity;
import com.example.aplicatie.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.Calendar;

public class AdaptorRVEveniment extends FirestoreRecyclerAdapter<Eveniment, AdaptorRVEveniment.MyViewHolder> {
    public static final String MODIFICA_EVENIMENT = "modifica eveniment";
    public static final String ID_EVENIMENT = "id eveniment";

    Context context;

    public AdaptorRVEveniment(@NonNull FirestoreRecyclerOptions<Eveniment> options) {
        super(options);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDenumireEveniment, tvDataEveniment, tvCategorieEveniment, tvDescriere;
        private ImageButton btnModificaEveniment, btnReminderEveniment;
        private ConstraintLayout item;

        public MyViewHolder(final View view) {
            super(view);

            item = view.findViewById(R.id.constraintLayoutRVEvenimente);
            tvDenumireEveniment = view.findViewById(R.id.tvDenumireEveniment);
            tvDataEveniment = view.findViewById(R.id.tvDataEveniment);
            tvCategorieEveniment = view.findViewById(R.id.tvCategorieEveniment);
            btnModificaEveniment = view.findViewById(R.id.btnModificaEveniment);
            btnReminderEveniment = view.findViewById(R.id.btnReminderEveniment);
            tvDescriere = view.findViewById(R.id.tvDescriere);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_rv_lista_evenimente,
                parent, false);
        final MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Eveniment model) {
        Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.animatie);
        long delay = position * 100;
        animation.setStartOffset(delay);
        holder.itemView.startAnimation(animation);

        holder.tvDenumireEveniment.setText(model.getDenumireEveniment());
        holder.tvDenumireEveniment.setSelected(true);
        holder.tvDataEveniment.setText(DateConverter.fromDate(model.getDataEveniment()));

        if(!model.getCategorieEveneiment().equalsIgnoreCase("CATEGORIE")) {
            holder.tvCategorieEveniment.setText(String.format("Categorie\n%s", model.getCategorieEveneiment()));
        } else {
            holder.tvCategorieEveniment.setText("");
        }

        holder.tvDescriere.setText(model.getDescriereEveniment());

        context = holder.itemView.getContext();
        if(model.getReminder() == null) {
            holder.btnReminderEveniment.setBackground(new ColorDrawable(Color.parseColor("#E6777575")));
        } else {
            holder.btnReminderEveniment.setBackground(context.getDrawable(R.drawable.btn_mic));
        }

        context = holder.itemView.getContext();
        holder.btnModificaEveniment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ModificaEvenimentActivity.class);
                intent.putExtra(MODIFICA_EVENIMENT, model);
                intent.putExtra(ID_EVENIMENT, getSnapshots().getSnapshot(holder.getAbsoluteAdapterPosition()).getReference().getId());
                context.startActivity(intent);
                ((Activity)holder.itemView.getContext()).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        if(model.getReminder() == null) {
            holder.btnReminderEveniment.setBackground(new ColorDrawable(Color.parseColor("#9F393335")));
        }
        holder.btnReminderEveniment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(model.getReminder() != null) {
                    arataReminder(context, DateConverter.fromReminder(model.getReminder().getDataReminder()));
                } else  {
                    Toast.makeText(context, "Evenimentul nu are reminder setat", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        if(model.getDataEveniment().before(calendar.getTime())) {
            holder.item.setBackground(new ColorDrawable(Color.parseColor("#AE4C4547")));
        } else {
            holder.item.setBackground(new ColorDrawable(Color.parseColor("#4DF48CB0")));
        }
    }

    private void arataReminder(Context context, String reminder) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_reminder);

        EditText etDialogReminder;
        etDialogReminder = dialog.findViewById(R.id.etDialogReminder);
        etDialogReminder.setText(reminder);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.show();
    }

    public void stergeEveniment(int position) {
        getSnapshots().getSnapshot(position).getReference()
                .delete();
    }
}
