package com.coaching.service;

import com.coaching.model.SuiviProgression;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ExportService {

    public void exporterProgressionCSV(File fichier, List<SuiviProgression> mesures) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fichier, StandardCharsets.UTF_8))) {
            writer.write("Date;Poids_kg;Tour_taille_cm;Commentaire");
            writer.newLine();
            for (SuiviProgression m : mesures) {
                String commentaire = m.getCommentaire() != null ? m.getCommentaire().replace(";", ",") : "";
                writer.write(m.getDateMesure() + ";" + m.getPoidsKg() + ";" + m.getTourTailleCm() + ";" + commentaire);
                writer.newLine();
            }
        }
    }
}