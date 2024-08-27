package com.example.consumirpaises;

public class Resultado {
    private String nomeUsuario;
    private String palpite;
    private boolean acertou;

    public Resultado() {}

    public Resultado(String nomeUsuario, String palpite, boolean acertou) {
        this.nomeUsuario = nomeUsuario;
        this.palpite = palpite;
        this.acertou = acertou;
    }

    // Getters e Setters
    public String getNomeUsuario() { return nomeUsuario; }
    public void setNomeUsuario(String nomeUsuario) { this.nomeUsuario = nomeUsuario; }

    public String getPalpite() { return palpite; }
    public void setPalpite(String palpite) { this.palpite = palpite; }

    public boolean isAcertou() { return acertou; }
    public void setAcertou(boolean acertou) { this.acertou = acertou; }
}
