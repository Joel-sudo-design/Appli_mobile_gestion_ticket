package com.example.appli_mobile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.appli_mobile.databinding.FragmentTicketsEnCoursBinding;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.logging.Logger;

public class fragment_tickets_en_cours extends Fragment {

    private FragmentTicketsEnCoursBinding binding;
    private SwipeRefreshLayout swipeRefreshLayout;
    ArrayList<model> tickets;
    RecyclerView recyclerView;
    adapter adapter;
    private static final String KEY_USERNAME = "username";
    private static final String KEY_STATUS = "status";
    private static final String KEY_MESSAGE = "message";

    public fragment_tickets_en_cours() {
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTicketsEnCoursBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        recyclerView = binding.fragmentTicketsEnCoursRecyclerView;
        swipeRefreshLayout = binding.swipeRefreshLayout;
        AutoCompleteTextView autoCompleteText = requireActivity().findViewById(R.id.autoCompleteText);
        autoCompleteText.setOnItemClickListener((parent, view1, position, id) -> {
            String itemFilter = parent.getItemAtPosition(position).toString();
            if (adapter != null) {
                if (itemFilter.equals("Plus récent")) {
                    recentTickets();
                } else if (itemFilter.equals("Plus ancien")) {
                    oldTickets();
                }
            }
        });
        Bundle bundle = getArguments();
        assert bundle != null;
        String username = bundle.getString("username");
        getTickets(username);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Créer une nouvelle instance du fragment et lui passer le même argument
                fragment_tickets_en_cours newFragment = new fragment_tickets_en_cours();
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

    protected void getTickets(String username) {

        binding.indeterminateBar.setVisibility(View.VISIBLE);
        JSONObject request = new JSONObject();
        final Logger logger = Logger.getLogger(fragment_tickets_en_cours.class.getName());
        try {
            request.put(KEY_USERNAME, username);
        } catch (JSONException e) {
            logger.severe(e.getMessage());
        }

        String login_url = "https://support.joeldermont.fr/in_Progress_ticket_android";
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(
                Request.Method.POST,
                login_url,
                request,
                response -> {
                    try {
                        if (response.getInt(KEY_STATUS) == 1) {
                            tickets = new ArrayList<>();
                            JSONArray inProgressTickets = response.getJSONArray("inProgressTickets");
                            for (int i = 0; i < inProgressTickets.length(); i++) {
                                JSONObject ticketObj = inProgressTickets.getJSONObject(i);
                                String id = ticketObj.getString("id");
                                String category = ticketObj.getString("category");
                                String simplePriority = ticketObj.getString("priority");
                                String priority = "priorité " + simplePriority.toLowerCase();
                                String title = ticketObj.getString("title");
                                String description = ticketObj.getString("description");
                                String date = ticketObj.getString("date");
                                boolean isopen = ticketObj.getBoolean("open");

                                // Traitement du tableau "answers" avec format HTML
                                JSONArray answersArray = ticketObj.getJSONArray("answers");
                                StringBuilder answerBuilder = new StringBuilder();
                                for (int j = 0; j < answersArray.length(); j++) {
                                    JSONObject answerObj = answersArray.getJSONObject(j);
                                    if (answerObj.has("admin") && !answerObj.getString("admin").isEmpty()) {
                                        answerBuilder.append("<b>Admin:</b> ")
                                                .append(answerObj.getString("admin"))
                                                .append("<br/><br/>");
                                    }
                                    if (answerObj.has("user") && !answerObj.getString("user").isEmpty()) {
                                        answerBuilder.append("<b>User:</b> ")
                                                .append(answerObj.getString("user"))
                                                .append("<br/><br/>");
                                    }
                                }
                                String answer = answerBuilder.toString().trim();

                                // Création de l'objet modèle avec la chaîne answer formatée
                                model modelObj = new model(id, category, priority, title, description, answer, date, isopen);
                                tickets.add(modelObj);
                            }
                            binding.indeterminateBar.setVisibility(View.INVISIBLE);
                            adapter = new adapter(tickets);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            recyclerView.setAdapter(adapter);
                        } else if (response.getInt(KEY_STATUS) == 0) {
                            Toast.makeText(requireActivity().getApplicationContext(),
                                    response.getString(KEY_MESSAGE), Toast.LENGTH_SHORT).show();
                            binding.indeterminateBar.setVisibility(View.INVISIBLE);
                        }
                    } catch (JSONException e) {
                        logger.severe(e.getMessage());
                    }
                },
                error -> Toast.makeText(requireActivity().getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show());

        MySingleton.getInstance(this.requireActivity().getApplicationContext()).addToRequestQueue(jsArrayRequest);
    }

    public void filter(String query) {
        if (tickets != null) {
            adapter.getFilter().filter(query);
        }
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