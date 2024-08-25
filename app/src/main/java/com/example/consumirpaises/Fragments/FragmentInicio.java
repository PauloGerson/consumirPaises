package com.example.consumirpaises.Fragments;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.example.consumirpaises.Pojo.Pais;
import com.example.consumirpaises.R;
import com.example.consumirpaises.banco.DatabaseHelper;
import com.example.consumirpaises.converter.Auxiliar;
import com.example.consumirpaises.converter.Conexao;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.io.InputStream;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class FragmentInicio extends Fragment {

    private TextView textView;
    private LottieAnimationView loadingAnimation;
    private LinearLayout loadingContainer;
    private ScrollView scrollView;
    private DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflar o layout do fragmento
        return inflater.inflate(R.layout.fragment_inicio, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicializar as views do layout
        textView = view.findViewById(R.id.textView);
        loadingAnimation = view.findViewById(R.id.loading_animation);
        loadingContainer = view.findViewById(R.id.loading_container);
        scrollView = view.findViewById(R.id.scroll_view);

        dbHelper = new DatabaseHelper(getContext());

        showLoading(true);

        // Adicionar um pequeno delay para garantir que a animação seja carregada corretamente
        new Handler().postDelayed(() -> {
            if (isInternetAvailable()) {
                fetchDataFromAPI();
            } else {
                exibirDadosSalvos();
            }
        }, 500); // Delay de 500 ms antes de iniciar a operação de rede
    }

    private void fetchDataFromAPI() {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            Conexao conexao = new Conexao();
            InputStream inputStream = conexao.obterRespostaHttp("https://restcountries.com/v3.1/all");

            if (inputStream != null) {
                Auxiliar auxiliar = new Auxiliar();
                String textoJSON = auxiliar.converter(inputStream);
                Log.i("DEbug", "API" + textoJSON);

                Gson gson = new Gson();
                JsonElement jsonElement = gson.fromJson(textoJSON, JsonElement.class);
                JsonArray jsonArray = jsonElement.getAsJsonArray();

                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.beginTransaction();  // Iniciar transação
                try {
                    for (JsonElement jsonElementPais : jsonArray) {
                        Pais pais = gson.fromJson(jsonElementPais, Pais.class);
                        if (pais != null) {
                            salvarPaisNoBanco(pais);
                        }
                    }
                    db.setTransactionSuccessful();  // Confirma transação
                } finally {
                    db.endTransaction();  // Finaliza transação
                }

                requireActivity().runOnUiThread(this::exibirDadosSalvos);
            } else {
                requireActivity().runOnUiThread(this::exibirDadosSalvos);
            }
        });
    }

    private void salvarPaisNoBanco(Pais pais) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_NOME, pais.getName() != null ? pais.getName().getCommon() : "N/A");
            values.put(DatabaseHelper.COLUMN_CAPITAL, pais.getCapital() != null && !pais.getCapital().isEmpty() ? pais.getCapital().get(0) : "N/A");
            values.put(DatabaseHelper.COLUMN_IDIOMA, pais.getLanguages() != null && pais.getLanguages().getEng() != null ? pais.getLanguages().getEng() : "N/A");
            values.put(DatabaseHelper.COLUMN_REGIAO, pais.getRegion() != null ? pais.getRegion() : "N/A");
            if (pais.getPopulation() != null) {
                NumberFormat formatter = NumberFormat.getNumberInstance(Locale.getDefault());
                String formattedPopulation = formatter.format(pais.getPopulation());
                values.put(DatabaseHelper.COLUMN_POPULACAO, formattedPopulation);
            } else {
                values.put(DatabaseHelper.COLUMN_POPULACAO, "N/A");
            }

            db.insert(DatabaseHelper.TABLE_PAIS, null, values);
            db.setTransactionSuccessful(); // Confirma a transação
        } finally {
            db.endTransaction(); // Finaliza a transação
        }
    }

    private void exibirDadosSalvos() {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String[] projection = {
                    DatabaseHelper.COLUMN_NOME,
                    DatabaseHelper.COLUMN_CAPITAL,
                    DatabaseHelper.COLUMN_IDIOMA,
                    DatabaseHelper.COLUMN_REGIAO,
                    DatabaseHelper.COLUMN_POPULACAO,
                    DatabaseHelper.COLUMN_BANDEIRA  // Adicionando a coluna bandeira aqui
            };

            Cursor cursor = db.query(
                    DatabaseHelper.TABLE_PAIS,
                    projection,
                    null,
                    null,
                    null,
                    null,
                    null
            );

            StringBuilder stringBuilder = new StringBuilder();

            if (cursor != null && cursor.moveToFirst()) {
                int batchSize = 20; // Exiba 20 itens por vez
                int currentCount = 0;

                do {
                    String nome = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NOME));
                    String capital = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CAPITAL));
                    String idioma = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_IDIOMA));
                    String regiao = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_REGIAO));
                    String populacao = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_POPULACAO));

                    stringBuilder.append("<b>Nome:</b> ").append(nome).append("<br>");
                    stringBuilder.append("<b>Capital:</b> ").append(capital).append("<br>");
                    stringBuilder.append("<b>Idioma:</b> ").append(idioma).append("<br>");
                    stringBuilder.append("<b>Região:</b> ").append(regiao).append("<br>");
                    stringBuilder.append("<b>População:</b> ").append(populacao).append("<br>");
                    stringBuilder.append("").append("").append("<br><br>");
                    currentCount++;

                    // Exibir em lotes
                    if (currentCount % batchSize == 0) {
                        final String partialData = stringBuilder.toString();
                        requireActivity().runOnUiThread(() -> textView.append(Html.fromHtml(partialData, Html.FROM_HTML_MODE_COMPACT)));
                        stringBuilder.setLength(0); // Limpa o buffer para o próximo lote
                    }

                } while (cursor.moveToNext());

                // Exibir o restante
                if (stringBuilder.length() > 0) {
                    final String remainingData = stringBuilder.toString();
                    requireActivity().runOnUiThread(() -> textView.append(Html.fromHtml(remainingData, Html.FROM_HTML_MODE_COMPACT)));
                }
            } else {
                requireActivity().runOnUiThread(() -> textView.setText("Nenhum dado disponível. Conecte-se à internet para baixar dados."));
            }

            if (cursor != null) {
                cursor.close();
            }

            requireActivity().runOnUiThread(() -> showLoading(false));
        });
    }

    private void showLoading(boolean isLoading) {
        if (isLoading) {
            loadingContainer.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.GONE);

            loadingAnimation.setRepeatCount(LottieDrawable.INFINITE);
            loadingAnimation.playAnimation();

        } else {
            loadingContainer.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);
        }
    }

    private boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
