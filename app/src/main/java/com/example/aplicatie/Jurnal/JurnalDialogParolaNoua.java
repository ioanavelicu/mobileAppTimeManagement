package com.example.aplicatie.Jurnal;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.aplicatie.R;

public class JurnalDialogParolaNoua extends AppCompatDialogFragment {
    private EditText etParolaNouaJurnal;
    private Button btnInchideJurnalNou, btnSalveazaJurnalNou;

    private JurnalDialogParolaNouaListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_jurnal_parola_noua, null);

        builder.setView(view)
                .setTitle("Setați parola pentru jurnal");

        Dialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(view.getContext().getDrawable(R.drawable.dialog_albastru));
        dialog.setCanceledOnTouchOutside(false);

        etParolaNouaJurnal = view.findViewById(R.id.etParolaNouaJurnal);
        btnSalveazaJurnalNou = view.findViewById(R.id.btnSalveazaJurnalNou);
        btnInchideJurnalNou = view.findViewById(R.id.btnInchideJurnalNou);

        btnSalveazaJurnalNou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String paroloa = etParolaNouaJurnal.getText().toString();
                listener.adaugaParolaJurnal(paroloa);
            }
        });

        btnInchideJurnalNou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        return dialog;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try{
            listener = (JurnalDialogParolaNoua.JurnalDialogParolaNouaListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "trebuie implementata interfata");
        }
    }

    public interface JurnalDialogParolaNouaListener{
        void adaugaParolaJurnal(String parola);
    }
}

