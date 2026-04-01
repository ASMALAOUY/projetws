package com.example.projetws;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.projetws.beans.Etudiant;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListEtudiant extends AppCompatActivity {

    private ListView listView;
    private List<Etudiant> etudiants;
    private EtudiantAdapter adapter;
    private RequestQueue requestQueue;
    private ProgressDialog progressDialog;

    private static final String loadUrl = "http://10.0.2.2/projetandr/ws/loadEtudiant.php";
    private static final String deleteUrl = "http://10.0.2.2/projetandr/ws/deleteEtudiant.php";
    private static final String updateUrl = "http://10.0.2.2/projetandr/ws/updateEtudiant.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_etudiant);

        listView = findViewById(R.id.listView);
        etudiants = new ArrayList<>();
        adapter = new EtudiantAdapter(this, etudiants);
        listView.setAdapter(adapter);

        requestQueue = Volley.newRequestQueue(this);
        progressDialog = new ProgressDialog(this);

        chargerEtudiants();

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            afficherOptions(position);
            return true;
        });
    }

    private void chargerEtudiants() {
        progressDialog.setMessage("Chargement...");
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.GET, loadUrl,
                response -> {
                    progressDialog.dismiss();
                    Log.d("RESPONSE", response);
                    try {
                        Type type = new TypeToken<List<Etudiant>>(){}.getType();
                        etudiants.clear();
                        etudiants.addAll(new Gson().fromJson(response, type));
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        Log.e("PARSE", "Erreur parsing: " + e.getMessage());
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    Log.e("VOLLEY", "Erreur : " + error.getMessage());
                    Toast.makeText(this, "Erreur de chargement", Toast.LENGTH_SHORT).show();
                });

        requestQueue.add(request);
    }

    private void afficherOptions(int position) {
        final Etudiant etudiant = etudiants.get(position);

        String[] options = {"Modifier", "Supprimer"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Options pour " + etudiant.getNom() + " " + etudiant.getPrenom());
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                afficherDialogModification(etudiant);
            } else if (which == 1) {
                confirmerSuppression(etudiant);
            }
        });
        builder.show();
    }

    private void confirmerSuppression(final Etudiant etudiant) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation");
        builder.setMessage("Voulez-vous vraiment supprimer " + etudiant.getNom() + " " + etudiant.getPrenom() + " ?");
        builder.setPositiveButton("Oui", (dialog, which) -> supprimerEtudiant(etudiant));
        builder.setNegativeButton("Non", null);
        builder.show();
    }

    private void supprimerEtudiant(final Etudiant etudiant) {
        progressDialog.setMessage("Suppression...");
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, deleteUrl,
                response -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Étudiant supprimé", Toast.LENGTH_SHORT).show();
                    chargerEtudiants();
                },
                error -> {
                    progressDialog.dismiss();
                    Log.e("VOLLEY", "Erreur : " + error.getMessage());
                    Toast.makeText(this, "Erreur de suppression", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(etudiant.getId()));
                return params;
            }
        };

        requestQueue.add(request);
    }

    private void afficherDialogModification(final Etudiant etudiant) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Modifier l'étudiant");

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_etudiant, null);
        builder.setView(dialogView);

        final EditText editNom = dialogView.findViewById(R.id.editNom);
        final EditText editPrenom = dialogView.findViewById(R.id.editPrenom);
        final Spinner editVille = dialogView.findViewById(R.id.editVille);
        final RadioButton editM = dialogView.findViewById(R.id.editM);
        final RadioButton editF = dialogView.findViewById(R.id.editF);

        editNom.setText(etudiant.getNom());
        editPrenom.setText(etudiant.getPrenom());

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.villes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editVille.setAdapter(adapter);

        String ville = etudiant.getVille();
        String[] villes = getResources().getStringArray(R.array.villes);
        for (int i = 0; i < villes.length; i++) {
            if (villes[i].equals(ville)) {
                editVille.setSelection(i);
                break;
            }
        }

        if (etudiant.getSexe().equals("homme")) {
            editM.setChecked(true);
        } else {
            editF.setChecked(true);
        }

        builder.setPositiveButton("Modifier", null);
        builder.setNegativeButton("Annuler", null);

        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(v -> {
                String sexe = editM.isChecked() ? "homme" : "femme";
                modifierEtudiant(etudiant.getId(), editNom.getText().toString(),
                        editPrenom.getText().toString(), editVille.getSelectedItem().toString(), sexe);
                dialog.dismiss();
            });
        });

        dialog.show();
    }

    private void modifierEtudiant(int id, String nom, String prenom, String ville, String sexe) {
        progressDialog.setMessage("Modification...");
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, updateUrl,
                response -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Étudiant modifié", Toast.LENGTH_SHORT).show();
                    chargerEtudiants();
                },
                error -> {
                    progressDialog.dismiss();
                    Log.e("VOLLEY", "Erreur : " + error.getMessage());
                    Toast.makeText(this, "Erreur de modification", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(id));
                params.put("nom", nom);
                params.put("prenom", prenom);
                params.put("ville", ville);
                params.put("sexe", sexe);
                return params;
            }
        };

        requestQueue.add(request);
    }
}