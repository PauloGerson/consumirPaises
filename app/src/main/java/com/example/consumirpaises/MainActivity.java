package com.example.consumirpaises;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.consumirpaises.Fragments.FragmentInicio;
import com.example.consumirpaises.Fragments.FragmentTela2;
import com.example.consumirpaises.Fragments.FragmentTela3;
import com.example.consumirpaises.Fragments.FragmentTela4;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.Firebase;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);

        FragmentStateAdapter pagerAdapter = new ScreenSlidePagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Início");
                    break;
                case 1:
                    tab.setText("Tela 2");
                    break;
                case 2:
                    tab.setText("Tela 3");
                    break;
                case 3:
                    tab.setText("Tela 4");
                    break;
            }
        }).attach();
    }

    private class ScreenSlidePagerAdapter extends FragmentStateAdapter {
        public ScreenSlidePagerAdapter(FragmentActivity fa) {
            super(fa);
        }

        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new FragmentInicio();  // Fragmento "Início"
                case 1:
                    return new FragmentTela2();  // Fragmento "Tela 2"
                case 2:
                    return new FragmentTela3();  // Fragmento "Tela 3"
                case 3:
                    return new FragmentTela4();  // Fragmento "Tela 4"
                default:
                    return new FragmentInicio();  // Default para evitar erros
            }
        }

        @Override
        public int getItemCount() {
            return 4;  // Número de tabs/fragments
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_inicio) {
            viewPager.setCurrentItem(0, true);  // Navegar para a aba "Início"
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
