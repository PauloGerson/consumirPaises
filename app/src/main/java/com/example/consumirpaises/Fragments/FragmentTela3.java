package com.example.consumirpaises.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.consumirpaises.R;
import com.example.consumirpaises.banco.DatabaseHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

public class FragmentTela3 extends Fragment {

    private DatabaseHelper dbHelper;
    private ArrayList<String> bandeirasList = new ArrayList<>();
    private ArrayList<String> nomesList = new ArrayList<>();
    private ViewPager2 viewPager;
    private TextView textViewNomeBandeira;
    private TextInputLayout inputLayout;
    private TextInputEditText inputText;
    private MaterialButton buttonConcluir;
    private Handler handler;
    private Runnable runnable;
    private int currentPosition = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflar o layout do fragmento
        return inflater.inflate(R.layout.fragment_tela3, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Inicializar DatabaseHelper
        dbHelper = new DatabaseHelper(getContext());

        // Recuperar o número salvo no SharedPreferences
        SharedPreferences prefs = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        int numero = prefs.getInt("numero_escolhido", 0);

        // Configurar ViewPager2 e outras views
        viewPager = getView().findViewById(R.id.view_pager_bandeiras);
        textViewNomeBandeira = getView().findViewById(R.id.text_view_nome_bandeira);
        inputLayout = getView().findViewById(R.id.input_layout);
        inputText = getView().findViewById(R.id.input_text);
        buttonConcluir = getView().findViewById(R.id.button_concluir);

        // Exibir bandeiras com base no número recuperado
        carregarBandeiras(numero);
    }

    private void carregarBandeiras(int quantidade) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = { DatabaseHelper.COLUMN_BANDEIRA, DatabaseHelper.COLUMN_NOME };

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_PAIS,
                projection,
                null,
                null,
                null,
                null,
                null,
                String.valueOf(quantidade)
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String linkBandeira = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_BANDEIRA));
                String nomeBandeira = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NOME));
                bandeirasList.add(linkBandeira);
                nomesList.add(nomeBandeira);
            } while (cursor.moveToNext());
            cursor.close();
        }

        // Configurar o ViewPager2 com o Adapter
        BandeiraAdapter adapter = new BandeiraAdapter(bandeirasList, nomesList);
        viewPager.setAdapter(adapter);

        // Configurar animação de transição
        viewPager.setPageTransformer((page, position) -> {
            page.setAlpha(0f);
            page.setVisibility(View.VISIBLE);
            page.animate()
                    .alpha(1f)
                    .setDuration(page.getResources().getInteger(android.R.integer.config_mediumAnimTime));
        });

        // Iniciar o auto-scroll das bandeiras
        handler = new Handler(Looper.getMainLooper());
        runnable = new Runnable() {
            @Override
            public void run() {
                if (currentPosition < bandeirasList.size()) {
                    viewPager.setCurrentItem(currentPosition++, true);
                    handler.postDelayed(this, 3000); // Tempo entre as transições, 3 segundos
                } else {
                    // Se chegou à última bandeira, mostrar a mensagem "Hora do jogo"
                    viewPager.setVisibility(View.GONE);
                    textViewNomeBandeira.setText("Hora do jogo!");
                    textViewNomeBandeira.setVisibility(View.VISIBLE);
                    inputLayout.setVisibility(View.VISIBLE);
                    buttonConcluir.setVisibility(View.VISIBLE);
                }
            }
        };
        handler.postDelayed(runnable, 3000); // Iniciar o auto-scroll após 3 segundos
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable); // Parar o auto-scroll quando o fragmento não está visível
    }

    // Adapter para o ViewPager2
    private class BandeiraAdapter extends RecyclerView.Adapter<BandeiraAdapter.ViewHolder> {
        private final ArrayList<String> bandeiras;
        private final ArrayList<String> nomes;

        BandeiraAdapter(ArrayList<String> bandeiras, ArrayList<String> nomes) {
            this.bandeiras = bandeiras;
            this.nomes = nomes;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bandeira, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String linkBandeira = bandeiras.get(position);
            String nomeBandeira = nomes.get(position);
            Glide.with(holder.itemView)
                    .load(linkBandeira)
                    .into(holder.imageViewBandeira);
            holder.textViewNomeBandeira.setText(nomeBandeira);
        }

        @Override
        public int getItemCount() {
            return bandeiras.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imageViewBandeira;
            TextView textViewNomeBandeira;

            ViewHolder(View itemView) {
                super(itemView);
                imageViewBandeira = itemView.findViewById(R.id.image_view_bandeira);
                textViewNomeBandeira = itemView.findViewById(R.id.text_view_nome_bandeira);
            }
        }
    }
}
