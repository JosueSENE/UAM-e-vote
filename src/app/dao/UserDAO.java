package app.dao;

import app.utils.DBConnection;
import app.model.User;
import app.utils.PasswordHasher;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    // AUTHENTIFICATION
    public void authentificate(String email, int code_permanent){
        String password = PasswordHasher.hashSHA256(code_permanent);
        String sql = "SELECT * FROM users WHERE email = ? AND code_permanent = ?";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, email);
            ps.setString(2, password);


            }

    }

    // AJOUTER Un UTILISATUR (CREATE)

    public void addUser(User u) throws SQLException{
        String sql = "INSERT INTO users (code_permanent ,nom, prenom, email, role, filiere_id, niveau, ufr_id)"
        +"VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String password = PasswordHasher.hashSHA256(u.getCode_permanent());

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

            if (u.getUfr_id() != null){
                ps.setInt(8, u.getUfr_id());
            }
            else {ps.setNull(8,  Types.INTEGER);}

            if (ps.executeUpdate() ==  0) throw new SQLException("Echec de l'nsertion ");
            else System.err.println("Succés : Utilisateur ajouter avec dans la base de données");
        }catch(SQLException e){
            e.printStackTrace();
        }
    }


    //SUPPRIMER UN UTILISATEUR

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
    //RECUPERER TOUS LES UTILISATEURS

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
                u.setUfr_id(rs.getInt("ufr_id"));
                liste.add(u);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }
    
}
