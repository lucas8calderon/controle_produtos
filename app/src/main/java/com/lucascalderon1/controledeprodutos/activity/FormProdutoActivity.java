package com.lucascalderon1.controledeprodutos.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.lucascalderon1.controledeprodutos.helper.FirebaseHelper;
import com.lucascalderon1.controledeprodutos.model.Produto;
import com.lucascalderon1.controledeprodutos.R;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.security.Permission;
import java.util.List;

public class FormProdutoActivity extends AppCompatActivity {

    private ImageButton ib_voltar;
    private ImageView imagem_produto;
    private EditText edit_produto, edit_quantidade, edit_valor;
    private static final int REQUEST_GALERIA = 100;
    private String caminhoImagem;
    private Bitmap imagem;

    private Produto produto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_produto);


        iniciaComponentes();

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            produto = (Produto) bundle.getSerializable("produto");

            editProduto();

        }


    }

    private void editProduto() {
        edit_produto.setText(produto.getNome());
        edit_quantidade.setText(String.valueOf(produto.getEstoque()));
        edit_valor.setText(String.valueOf(produto.getValor()));

        Picasso.get().load(produto.getUrlImagem()).into(imagem_produto);
    }


    public void verificaPermissaoGaleria(View view) {
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                openGaleria();


            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(FormProdutoActivity.this, "Permissão negada", Toast.LENGTH_SHORT).show();

            }
        };

        showDialogPermissao(permissionListener, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE});

    }

    private void openGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_GALERIA);

    }

    private void showDialogPermissao(PermissionListener listener, String[] permissoes) {
        TedPermission.create()
                .setPermissionListener(listener)
                .setDeniedMessage("Permissões")
                .setDeniedMessage("Você permite que o aplicativo tenha acesso a sua galeria?")
                .setDeniedCloseButtonText("Não")
                .setGotoSettingButtonText("Sim")
                .setPermissions(permissoes)
                .check();


    }


    public void salvarProduto(View view) {


        String nome = edit_produto.getText().toString();
        String quantidade = edit_quantidade.getText().toString();
        String valor = edit_valor.getText().toString();

        if (!nome.isEmpty()) {
            if (!quantidade.isEmpty()) {

                int qtd = Integer.parseInt(quantidade);

                if (qtd >= 1) {

                    if (!valor.isEmpty()) {
                        double valorProduto = Double.parseDouble(valor);

                        if (valorProduto > 0) {

                            if (produto == null) produto = new Produto();
                            produto.setNome(nome);
                            produto.setEstoque(qtd);
                            produto.setValor(valorProduto);

                            if (caminhoImagem == null) {
                                Toast.makeText(this, "Selecione uma imagem", Toast.LENGTH_SHORT).show();

                            } else {

                                salvarImagemProduto();

                            }

                            Toast.makeText(this, "Produto cadastrado com sucesso!", Toast.LENGTH_SHORT).show();

                            finish();


                        } else {

                            edit_valor.requestFocus();
                            edit_valor.setError("Informe um valor maior que 0.");

                        }


                    } else {
                        edit_valor.requestFocus();
                        edit_valor.setError("Informe o valor do produto.");
                    }

                } else {

                    edit_quantidade.requestFocus();
                    edit_quantidade.setError("Informe um valor maior que 0.");

                }

            } else {

                edit_quantidade.requestFocus();
                edit_quantidade.setError("Informe a quantidade do produto.");

            }

        } else {
            edit_produto.requestFocus();
            edit_produto.setError("Informe o nome do produto.");

        }

    }

    private void salvarImagemProduto() {
        StorageReference reference = FirebaseHelper.getStorageReference()
                .child("imagens")
                .child("produtos")
                .child(FirebaseHelper.getIdFirebase())
                .child(produto.getId() + ".jpeg");

        UploadTask uploadTask = reference.putFile(Uri.parse(caminhoImagem));
        uploadTask.addOnSuccessListener(taskSnapshot -> reference.getDownloadUrl().addOnCompleteListener(task -> {
            produto.setUrlImagem(task.getResult().toString());
            produto.salvarProduto();

            finish();

        })).addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
    }


    private void iniciaComponentes() {
        edit_produto = findViewById(R.id.edit_produto);
        edit_quantidade = findViewById(R.id.edit_quantidade);
        edit_valor = findViewById(R.id.edit_valor);
        ib_voltar = findViewById(R.id.ib_voltar);
        imagem_produto = findViewById(R.id.imagem_produto);

        ib_voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_GALERIA) {

                Uri localImagemSelecionada = data.getData();
                caminhoImagem = localImagemSelecionada.toString();

                if (Build.VERSION.SDK_INT < 28) {

                    try {
                        imagem = MediaStore.Images.Media.getBitmap(getBaseContext().getContentResolver(), localImagemSelecionada);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                } else {

                    ImageDecoder.Source source = ImageDecoder.createSource(getBaseContext().getContentResolver(), localImagemSelecionada);

                    try {

                        imagem = ImageDecoder.decodeBitmap(source);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    imagem_produto.setImageBitmap(imagem);


                }


            }

        }


    }


}