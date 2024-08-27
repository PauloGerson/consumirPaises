package com.example.consumirpaises.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.consumirpaises.R;
import com.example.consumirpaises.Resultado;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;

public class FragmentTela4 extends Fragment {

    private ArrayList<String> nomesBandeirasCorretos;
    private ArrayList<TextInputEditText> inputFields;
    private Button buttonVerificar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tela4, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicializar o botão de verificação
        buttonVerificar = view.findViewById(R.id.button_verificar);
        buttonVerificar.setEnabled(false); // Desabilitar o botão inicialmente

        // Recuperar a lista de nomes das bandeiras do SharedPreferences
        SharedPreferences prefs = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String nomesBandeirasString = prefs.getString("nomes_bandeiras", null);

        if (nomesBandeirasString != null) {
            nomesBandeirasCorretos = new ArrayList<>(Arrays.asList(nomesBandeirasString.split(","))); // Converte a string de volta para uma lista
            Log.d("FragmentTela4", "Nomes das bandeiras recebidos: " + nomesBandeirasCorretos);
        } else {
            Log.d("FragmentTela4", "Não conseguiu capturar as bandeiras.");
            return; // Se não há nomes, não faz sentido continuar
        }

        inputFields = new ArrayList<>();
        LinearLayout inputContainer = view.findViewById(R.id.input_container);
        inputContainer.removeAllViews(); // Limpar qualquer campo existente antes de adicionar novos

        // Criar campos de texto dinamicamente com base no número de bandeiras
        if (nomesBandeirasCorretos != null && !nomesBandeirasCorretos.isEmpty()) {
            for (int i = 0; i < nomesBandeirasCorretos.size(); i++) {
                TextInputEditText inputField = new TextInputEditText(getContext());
                inputField.setHint("Nome da bandeira " + (i + 1) + ": ");
                inputField.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));
                inputContainer.addView(inputField);
                inputFields.add(inputField);

                // Adicionar TextWatcher para monitorar as mudanças nos campos
                inputField.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        verificarCamposPreenchidos(); // Verificar se todos os campos estão preenchidos corretamente
                    }

                    @Override
                    public void afterTextChanged(Editable s) {}
                });
            }
        }

        buttonVerificar.setOnClickListener(v -> {
            verificarRespostas(view);
            Log.d("FragmentTela4", "Botão Verificar clicado");
            Toast.makeText(getContext(), "Verificando respostas...", Toast.LENGTH_SHORT).show();
        });
    }

    private void verificarCamposPreenchidos() {
        for (TextInputEditText inputField : inputFields) {
            if (inputField.getText().toString().trim().length() < 4) {
                buttonVerificar.setEnabled(false);
                return;
            }
        }
        buttonVerificar.setEnabled(true);
    }

    private void verificarRespostas(View view) {
        LinearLayout resultsContainer = view.findViewById(R.id.results_container);
        resultsContainer.removeAllViews();

        SharedPreferences prefs = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String nomeUsuario = prefs.getString("nome_usuario", "Usuário");

        if (nomesBandeirasCorretos != null && inputFields != null) {
            DatabaseReference database = FirebaseDatabase.getInstance().getReference("resultados");

            for (int i = 0; i < nomesBandeirasCorretos.size(); i++) {
                String respostaCorreta = nomesBandeirasCorretos.get(i);
                String respostaUsuario = inputFields.get(i).getText().toString().trim();
                boolean acertou = respostaCorreta.equalsIgnoreCase(respostaUsuario);

                // Salvar o resultado no Firebase
                Resultado resultado = new Resultado(nomeUsuario, respostaUsuario, acertou);
                database.push().setValue(resultado);

                // Exibir o resultado na tela
                TextView resultView = new TextView(getContext());
                String resultadoTexto = "Correto: " + respostaCorreta + " | Sua resposta: " + respostaUsuario;
                resultadoTexto += acertou ? " ✔️" : " ❌";
                resultView.setText(resultadoTexto);
                resultsContainer.addView(resultView);
            }
        } else {
            Log.d("FragmentTela4", "Erro: nomesBandeirasCorretos ou inputFields estão nulos.");
        }
    }
}
