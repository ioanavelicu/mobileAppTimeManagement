package com.example.aplicatie.Sarcina;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aplicatie.R;
import com.example.aplicatie.Sarcina.Sarcina;

import java.util.ArrayList;

public class AdaptorRVSarcinaBif extends RecyclerView.Adapter<AdaptorRVSarcinaBif.MyViewHolder> {
    private ArrayList<Sarcina> listaSarcini;

    public AdaptorRVSarcinaBif(ArrayList<Sarcina> listaSarcini) {
        this.listaSarcini = listaSarcini;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CheckBox checkBoxSarcinaBif;

        public MyViewHolder(final View view) {
            super(view);
            checkBoxSarcinaBif = view.findViewById(R.id.checkBoxSarcina);
        }
    }

    @NonNull
    @Override
    public AdaptorRVSarcinaBif.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_rv_lista_sarcini_bifabile,
                parent, false);
        return new AdaptorRVSarcinaBif.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptorRVSarcinaBif.MyViewHolder holder, int position) {
        String descriere = listaSarcini.get(position).getDescriereSarcina();
        holder.checkBoxSarcinaBif.setText(descriere);
        holder.checkBoxSarcinaBif.setChecked(listaSarcini.get(position).getEsteBifata());
        holder.checkBoxSarcinaBif.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    listaSarcini.get(position).setEsteBifata(true);
                } else {
                    listaSarcini.get(position).setEsteBifata(false);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaSarcini.size();
    }

    public ArrayList<Sarcina> getListaSarcini() {
        return listaSarcini;
    }
}
