package dao;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import conexao.ConnectionFactory;

public class UsuarioDAO {

	
	public void create(String login, String senha, String sal) {
        Connection con = ConnectionFactory.getConnection();
        
        PreparedStatement stmt = null;
        
        try {
            stmt = con.prepareStatement("INSERT INTO Usuario (login,senha,sal)VALUES(?,?,?)");
            stmt.setString(1, login);
            stmt.setString(2, senha);
            stmt.setString(3, sal);
            stmt.executeUpdate();

        } catch (SQLException ex) {
            System.out.println(ex);
        } finally {
            ConnectionFactory.closeConnection(con, stmt);
        }
	}
	

	//PEGA O SAL DO USUÁRIO INFORMADO, CASO ESTEJA NO BANCO
	public String getSal(String login) {
        Connection con = ConnectionFactory.getConnection();
        
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            stmt = con.prepareStatement("SELECT sal FROM Usuario WHERE login = ?");
            stmt.setString(1, login);
            rs = stmt.executeQuery();
            
            if(!rs.next()) {
            	return null;
            }
            
         return rs.getString(1); 

        } catch (SQLException ex) {
            Logger.getLogger(UsuarioDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            ConnectionFactory.closeConnection(con, stmt, rs);
        }
        return null;
	}

	
	//VERIFICA SE A SENHA CONSULTADA É IGUAL A QUE FOI INFORMADA PELO USUÁRIO
	public boolean validarSenhaDoUsuario(String login, String senha) {
        Connection con = ConnectionFactory.getConnection();
        
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            stmt = con.prepareStatement("SELECT senha FROM Usuario WHERE login = ?");
            stmt.setString(1, login);
            rs = stmt.executeQuery();
            
            if(!rs.next()) {
            	return false;
            }
            
            String senhaConsultada = rs.getString(1);
            return senha.equals(senhaConsultada); 

        } catch (SQLException ex) {
            Logger.getLogger(UsuarioDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            ConnectionFactory.closeConnection(con, stmt, rs);
        }
        return false;
            
	}


	public boolean updateUsuario(String login, String novoLogin, String novaSenha, String novoSal) {
        Connection con = ConnectionFactory.getConnection();
        
        PreparedStatement stmt = null;
        
        try {
            stmt = con.prepareStatement("UPDATE Usuario SET login = ?, senha = ?, sal = ? WHERE login = ?");
            stmt.setString(1, novoLogin);
            stmt.setString(2, novaSenha);
            stmt.setString(3, novoSal);
            stmt.setString(4, login);
            stmt.executeUpdate();
            return true;
            
        } catch (SQLException ex) {
            System.out.println(ex);
        } finally {
            ConnectionFactory.closeConnection(con, stmt);
        }
		return false;
	}
	
	public boolean deleteUsuario(String login) {
        Connection con = ConnectionFactory.getConnection();
        
        PreparedStatement stmt = null;
        
        try {
            stmt = con.prepareStatement("DELETE FROM Usuario WHERE login = ?");
            stmt.setString(1, login);
            stmt.executeUpdate();
            return true;

        } catch (SQLException ex) {
            System.out.println(ex);
        } finally {
            ConnectionFactory.closeConnection(con, stmt);
        }
        return false;
	}
	
	
}	
	
	
	
