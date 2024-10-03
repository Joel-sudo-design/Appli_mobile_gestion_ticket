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
import com.example.appli_mobile.databinding.FragmentTicketsEnCoursBinding;
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

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTicketsEnCoursBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        recyclerView = binding.fragmentTicketsEnCoursRecyclerView;
        swipeRefreshLayout = binding.swipeRefreshLayout;
        Bundle bundle = getArguments();
        assert bundle != null;
        String username = bundle.getString("username");
        getTickets(username);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getTicketsNoBar(username);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        swipeRefreshLayout.setColorSchemeResources(
                R.color.niit
        );
        return root;
    }

    protected void getTicketsNoBar(String username) {

        JSONObject request = new JSONObject();
        final Logger logger = Logger.getLogger(fragment_tickets_en_cours.class.getName());
        try {
            request.put(KEY_USERNAME, username);

        } catch (JSONException e) {
            logger.severe(e.getMessage());
        }

        String login_url = "https://support.joeldermont.fr/in_Progress_ticket_android";
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest
                (Request.Method.POST, login_url, request, response -> {
                    try {

                        if (response.getInt(KEY_STATUS) == 1) {
                            tickets = new ArrayList<>();
                            for (int i = 0; i < response.getJSONArray("inProgressTickets").length(); i++) {
                                String id = response.getJSONArray("inProgressTickets").getJSONObject(i).getString("id");
                                String category = response.getJSONArray("inProgressTickets").getJSONObject(i).getString("category");
                                String simplePriority = response.getJSONArray("inProgressTickets").getJSONObject(i).getString("priority");
                                String priority = "priorité" + " " + simplePriority.toLowerCase();
                                String title = response.getJSONArray("inProgressTickets").getJSONObject(i).getString("title");
                                String description = response.getJSONArray("inProgressTickets").getJSONObject(i).getString("description");
                                String answer = response.getJSONArray("inProgressTickets").getJSONObject(i).getString("answer");
                                String date = response.getJSONArray("inProgressTickets").getJSONObject(i).getString("date");
                                Boolean isopen = response.getJSONArray("inProgressTickets").getJSONObject(i).getBoolean("open");
                                model model = new model(id,category, priority, title, description, answer, date, isopen);
                                tickets.add(model);
                            }
                            adapter = new adapter(tickets);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            recyclerView.setAdapter(adapter);
                        }

                        else if (response.getInt(KEY_STATUS) == 0) {
                            Toast.makeText(requireActivity().getApplicationContext(),
                                    response.getString(KEY_MESSAGE), Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        logger.severe(e.getMessage());
                    }
                }, error -> Toast.makeText(requireActivity().getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show());

        MySingleton.getInstance(this.requireActivity().getApplicationContext()).addToRequestQueue(jsArrayRequest);
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
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest
                (Request.Method.POST, login_url, request, response -> {
                    try {

                        if (response.getInt(KEY_STATUS) == 1) {
                            tickets = new ArrayList<>();
                            for (int i = 0; i < response.getJSONArray("inProgressTickets").length(); i++) {
                                String id = response.getJSONArray("inProgressTickets").getJSONObject(i).getString("id");
                                String category = response.getJSONArray("inProgressTickets").getJSONObject(i).getString("category");
                                String simplePriority = response.getJSONArray("inProgressTickets").getJSONObject(i).getString("priority");
                                String priority = "priorité" + " " + simplePriority.toLowerCase();
                                String title = response.getJSONArray("inProgressTickets").getJSONObject(i).getString("title");
                                String description = response.getJSONArray("inProgressTickets").getJSONObject(i).getString("description");
                                String answer = response.getJSONArray("inProgressTickets").getJSONObject(i).getString("answer");
                                String date = response.getJSONArray("inProgressTickets").getJSONObject(i).getString("date");
                                Boolean isopen = response.getJSONArray("inProgressTickets").getJSONObject(i).getBoolean("open");
                                model model = new model(id,category, priority, title, description, answer, date, isopen);
                                tickets.add(model);
                            }
                            adapter = new adapter(tickets);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            recyclerView.setAdapter(adapter);
                            binding.indeterminateBar.setVisibility(View.INVISIBLE);
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