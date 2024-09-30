package com.example.appli_mobile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import java.util.logging.Logger;

public class adapter extends RecyclerView.Adapter<adapter.ViewHolder> implements Filterable {

    private final ArrayList<model> tickets;
    private final ArrayList<model> ticketsFiltered;
    private static final String KEY_ID = "id";
    private static final String KEY_SUCCESS = "success";
    private int expandedPosition = -1;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView ticketNumber, category, title, description, answer, date,  isopen;
        private final View divider;
        private final ImageView priority;
        private final ImageView btnArrow;
        private final RelativeLayout itemClicked;

        public ViewHolder(View view) {
            super(view);
            ticketNumber = view.findViewById(R.id.ticketNumber);
            category = view.findViewById(R.id.category);
            priority = view.findViewById(R.id.priority);
            title = view.findViewById(R.id.title);
            description = view.findViewById(R.id.description);
            answer = view.findViewById(R.id.answer);
            btnArrow = view.findViewById(R.id.btnArrow);
            divider = view.findViewById(R.id.divider);
            itemClicked = view.findViewById(R.id.itemClicked);
            date = view.findViewById(R.id.date);
            isopen = view.findViewById(R.id.newTicket);
        }
    }

    public adapter(ArrayList<model> tickets) {
        this.tickets = tickets;
        this.ticketsFiltered = new ArrayList<>(tickets);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                ArrayList<model> filterResults = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    filterResults.addAll(ticketsFiltered);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (model item : ticketsFiltered) {
                        if (item.getDescription().toLowerCase().contains(filterPattern)) {
                            filterResults.add(item);
                        }
                        if (item.getTitle().toLowerCase().contains(filterPattern)) {
                            filterResults.add(item);
                        }
                        if (item.getCategory().toLowerCase().contains(filterPattern)) {
                            filterResults.add(item);
                        }
                        if (item.getTicketNumber().toLowerCase().contains(filterPattern)) {
                            filterResults.add(item);
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.values = filterResults;
                return results;
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                tickets.clear();
                tickets.addAll((ArrayList<model>) results.values);
                notifyDataSetChanged();
            }
        };
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.modele_view, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull adapter.ViewHolder viewholder, int position) {
        Context context = viewholder.itemView.getContext();
        model model = tickets.get(position);
        viewholder.ticketNumber.setText(model.getTicketNumber());
        viewholder.category.setText(model.getCategory());
        viewholder.title.setText(model.getTitle());
        viewholder.description.setText(model.getDescription());
        viewholder.answer.setText(model.getAnswer());
        viewholder.date.setText(model.getDate());

        boolean isExpanded = position == expandedPosition == !com.example.appli_mobile.model.isExpanded();
        viewholder.description.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        viewholder.answer.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        if (!isExpanded) {
            viewholder.btnArrow.setImageResource(R.drawable.arrow_up);
        } else {
            viewholder.btnArrow.setImageResource(R.drawable.arrow_down);
        }
        viewholder.itemClicked.setOnClickListener(v -> {
            setIsopen(model.getTicketNumber(), context);
            viewholder.isopen.setVisibility(View.GONE);
            expandedPosition = isExpanded ? -1 : position;
            notifyDataSetChanged();
        });

        if (Objects.equals(model.getPriority(), "priorité haute")) {
            viewholder.priority.setVisibility(View.VISIBLE);
        }
        if (Objects.equals(model.getPriority(), "priorité basse")) {
            viewholder.priority.setVisibility(View.GONE);
        }
        if (model.getIsopen()) {
            viewholder.isopen.setVisibility(View.GONE);
        }
        if (model.getAnswer().isEmpty()) {
            viewholder.answer.setVisibility(View.GONE);
            viewholder.divider.setVisibility(View.GONE);
        }
    }

    private void setIsopen(String id, Context context) {

        JSONObject request = new JSONObject();
        final Logger logger = Logger.getLogger(adapter.class.getName());
        try {
            request.put(KEY_ID, id);

        } catch (JSONException e) {
            logger.severe(e.getMessage());
        }

        String login_url = "https://151.80.59.103/open_ticket_android";
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest
                (Request.Method.POST, login_url, request, response -> {
                    try {
                        if (response.getString(KEY_SUCCESS).equals("Ticket ouvert avec succès")) {
                            Log.i("Success", response.getString(KEY_SUCCESS));
                        }
                    } catch (JSONException e) {
                        logger.severe(e.getMessage());
                    }
                }, error -> Toast.makeText(context,
                        error.getMessage(), Toast.LENGTH_SHORT).show());

        MySingleton.getInstance(context).addToRequestQueue(jsArrayRequest);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void recentTickets() {
        this.tickets.sort(new Comparator<model>() {
            @Override
            public int compare(model o1, model o2) {
                return o2.getTicketNumber().compareTo(o1.getTicketNumber());
            }
        });
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void oldTickets() {
        this.tickets.sort(new Comparator<model>() {
            @Override
            public int compare(model o1, model o2) {
                return o1.getTicketNumber().compareTo(o2.getTicketNumber());
            }
        });
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return tickets.size();
    }

}

