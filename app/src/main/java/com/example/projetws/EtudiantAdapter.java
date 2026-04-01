package com.example.projetws;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.projetws.beans.Etudiant;
import java.util.List;

public class EtudiantAdapter extends BaseAdapter {

    private Context context;
    private List<Etudiant> etudiants;
    private LayoutInflater inflater;

    public EtudiantAdapter(Context context, List<Etudiant> etudiants) {
        this.context = context;
        this.etudiants = etudiants;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return etudiants.size();
    }

    @Override
    public Object getItem(int position) {
        return etudiants.get(position);
    }

    @Override
    public long getItemId(int position) {
        return etudiants.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_etudiant, parent, false);
            holder = new ViewHolder();
            holder.tvNom = convertView.findViewById(R.id.tvNom);
            holder.tvPrenom = convertView.findViewById(R.id.tvPrenom);
            holder.tvVille = convertView.findViewById(R.id.tvVille);
            holder.tvSexe = convertView.findViewById(R.id.tvSexe);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Etudiant e = etudiants.get(position);
        holder.tvNom.setText("Nom: " + e.getNom());
        holder.tvPrenom.setText("Prénom: " + e.getPrenom());
        holder.tvVille.setText("Ville: " + e.getVille());
        holder.tvSexe.setText("Sexe: " + e.getSexe());

        return convertView;
    }

    static class ViewHolder {
        TextView tvNom, tvPrenom, tvVille, tvSexe;
    }
}