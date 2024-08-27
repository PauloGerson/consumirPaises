package com.example.consumirpaises.banco;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "paises.db";
    private static final int DATABASE_VERSION = 3;  // Incrementado para refletir a nova estrutura

    public static final String TABLE_PAIS = "pais";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NOME = "nome";
    public static final String COLUMN_CAPITAL = "capital";
    public static final String COLUMN_IDIOMA = "idioma";
    public static final String COLUMN_REGIAO = "regiao";
    public static final String COLUMN_POPULACAO = "populacao";
    public static final String COLUMN_BANDEIRA = "bandeira";  // Coluna para o link da bandeira

    public static final String TABLE_NUMERO = "numero";  // Nova tabela para armazenar o número escolhido
    public static final String COLUMN_NUMERO_ESCOLHIDO = "numero_escolhido";

    private static final String TABLE_PAIS_CREATE =
            "CREATE TABLE " + TABLE_PAIS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NOME + " TEXT, " +
                    COLUMN_CAPITAL + " TEXT, " +
                    COLUMN_IDIOMA + " TEXT, " +
                    COLUMN_REGIAO + " TEXT, " +
                    COLUMN_POPULACAO + " TEXT, " +
                    COLUMN_BANDEIRA + " TEXT" +
                    ");";

    // SQL para criar a nova tabela que vai armazenar o número escolhido
    private static final String TABLE_NUMERO_CREATE =
            "CREATE TABLE " + TABLE_NUMERO + " (" +
                    COLUMN_NUMERO_ESCOLHIDO + " INTEGER" +
                    ");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_PAIS_CREATE);
        db.execSQL(TABLE_NUMERO_CREATE);  // Criando a nova tabela na criação do banco de dados
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_PAIS + " ADD COLUMN " + COLUMN_BANDEIRA + " TEXT;");
        }
        if (oldVersion < 3) {
            db.execSQL(TABLE_NUMERO_CREATE);  // Criando a nova tabela se a versão for atualizada
        }
    }
}
