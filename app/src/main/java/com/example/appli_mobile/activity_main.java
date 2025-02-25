package com.example.appli_mobile;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AutoCompleteTextView;
import androidx.appcompat.widget.SearchView;
import android.widget.TextView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.appli_mobile.databinding.ActivityMainBinding;
import java.util.Objects;

public class activity_main extends AppCompatActivity {

    String[] items = {"Plus récent", "Plus ancien"};
    AutoCompleteTextView autoCompleteText;
    fragment_tickets_en_attente fragmentTicketsEnAttente = new fragment_tickets_en_attente();
    fragment_tickets_en_cours fragmentTicketsEnCours = new fragment_tickets_en_cours();
    fragment_tickets_resolus fragmentTicketsResolus = new fragment_tickets_resolus();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.example.appli_mobile.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        NavigationView navigationView = binding.navView;
        DrawerLayout drawer = binding.drawerLayout;
        MaterialToolbar toolbar = findViewById(R.id.toolbar);

        // Récupération des données de l'Intent (username et token)
        Intent i = getIntent();
        String username = i.getStringExtra("username");
        String token = i.getStringExtra("token");

        // Toolbar configuration
        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.toolbar.setTitleCentered(true);
        Drawable filter = ResourcesCompat.getDrawable(getResources(), R.drawable.filter, null);
        toolbar.setOverflowIcon(filter);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, binding.appBarMain.toolbar, R.string.open_drawer, R.string.close_drawer);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Filter configuration
        autoCompleteText = findViewById(R.id.autoCompleteText);
        adapter_filter adapter = new adapter_filter(this, items);
        autoCompleteText.setAdapter(adapter);

        // Affichage du fragment par défaut avec le Bundle contenant username et token
        TextView ticket = findViewById(R.id.ticket);
        if (savedInstanceState == null) {
            autoCompleteText.setOnItemClickListener((parent, view, position, id) -> {
                String itemFilter = parent.getItemAtPosition(position).toString();
                if (fragmentTicketsEnAttente.adapter != null) {
                    if (itemFilter.equals("Plus récent")) {
                        fragmentTicketsEnAttente.recentTickets();
                    } else if (itemFilter.equals("Plus ancien")) {
                        fragmentTicketsEnAttente.oldTickets();
                    }
                }
            });
            ticket.setText(R.string.tickets_en_attente_main);
            Objects.requireNonNull(getSupportActionBar()).setTitle("Bienvenue " + username);
            Bundle bundle = new Bundle();
            bundle.putString("username", username);
            bundle.putString("token", token); // Transmission du token
            fragment_tickets_en_attente fragmentTicketsEnAttente = new fragment_tickets_en_attente();
            fragmentTicketsEnAttente.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragmentTicketsEnAttente).commit();
            navigationView.setCheckedItem(R.id.nav_tickets_en_attente);
        }

        // Navigation Drawer : passage du Bundle avec username et token aux fragments sélectionnés
        navigationView.setNavigationItemSelectedListener(item -> {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            Bundle bundle = new Bundle();
            bundle.putString("username", username);
            bundle.putString("token", token); // Ajout du token dans le Bundle

            if (item.getItemId() == R.id.nav_tickets_en_attente) {
                ticket.setText(R.string.tickets_en_attente_main);
                fragment_tickets_en_attente newFragment = new fragment_tickets_en_attente();
                newFragment.setArguments(bundle);
                transaction.replace(R.id.frame_layout, newFragment).commit();
                drawer.closeDrawers();
                autoCompleteText.setText("");
            } else if (item.getItemId() == R.id.nav_tickets_en_cours) {
                ticket.setText(R.string.tickets_en_cours_main);
                fragment_tickets_en_cours newFragment = new fragment_tickets_en_cours();
                newFragment.setArguments(bundle);
                transaction.replace(R.id.frame_layout, newFragment).commit();
                drawer.closeDrawers();
                autoCompleteText.setText("");
            } else if (item.getItemId() == R.id.nav_tickets_resolus) {
                ticket.setText(R.string.tickets_r_solus_main);
                fragment_tickets_resolus newFragment = new fragment_tickets_resolus();
                newFragment.setArguments(bundle);
                transaction.replace(R.id.frame_layout, newFragment).commit();
                drawer.closeDrawers();
                autoCompleteText.setText("");
            } else if (item.getItemId() == R.id.nav_btnLogout) {
                Intent i1 = new Intent(activity_main.this, activity_login.class);
                startActivity(i1);
                finish();
            }
            return true;
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_search) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        assert searchView != null;
        searchView.setQueryHint("Recherche");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterFragment(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterFragment(newText);
                return false;
            }
        });
        return true;
    }

    private void filterFragment(String query) {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_layout);
        if (currentFragment instanceof fragment_tickets_en_attente){
            ((fragment_tickets_en_attente) currentFragment).filter(query);
        }
        if (currentFragment instanceof fragment_tickets_en_cours) {
            ((fragment_tickets_en_cours) currentFragment).filter(query);
        }
        if (currentFragment instanceof fragment_tickets_resolus) {
            ((fragment_tickets_resolus) currentFragment).filter(query);
        }
    }
}