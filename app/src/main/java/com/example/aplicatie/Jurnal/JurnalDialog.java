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

public class JurnalDialog extends AppCompatDialogFragment {
    private EditText etParolaJurnal;
    private Button btnInchideJurnal, btnIntraInJurnal;

    private JurnalDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_jurnal_parola, null);

        builder.setView(view)
                .setTitle("Introduceți parola pentru jurnal");

        Dialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(view.getContext().getDrawable(R.drawable.dialog_albastru));
        dialog.setCanceledOnTouchOutside(false);

        etParolaJurnal = view.findViewById(R.id.etParolaJurnal);
        btnInchideJurnal = view.findViewById(R.id.btnInchideJ);
        btnIntraInJurnal = view.findViewById(R.id.btnSalveazaJ);

        btnIntraInJurnal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String paroloa = etParolaJurnal.getText().toString();
                listener.applyTexts(paroloa);
            }
        });

        btnInchideJurnal.setOnClickListener(new View.OnClickListener() {
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
            listener = (JurnalDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "trebuie implementata interfata");
        }
    }

    public interface JurnalDialogListener{
        void applyTexts(String parola);
    }
}
