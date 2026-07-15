package app.dao;

import app.utils.DBConnection;
import app.model.User;
import app.utils.PasswordHasher;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    // AUTHENTIFICATION

    public User authentificate(String emailSaisi, String code_permanentSaisi) {
        String query = "SELECT * FROM users WHERE email = ?"; 
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, emailSaisi);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String correctpassword = rs.getString("code_permanent");
                    String hashSaisie = PasswordHasher.hashSHA256(code_permanentSaisi);
                    // On compare les deux empreintes de hachage
                    if (correctpassword != null && correctpassword.equals(hashSaisie)) {
                        // Si les empreintes correspondent, l'authentification est validée !
                        // On instancie l'objet User pour le renvoyer au contrôleur
                        User user = new User();
                        user.setId(rs.getInt("id"));
                        user.setCode_permanent(rs.getInt("code_permanent"));
                        user.setNom(rs.getString("nom"));
                        user.setPrenom(rs.getString("prenom"));
                        user.setEmail(rs.getString("email"));
                        user.setRole(rs.getString("role"));
                        // Récupération des clés étrangères optionnelles
                        int filiereId = rs.getInt("filiere_id");
                        user.setFiliere_id(rs.wasNull() ? null : filiereId);
                        user.setNiveau(rs.getString("niveau"));                        
                        return user; // Succès
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Échec de l'authentification (mauvais login ou mauvais mot de passe)
    }

    // ===================== CRUD  ==============================

    // AJOUTER UN UTILISATUR (CREATE)

    public void addUser(User u, String code_permanent) throws SQLException{
        String sql = "INSERT INTO users (code_permanent ,nom, prenom, email, role, filiere_id, niveau)"
        +"VALUES (?, ?, ?, ?, ?, ?, ?)";
        String password = PasswordHasher.hashSHA256(code_permanent);

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, password);
            ps.setString(2, u.getNom());
            ps.setString(3, u.getPrenom());
            ps.setString(4, u.getEmail());
            ps.setString(5, u.getRole());
            // Gestion des champs optionnels qui peuvent etre NULL
            if (u.getFiliere_id() != null){
                ps.setInt(6, u.getFiliere_id());
            }
            else {ps.setNull(6,  Types.INTEGER);}

            if (u.getNiveau() != null){
                ps.setString(7, u.getNiveau());
            }
            else {ps.setNull(7,  Types.VARCHAR);}

            if (ps.executeUpdate() ==  0) throw new SQLException("Echec de l'nsertion ");
            else System.err.println("Succés : Utilisateur ajouter avec dans la base de données");
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    //RECHERCHER UN UTILISATEUR PAR SON email (READ)

    public User searchUser (String email){
        String sql = "SELECT * FROM users WHERE email= ?";
        try  (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){  
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()){
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setCode_permanent(rs.getInt("code_permanent"));
                user.setNom(rs.getString("nom"));
                user.setPrenom(rs.getString("prenom"));
                user.setEmail(rs.getString("email"));
                user.setRole(rs.getString("role"));
                // Récupération des clés étrangères optionnelles
                int filiereId = rs.getInt("filiere_id");
                user.setFiliere_id(rs.wasNull() ? null : filiereId);
                user.setNiveau(rs.getString("niveau"));                        
                return user; // Succès
                }          
            } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    //RECUPERER TOUS LES UTILISATEURS (READ)

    public List<User> getAllUsers(){
        List<User> liste = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try ( Connection conn = DBConnection.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)){
            while (rs.next()){
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setCode_permanent(rs.getInt("code_permanent"));
                u.setNom(rs.getString("nom"));
                u.setPrenom(rs.getString("prenom"));
                u.setEmail(rs.getString("email"));
                u.setRole(rs.getString("role"));
                u.setFiliere_id(rs.getInt("filiere_id"));
                u.setNiveau(rs.getString("niveau"));
                liste.add(u);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    //MODIFIER UN UTILISATEUR (UPDATE)



    //SUPPRIMER UN UTILISATEUR (DELETE)

    public void deleteUser(int email) {
        String sql = "DELETE FROM users WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, email);
            if (ps.executeUpdate() ==  0) throw new SQLException("Echec lors de la suppression");
            else System.err.println("Succes: Utilisateur supprimer dans la base de données ");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
}
