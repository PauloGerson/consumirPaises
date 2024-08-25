package com.example.consumirpaises.converter;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Auxiliar {
    public String converter(InputStream inputStream) {
        if (inputStream == null) {
            // Se o inputStream for nulo, retorna uma string vazia ou mensagem de erro
            Log.e("Auxiliar", "InputStream nulo recebido na conversão.");
            return "";
        }

        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        StringBuilder stringBuilder = new StringBuilder();
        String conteudo = "";

        try {
            while ((conteudo = bufferedReader.readLine()) != null) {
                stringBuilder.append(conteudo).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stringBuilder.toString();
    }
}
