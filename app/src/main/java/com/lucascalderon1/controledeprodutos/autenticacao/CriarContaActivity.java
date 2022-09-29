package com.lucascalderon1.controledeprodutos.autenticacao;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lucascalderon1.controledeprodutos.R;
import com.lucascalderon1.controledeprodutos.activity.MainActivity;
import com.lucascalderon1.controledeprodutos.helper.FirebaseHelper;
import com.lucascalderon1.controledeprodutos.model.Usuario;

public class CriarContaActivity extends AppCompatActivity {

    private EditText edit_nome, edit_email, edit_senha;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_conta);

        iniciaComponentes();
        configCliques();




    }

    private void configCliques() {
        findViewById(R.id.ib_voltar).setOnClickListener(view -> finish());
    }



    public void validaDados(View view) {
        String nome = edit_nome.getText().toString();
        String email = edit_email.getText().toString();
        String senha = edit_senha.getText().toString();


        if (!nome.isEmpty()) {

            if (!email.isEmpty()) {

                if (!senha.isEmpty()) {

                    progressBar.setVisibility(View.VISIBLE);

                    Usuario usuario = new Usuario();
                    usuario.setNome(nome);
                    usuario.setEmail(email);
                    usuario.setSenha(senha);

                    salvarCadastro(usuario);



                } else  {
                    edit_senha.requestFocus();
                    edit_senha.setError("Informe sua senha");

                }

            } else  {
                edit_email.requestFocus();
                edit_email.setError("Informe seu e-mail");

            }


        } else  {
            edit_nome.requestFocus();
            edit_nome.setError("Informe seu nome");

        }


    }


    private void salvarCadastro(Usuario usuario) {
        FirebaseHelper.getAuth().createUserWithEmailAndPassword(
                usuario.getEmail(), usuario.getSenha()
        ).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {

                String id = task.getResult().getUser().getUid();
                usuario.setId(id);

                finish();
                startActivity(new Intent(this, MainActivity.class));
                Toast.makeText(this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show();




            }

        });
    }

    private void iniciaComponentes() {
        edit_email = findViewById(R.id.edit_email);
        edit_nome = findViewById(R.id.edit_nome);
        edit_senha = findViewById(R.id.edit_senha);
        progressBar = findViewById(R.id.progressBar);


        TextView text_titulo = findViewById(R.id.text_titulo);
        text_titulo.setText("Criar conta");
    }

}