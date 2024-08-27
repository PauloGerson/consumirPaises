package com.example.consumirpaises;

import android.app.Application;
import android.util.Log;

import com.google.firebase.FirebaseApp;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Inicialize o Firebase
        FirebaseApp.initializeApp(this);
        Log.d("MyApplication", "Firebase initialized");

    }
}
