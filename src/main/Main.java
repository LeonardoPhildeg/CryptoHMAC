package main;



import conexao.ConnectionFactory;
import controle.UsuarioControle;

public class Main {

	public static void main(String[] args) throws Exception {
		ConnectionFactory.getConnection();
		
		UsuarioControle usu = new UsuarioControle();
		
		usu.init();
	}

}
