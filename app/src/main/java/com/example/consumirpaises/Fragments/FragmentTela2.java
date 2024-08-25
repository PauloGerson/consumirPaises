package com.example.consumirpaises.Fragments;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.consumirpaises.Pojo.Pais;
import com.example.consumirpaises.R;
import com.example.consumirpaises.banco.DatabaseHelper;
import com.example.consumirpaises.converter.Auxiliar;
import com.example.consumirpaises.converter.Conexao;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.io.InputStream;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class FragmentTela2 extends Fragment {

    private TextInputEditText editTextNome;
    private TextInputEditText editTextNumero;
    private MaterialButton buttonConfirm;
    private MaterialButton buttonGabarito;
    private TextView textViewResult;
    private DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflar o layout do fragmento
        return inflater.inflate(R.layout.fragment_tela2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicializar as views
        editTextNome = view.findViewById(R.id.edit_text_nome);
        editTextNumero = view.findViewById(R.id.edit_text_numero);
        buttonConfirm = view.findViewById(R.id.button_confirm);
        buttonGabarito = view.findViewById(R.id.button_gabarito);
        textViewResult = view.findViewById(R.id.text_view_result);
        dbHelper = new DatabaseHelper(getContext());

        // Configurar o botão de confirmação
        buttonConfirm.setOnClickListener(v -> {
            String nome = editTextNome.getText() != null ? editTextNome.getText().toString() : "";
            String numeroStr = editTextNumero.getText() != null ? editTextNumero.getText().toString() : "";

            // Validação do nome
            if (nome.isEmpty()) {
                Toast.makeText(getContext(), "Por favor, digite seu nome", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validação do número
            if (TextUtils.isEmpty(numeroStr)) {
                Toast.makeText(getContext(), "Por favor, digite um número", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int numero = Integer.parseInt(numeroStr);

                if (numero < 5 || numero > 100) {
                    Toast.makeText(getContext(), "O número deve estar entre 5 e 100", Toast.LENGTH_SHORT).show();
                } else {
                    // Exibir o resultado
                    String resultado = "Olá " + nome + ", você escolheu o número: " + numero;
                    textViewResult.setText(resultado);

                    // Tornar o botão "Gabarito" visível
                    buttonGabarito.setVisibility(View.VISIBLE);

                    // Configurar o botão "Gabarito" para buscar e mostrar os países
                    buttonGabarito.setOnClickListener(g -> {
                        consumirApiDeBandeiras(() -> mostrarGabarito(numero));
                    });
                }

            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Por favor, insira um número válido", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarGabarito(int quantidade) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                DatabaseHelper.COLUMN_NOME,  // Nome da coluna para nome
                DatabaseHelper.COLUMN_BANDEIRA  // Nome da coluna para bandeira
        };

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_PAIS,  // Nome da tabela
                projection,
                null,
                null,
                null,
                null,
                null,
                String.valueOf(quantidade) // Limitar a quantidade de resultados
        );

        StringBuilder resultado = new StringBuilder();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String nomePais = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NOME));
                String linkBandeira = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_BANDEIRA));
                resultado.append("País: ").append(nomePais).append("\n")
                        .append("Bandeira: ").append(linkBandeira).append("\n\n");
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }

        textViewResult.setText(resultado.toString());
    }

    private void consumirApiDeBandeiras(Runnable onFinish) {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            Conexao conexao = new Conexao();
            InputStream inputStream = conexao.obterRespostaHttp("https://restcountries.com/v3.1/all?fields=name,flags");

            if (inputStream != null) {
                Gson gson = new Gson();
                String textoJSON = new Auxiliar().converter(inputStream);
                JsonElement jsonElement = gson.fromJson(textoJSON, JsonElement.class);
                JsonArray jsonArray = jsonElement.getAsJsonArray();

                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.beginTransaction();
                try {
                    for (JsonElement jsonElementPais : jsonArray) {
                        Pais pais = gson.fromJson(jsonElementPais, Pais.class);
                        if (pais != null) {
                            atualizarBandeiraNoBanco(pais);
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
            }

            requireActivity().runOnUiThread(onFinish);
        });
    }

    private void atualizarBandeiraNoBanco(Pais pais) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_BANDEIRA, pais.getFlags() != null ? pais.getFlags().getPng() : "N/A");

        String whereClause = DatabaseHelper.COLUMN_NOME + " = ?";
        String[] whereArgs = new String[]{pais.getName().getCommon()};

        db.update(DatabaseHelper.TABLE_PAIS, values, whereClause, whereArgs);
    }
}
