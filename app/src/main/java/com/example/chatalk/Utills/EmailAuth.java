package com.example.chatalk.Utills;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.chatalk.R;

public class EmailAuth extends DialogFragment {
    private String pass;
    EditText password;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.email_auth,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Authenticate gmail account");
        builder.setView(view);
        password = view.findViewById(R.id.passwordEmail);
        builder.setNegativeButton("Cancel",null);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                pass = password.getText().toString();
            }
        });
        return builder.create();
    }
    public String getPass(){
        return pass;
    }

}
