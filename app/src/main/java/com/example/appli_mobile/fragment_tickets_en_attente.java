package com.example.appli_mobile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.appli_mobile.databinding.FragmentTicketsEnAttenteBinding;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.logging.Logger;

public class fragment_tickets_en_attente extends Fragment {

    private FragmentTicketsEnAttenteBinding binding;
    private SwipeRefreshLayout swipeRefreshLayout;
    ArrayList<model> tickets;
    RecyclerView recyclerView;
    adapter adapter;
    private static final String KEY_USERNAME = "username";
    private static final String KEY_STATUS = "status";
    private static final String KEY_MESSAGE = "message";

    public fragment_tickets_en_attente() {
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTicketsEnAttenteBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        recyclerView = binding.fragmentTicketsEnAttenteRecyclerView;
        swipeRefreshLayout = binding.swipeRefreshLayout;
        Bundle bundle = getArguments();
        assert bundle != null;
        String username = bundle.getString("username");
        getTickets(username);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Créer une nouvelle instance du fragment et lui passer le même argument
                fragment_tickets_en_attente newFragment = new fragment_tickets_en_attente();
                Bundle newBundle = new Bundle();
                newBundle.putString("username", username);
                newFragment.setArguments(newBundle);

                // Remplacer le fragment actuel par la nouvelle instance
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.frame_layout, newFragment)
                        .commit();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        swipeRefreshLayout.setColorSchemeResources(
                R.color.niit
        );
        return root;
    }

    public void filter(String query) {
        if (tickets != null) {
            adapter.getFilter().filter(query);
        }
    }

    protected void getTickets(String username) {

        binding.indeterminateBar.setVisibility(View.VISIBLE);
        JSONObject request = new JSONObject();
        final Logger logger = Logger.getLogger(fragment_tickets_en_attente.class.getName());
        try {
            request.put(KEY_USERNAME, username);

        } catch (JSONException e) {
            logger.severe(e.getMessage());
        }

        String login_url = "https://support.joeldermont.fr/waiting_ticket_android";
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest
                (Request.Method.POST, login_url, request, response -> {
                    try {
                        if (response.getInt(KEY_STATUS) == 1) {
                            tickets = new ArrayList<>();
                            for (int i = 0; i < response.getJSONArray("waitingTickets").length(); i++) {
                                JSONObject ticketObj = response.getJSONArray("waitingTickets").getJSONObject(i);
                                String id = ticketObj.getString("id");
                                String category = ticketObj.getString("category");
                                String simplePriority = ticketObj.getString("priority");
                                String priority = "priorité " + simplePriority.toLowerCase();
                                String title = ticketObj.getString("title");
                                String description = ticketObj.getString("description");
                                String date = ticketObj.getString("date");
                                boolean isopen = ticketObj.getBoolean("open");

                                // Traitement du tableau "answers"
                                JSONArray answersArray = ticketObj.getJSONArray("answers");
                                String adminAnswer = "";
                                String userAnswer = "";
                                for (int j = 0; j < answersArray.length(); j++) {
                                    JSONObject answerObj = answersArray.getJSONObject(j);
                                    if (answerObj.has("admin")) {
                                        adminAnswer = answerObj.getString("admin");
                                    }
                                    if (answerObj.has("user")) {
                                        userAnswer = answerObj.getString("user");
                                    }
                                }
                                String answer = "Admin: " + adminAnswer + "\nUser: " + userAnswer;

                                // Création de l'objet modèle avec les réponses concaténées
                                model model = new model(id, category, priority, title, description, answer, date, isopen);
                                tickets.add(model);
                                binding.indeterminateBar.setVisibility(View.INVISIBLE);
                            }
                            adapter = new adapter(tickets);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            recyclerView.setAdapter(adapter);
                        }

                        else if (response.getInt(KEY_STATUS) == 0) {
                            Toast.makeText(requireActivity().getApplicationContext(),
                                    response.getString(KEY_MESSAGE), Toast.LENGTH_SHORT).show();
                            binding.indeterminateBar.setVisibility(View.INVISIBLE);
                        }

                    } catch (JSONException e) {
                        logger.severe(e.getMessage());
                    }
                }, error -> Toast.makeText(requireActivity().getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show());

        MySingleton.getInstance(this.requireActivity().getApplicationContext()).addToRequestQueue(jsArrayRequest);
    }

    public void recentTickets() {
        adapter.recentTickets();
    }

    public void oldTickets() {
        adapter.oldTickets();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}