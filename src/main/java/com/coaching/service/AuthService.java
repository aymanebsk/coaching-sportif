package com.coaching.service;

import com.coaching.config.DatabaseConnection;
import com.coaching.exception.BusinessException;
import com.coaching.model.Utilisateur;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthService {

    public static String hashPassword(String plainPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(plainPassword.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(2 * encodedHash.length);
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 indisponible", e);
        }
    }

    public Utilisateur authenticate(String email, String motDePasseClair) throws BusinessException {
        if (email == null || email.isBlank() || motDePasseClair == null || motDePasseClair.isBlank()) {
            throw new BusinessException("Veuillez saisir votre e-mail et votre mot de passe.");
        }

        String hash = hashPassword(motDePasseClair.trim());
        String sql = "SELECT * FROM utilisateur WHERE email = ? AND mot_de_passe_hash = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email.trim().toLowerCase());
            stmt.setString(2, hash);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Utilisateur u = new Utilisateur();
                    u.setId(rs.getInt("id"));
                    u.setNom(rs.getString("nom"));
                    u.setEmail(rs.getString("email"));
                    u.setRole(rs.getString("role"));
                    u.setAge(rs.getInt("age"));
                    u.setTailleCm(rs.getDouble("taille_cm"));
                    u.setObjectif(rs.getString("objectif"));
                    return u;
                }
            }
            throw new BusinessException("E-mail ou mot de passe incorrect.");
        } catch (SQLException e) {
            throw new BusinessException("Erreur SQL : " + e.getMessage());
        }
    }
}