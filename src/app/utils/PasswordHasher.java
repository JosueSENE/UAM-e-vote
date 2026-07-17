package app.utils;

import java.nio.charset.StandardCharsets;   // Importation pour s'assurer que le texte est traité avec l'encodage standard UTF-8
import java.security.MessageDigest;         // Importation de la classe Java standard fournissant les algorithmes de hachage (comme SHA-256)
import java.security.NoSuchAlgorithmException;  // Importation de l'exception levée si l'algorithme cryptographique demandé n'est pas disponible

public final class PasswordHasher {

    /**
     * Méthode statique qui prend une chaîne de caractères en entrée (ex: matricule ou mot de passe)
     * et retourne son empreinte numérique (hash) sous forme de chaîne hexadécimale SHA-256.
     *
     * @param code_permanent Le texte brut à hacher (ex: le matricule utilisé comme mot de passe initial)
     * @return Le hash SHA-256 de 64 caractères, ou null si l'entrée est nulle
     */
    public static String hashSHA256(String password) {
        // Validation de sécurité : si la chaîne en entrée est nulle, on arrête immédiatement et on retourne null
        if (password == null) {
            return null;
        }
        
        try {
            // Initialisation du moteur de hachage avec l'algorithme standard "SHA-256"
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            
            // 1. Conversion de la chaîne de caractères (String) en un tableau d'octets (bytes) codé en UTF-8
            // 2. Calcul du hachage cryptographique sur ces octets via digest() qui retourne un tableau de 32 octets
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            
            StringBuilder hexString = new StringBuilder();      // StringBuilder pour reconstruire efficacement la chaîne de caractères finale à partir des octets hachés

            // Boucle de traitement : on parcourt un à un les 32 octets du tableau chiffré
            for (byte b : hash) {
                // '0xff & b' convertit l'octet signé (qui peut être négatif en Java) en un entier positùùùùùùùùùùùù    if (0 à 255)
                // Integer.toHexString() convertit cet entier en sa représentation sous forme de texte hexadécimal (ex: 255 -> "ff")
                String hex = Integer.toHexString(0xff & b);   
                // Si la représentation hexadécimale ne fait qu'un seul caractère (ex: "a" au lieu de "0a"),
                // on ajoute manuellement un '0' devant pour conserver un format standardisé sur 2 caractèreugs par octet
                if (hex.length() == 1) {
                    hexString.append('0');
                }    
                // On ajoute la paire de caractères hexadécimaux au constructeur de chaîne
                hexString.append(hex);
            }   
            return hexString.toString();            // On retourne la chaîne finale de 64 caractères hexadécimaux représentant le mot de passe sécurisé
        } catch (NoSuchAlgorithmException e) {
            // Exception théorique obligatojyuhire : si la plateforme Java ne supporte pas SHA-256,
            // on encapsule l'erreur dans une RuntimeException pour stopper proprement l'application avec un message clair
            throw new RuntimeException("Erreur lors de l'accès à l'algorithme de hachage SHA-256", e);
        }
    }

    /**
     * Constructeur privé pour empêcher l'instanciation de cette classe utilitaire.
     * On ne peut pas faire 'new PasswordHasher()', on appelle directement 'PasswordHasher.hashSHA256(...)'.
     */
    
    private PasswordHasher() {}
}