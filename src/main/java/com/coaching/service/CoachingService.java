package com.coaching.service;

import com.coaching.exception.BusinessException;
import com.coaching.model.Seance;
import com.coaching.model.SuiviNutrition;
import com.coaching.model.SuiviProgression;
import com.coaching.model.Utilisateur;

import java.time.LocalDate;
import java.util.List;

public class CoachingService {

    public double calculerIMC(double poidsKg, double tailleCm) throws BusinessException {
        if (poidsKg <= 20 || poidsKg > 350) {
            throw new BusinessException("Le poids doit être compris entre 20 kg et 350 kg.");
        }
        if (tailleCm <= 100 || tailleCm > 250) {
            throw new BusinessException("La taille doit être comprise entre 100 cm et 250 cm.");
        }
        double tailleM = tailleCm / 100.0;
        return Math.round((poidsKg / (tailleM * tailleM)) * 100.0) / 100.0;
    }

    public int calculerObjectifCalorique(Utilisateur sportif, double dernierPoids) throws BusinessException {
        if (sportif == null) {
            throw new BusinessException("Sportif introuvable.");
        }
        double bmr = (10 * dernierPoids) + (6.25 * sportif.getTailleCm()) - (5 * sportif.getAge()) + 5;
        int tdee = (int) Math.round(bmr * 1.55);

        return switch (sportif.getObjectif()) {
            case "PERTE_POIDS" -> tdee - 400;
            case "PRISE_MASSE" -> tdee + 350;
            default -> tdee;
        };
    }

    public double calculerTauxCompletion(List<Seance> seances) {
        if (seances == null || seances.isEmpty()) return 0.0;
        long terminees = seances.stream().filter(s -> "TERMINEE".equals(s.getStatut())).count();
        return Math.round(((double) terminees / seances.size()) * 1000.0) / 10.0;
    }

    public int calculerCaloriesJour(List<SuiviNutrition> repas, LocalDate date) {
        if (repas == null) return 0;
        return repas.stream()
                .filter(r -> date.equals(r.getDateRepas()))
                .mapToInt(SuiviNutrition::getCalories)
                .sum();
    }

    public boolean detecterStagnation(List<SuiviProgression> historique, String objectif) {
        if ("MAINTIEN".equals(objectif) || historique == null || historique.size() < 3) {
            return false;
        }
        int n = historique.size();
        double p1 = historique.get(n - 3).getPoidsKg();
        double p2 = historique.get(n - 2).getPoidsKg();
        double p3 = historique.get(n - 1).getPoidsKg();
        return Math.abs(p1 - p2) < 0.2 && Math.abs(p2 - p3) < 0.2;
    }
}