package com.lucascalderon1.controledeprodutos.autenticacao;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.lucascalderon1.controledeprodutos.R;
import com.lucascalderon1.controledeprodutos.helper.FirebaseHelper;

import kotlin.jvm.internal.PropertyReference0Impl;

public class RecuperarContaActivity extends AppCompatActivity {

    private EditText edit_email;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_conta);

        iniciaComponentes();
    }

    public void recuperarSenha(View view) {
        String email = edit_email.getText().toString().trim();


        if (!email.isEmpty()) {

            progressBar.setVisibility(View.VISIBLE);
            enviarEmail(email);

        } else  {
            edit_email.requestFocus();
            edit_email.setError("Informe seu e-mail.");
        }

    }

    private void enviarEmail(String email) {
        FirebaseHelper.getAuth().sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "E-mal enviado com sucesso!", Toast.LENGTH_LONG).show();

            } else {
                
                String error = task.getException().getMessage();
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();

            }
            progressBar.setVisibility(View.GONE);
        });

    }

    private void iniciaComponentes() {
        edit_email = findViewById(R.id.edit_email);
        progressBar = findViewById(R.id.progressBar);



    }



}