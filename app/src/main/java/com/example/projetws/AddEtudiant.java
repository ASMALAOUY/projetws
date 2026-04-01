package com.example.projetws;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.projetws.beans.Etudiant;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AddEtudiant extends AppCompatActivity implements View.OnClickListener {

    private EditText nom, prenom;
    private Spinner ville;
    private RadioButton m, f;
    private Button add, btnList;
    private RequestQueue requestQueue;
    private ProgressDialog progressDialog;

    private static final String insertUrl = "http://10.0.2.2/projetandr/ws/createEtudiant.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_etudiant);

        nom = findViewById(R.id.nom);
        prenom = findViewById(R.id.prenom);
        ville = findViewById(R.id.ville);
        m = findViewById(R.id.m);
        f = findViewById(R.id.f);
        add = findViewById(R.id.add);
        btnList = findViewById(R.id.btnList);

        requestQueue = Volley.newRequestQueue(this);
        add.setOnClickListener(this);
        btnList.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Ajout en cours...");
    }

    @Override
    public void onClick(View v) {
        if (v == add) {
            envoyerEtudiant();
        } else if (v == btnList) {
            Intent intent = new Intent(this, ListEtudiant.class);
            startActivity(intent);
        }
    }

    private void envoyerEtudiant() {
        if (nom.getText().toString().isEmpty() || prenom.getText().toString().isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, insertUrl,
                response -> {
                    progressDialog.dismiss();
                    Log.d("RESPONSE", response);
                    try {
                        Type type = new TypeToken<Collection<Etudiant>>(){}.getType();
                        Collection<Etudiant> etudiants = new Gson().fromJson(response, type);
                        for (Etudiant e : etudiants) {
                            Log.d("ETUDIANT", e.toString());
                        }
                        Toast.makeText(this, "Étudiant ajouté avec succès", Toast.LENGTH_SHORT).show();
                        nom.setText("");
                        prenom.setText("");
                        ville.setSelection(0);
                        m.setChecked(true);
                    } catch (Exception e) {
                        Log.e("PARSE", "Erreur parsing: " + e.getMessage());
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    Log.e("VOLLEY", "Erreur : " + error.getMessage());
                    Toast.makeText(this, "Erreur de connexion: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }) {

            @Override
            protected Map<String, String> getParams() {
                String sexe = m.isChecked() ? "homme" : "femme";
                Map<String, String> params = new HashMap<>();
                params.put("nom", nom.getText().toString());
                params.put("prenom", prenom.getText().toString());
                params.put("ville", ville.getSelectedItem().toString());
                params.put("sexe", sexe);
                return params;
            }
        };

        requestQueue.add(request);
    }
}