package com.example.consumirpaises.converter;

import android.util.Log;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Conexao {

    public InputStream obterRespostaHttp(String endereco) {
        try {
            URL url = new URL(endereco);
            HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
            conexao.setRequestMethod("GET");

            // Verifica se a resposta é 200 (HTTP_OK)
            if (conexao.getResponseCode() == HttpURLConnection.HTTP_OK) {
                return new BufferedInputStream(conexao.getInputStream());
            } else {
                // Log para resposta HTTP não esperada
                Log.e("Conexao", "Resposta HTTP não OK: " + conexao.getResponseCode());
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
