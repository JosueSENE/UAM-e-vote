package app.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public final class PasswordHasher {

    // Constantes pour la sécurité
    private static final String ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16;
    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final int MAX_PASSWORD_LENGTH = 64;

    /**
     * Méthode statique qui prend une chaîne de caractères en entrée 
     * et retourne son empreinte numérique (hash) SHA-256.
     *
     * @param password Le texte brut à hacher
     * @return Le hash SHA-256 de 64 caractères, ou null si l'entrée est nulle
     */
    public static String hashSHA256(String password) {
        // Validation de sécurité
        if (password == null) {
            return null;
        }
        
        try {
            MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
            
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erreur lors de l'accès à l'algorithme de hachage " + ALGORITHM, e);
        }
    }

    /**
     * Vérifie si un mot de passe correspond à son hash
     * 
     * @param password Mot de passe en clair
     * @param hashedPassword Hash stocké en base de données
     * @return true si le mot de passe correspond au hash
     */
    public static boolean verifyPassword(String password, String hashedPassword) {
        if (password == null || hashedPassword == null) {
            return false;
        }
        String hash = hashSHA256(password);
        return hash != null && hash.equals(hashedPassword);
    }

    /**
     * Vérifie la force d'un mot de passe
     * 
     * @param password Mot de passe à vérifier
     * @return Un objet contenant le niveau de force et les messages
     */
    public static PasswordStrength checkPasswordStrength(String password) {
        if (password == null || password.isEmpty()) {
            return new PasswordStrength(0, "Mot de passe vide", false);
        }

        int score = 0;
        StringBuilder feedback = new StringBuilder();

        // Critère 1 : Longueur
        if (password.length() >= 12) {
            score += 3;
            feedback.append("✅ Longueur excellente (≥ 12 caractères)\n");
        } else if (password.length() >= 8) {
            score += 2;
            feedback.append("✅ Longueur bonne (≥ 8 caractères)\n");
        } else if (password.length() >= 6) {
            score += 1;
            feedback.append("⚠️ Longueur minimale (6 caractères)\n");
        } else {
            feedback.append("❌ Trop court (< 6 caractères)\n");
        }

        // Critère 2 : Présence de majuscules
        if (password.matches(".*[A-Z].*")) {
            score += 1;
            feedback.append("✅ Contient des majuscules\n");
        } else {
            feedback.append("❌ Aucune majuscule\n");
        }

        // Critère 3 : Présence de minuscules
        if (password.matches(".*[a-z].*")) {
            score += 1;
            feedback.append("✅ Contient des minuscules\n");
        } else {
            feedback.append("❌ Aucune minuscule\n");
        }

        // Critère 4 : Présence de chiffres
        if (password.matches(".*\\d.*")) {
            score += 1;
            feedback.append("✅ Contient des chiffres\n");
        } else {
            feedback.append("❌ Aucun chiffre\n");
        }

        // Critère 5 : Présence de caractères spéciaux
        if (password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) {
            score += 2;
            feedback.append("✅ Contient des caractères spéciaux\n");
        } else {
            feedback.append("⚠️ Aucun caractère spécial\n");
        }

        // Déterminer le niveau
        boolean isValid = password.length() >= MIN_PASSWORD_LENGTH;
        String level;
        if (score >= 8) {
            level = "Fort";
        } else if (score >= 5) {
            level = "Moyen";
        } else if (score >= 3) {
            level = "Faible";
        } else {
            level = "Très faible";
            isValid = false;
        }

        return new PasswordStrength(score, level, feedback.toString(), isValid);
    }

    /**
     * Génère un mot de passe aléatoire sécurisé
     * 
     * @param length Longueur du mot de passe (entre 8 et 20)
     * @return Mot de passe aléatoire
     */
    public static String generateRandomPassword(int length) {
        if (length < 8) length = 8;
        if (length > 20) length = 20;

        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(chars.length());
            sb.append(chars.charAt(index));
        }

        return sb.toString();
    }

    /**
     * Valide un mot de passe selon les règles de sécurité
     * 
     * @param password Mot de passe à valider
     * @return true si le mot de passe est valide
     */
    public static boolean isValidPassword(String password) {
        if (password == null) return false;
        if (password.length() < MIN_PASSWORD_LENGTH) return false;
        if (password.length() > MAX_PASSWORD_LENGTH) return false;
        
        // Au moins une majuscule
        if (!password.matches(".*[A-Z].*")) return false;
        // Au moins une minuscule
        if (!password.matches(".*[a-z].*")) return false;
        // Au moins un chiffre
        if (!password.matches(".*\\d.*")) return false;
        
        return true;
    }

    /**
     * Classe interne pour le résultat de la vérification de force
     */
    public static class PasswordStrength {
        private final int score;
        private final String level;
        private final String feedback;
        private final boolean valid;

        public PasswordStrength(int score, String level, String feedback, boolean valid) {
            this.score = score;
            this.level = level;
            this.feedback = feedback;
            this.valid = valid;
        }

        public PasswordStrength(int score, String level, boolean valid) {
            this(score, level, "", valid);
        }

        public int getScore() { return score; }
        public String getLevel() { return level; }
        public String getFeedback() { return feedback; }
        public boolean isValid() { return valid; }

        @Override
        public String toString() {
            return String.format("Force: %s (Score: %d/10) - %s", level, score, valid ? "Valide" : "Invalide");
        }
    }

    /**
     * Constructeur privé pour empêcher l'instanciation
     */
    private PasswordHasher() {}
}