package com.example.appli_mobile;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.appli_mobile.databinding.ActivityLoginBinding;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Objects;
import java.util.logging.Logger;


public class activity_login extends AppCompatActivity {

    private static final String KEY_STATUS = "status";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_EMPTY = "";
    private String username;
    private String password;
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.indeterminateBar.setVisibility(View.INVISIBLE);
        binding.btnForgotPassword.setOnClickListener(v -> {
            binding.btnForgotPassword.setTextColor(Color.parseColor("#a8efff"));
            String url = "https://support.joeldermont.fr/reset-password";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        });
        binding.btnLoginRegister.setOnClickListener(v -> {
            binding.btnLoginRegister.setTextColor(Color.parseColor("#a8efff"));
            String url = "https://support.joeldermont.fr/register";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        });
        binding.btnLogin.setOnClickListener(v -> {
            binding.btnLogin.setBackgroundColor(Color.parseColor("#a8efff"));
            //Retrieve the data entered in the edit texts
            username = Objects.requireNonNull(binding.LoginUsername.getText()).toString().toLowerCase().trim();
            password = Objects.requireNonNull(binding.LoginPassword.getText()).toString().trim();
            if (validateInputs()) {
                binding.indeterminateBar.setVisibility(View.VISIBLE);
                login();
            }
        });
    }
    private void loadMain(String token) {
        Intent i = new Intent(getApplicationContext(), activity_main.class);
        i.putExtra("username", username);
        i.putExtra("token", token);
        startActivity(i);
        finish();
    }

    private void login() {
        JSONObject request = new JSONObject();
        final Logger logger = Logger.getLogger(activity_login.class.getName());
        try {
            request.put(KEY_USERNAME, username);
            request.put(KEY_PASSWORD, password);

        } catch (JSONException e) {
            logger.severe(e.getMessage());
        }
        String login_url = "https://support.joeldermont.fr/api/login";
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest
                (Request.Method.POST, login_url, request, response -> {
                    try {
                        if (response.has("token")) {
                            String token = response.getString("token");
                            loadMain(token);
                        } else if (response.has("error")) {
                            Toast.makeText(getApplicationContext(),
                                    response.getString("error"), Toast.LENGTH_SHORT).show();
                            binding.indeterminateBar.setVisibility(View.INVISIBLE);
                            binding.btnLogin.setBackgroundColor(Color.WHITE);
                        }
                    } catch (JSONException e) {
                        logger.severe(e.getMessage());
                    }
                },
                        error -> {
                            String errorMessage = error.getMessage();
                            if (errorMessage == null || errorMessage.isEmpty()) {
                                errorMessage = "Une erreur s'est produite";
                            }
                            Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
                        }
                );
        MySingleton.getInstance(this).addToRequestQueue(jsArrayRequest);
    }
    private boolean validateInputs() {
        if(KEY_EMPTY.equals(username)){
            binding.LoginUsername.setError("Veuillez entrer votre nom d'utilisateur");
            binding.LoginUsername.requestFocus();
            return false;
        }
        if(KEY_EMPTY.equals(password)){
            binding.LoginPassword.setError("Veuillez entrer votre mot de passe");
            binding.LoginPassword.requestFocus();
            return false;
        }
        return true;
    }
}
