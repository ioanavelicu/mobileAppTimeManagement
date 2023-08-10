package com.example.aplicatie.Sarcina;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.aplicatie.R;

public class SarcinaDialog extends AppCompatDialogFragment {
    private EditText etSarcinaNoua;
    private Button btnInchideSarcina, btnSalveazaSarcina;

    private SracinaDialogListener listener;

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_adauga_sarcina, null);

        ((Activity) view.getContext()).getWindow().setBackgroundDrawable(view.getContext().getDrawable(R.drawable.dialog_albastru));

        builder.setView(view);

        etSarcinaNoua = view.findViewById(R.id.etSarcinaNoua);
        btnInchideSarcina = view.findViewById(R.id.btnInchideSarcina);
        btnSalveazaSarcina = view.findViewById(R.id.btnSalveazaSarcina);

        Dialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(view.getContext().getDrawable(R.drawable.dialog_albastru));
        dialog.setCanceledOnTouchOutside(false);

        btnSalveazaSarcina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String descriereSarcina = etSarcinaNoua.getText().toString();
                listener.applyTexts(descriereSarcina);
                dialog.dismiss();
            }
        });

        btnInchideSarcina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        return dialog;
    }

    public interface SracinaDialogListener {
        void applyTexts(String descriereSarcina);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try{
            listener = (SracinaDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "trebuie implementata interfata");
        }
    }
}
