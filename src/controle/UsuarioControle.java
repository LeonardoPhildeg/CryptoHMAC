package controle;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Scanner;

import crypto.Crypto;
import dao.UsuarioDAO;

public class UsuarioControle {

	
	private UsuarioDAO dao;
	private Crypto crypto;
	
	
	public UsuarioControle() {
		this.dao = new UsuarioDAO();
		this.crypto = Crypto.getInstance();
	}
	
	
	public void init() throws Exception {
		System.out.println("Seja bem-vindo ao melhor sistema de autenticação de usuário de São Bonifácio \n");
		System.out.println("Escolha uma das oções abaixo:");
		System.out.println("1 - Cadastro de usuário");
		System.out.println("2 - Autenticação de usuário");
		System.out.println("3 - Alteração de dados do usuário");
		System.out.println("4 - Exclusão de usuário \n");
		
		Scanner scan = new Scanner(System.in);
		int opcao = scan.nextInt();
		
		switch (opcao) {
		case 1:
			criarUsuario();
		
		case 2:
			efetuarLogin();
			
		case 3:
			updateUsuario();
			
		case 4:
			deleteUsuario();
			
			break;

		default:
			break;
		}
		
	}
	

	public void criarUsuario() throws Exception {
		Scanner scan = new Scanner(System.in);
		
		System.out.println("----- CADASTRO DE USUÁRIO -----");
		System.out.println("Insira o seu novo login para cadastro");
		String login = scan.next();
		login = crypto.toSha256(login);
		
		String sal = crypto.generateSal();
		
		System.out.println("Insira sua senha");
		String senha = scan.next();
		
		senha = crypto.hMAC(senha, sal);
		dao.create(login, senha, sal);
		
		this.init();
	}
	
	public boolean efetuarLogin() throws Exception {
		Scanner scan = new Scanner(System.in);

		System.out.println("----- AUTENTICAÇÃO DE USUÁRIO -----");
		System.out.println("Insira seu login");
		String login = scan.next();
		
		System.out.println("Insira sua senha");
		String senha = scan.next();

		boolean usuarioAutenticado = autenticar(login,senha);
		
		if(usuarioAutenticado) {
			System.out.println("Usuário autenticado!! \n");
			this.init();
			return true;
		}
		this.init();
		return false;
	}


	private boolean autenticar(String login, String senha) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException {
		//LOGIN PARA SHA-256 E PROCURA O SAL DESSE USUÁRIO
		login = crypto.toSha256(login);
		String sal = dao.getSal(login);
		
		if(sal == null) {
			System.out.println("Usuário não consta na base de dados");
			return false;
		}
		
		//REALIZA O HMAC DA SENHA PARA VALIDAÇÃO COM O USUÁRIO
		senha = crypto.hMAC(senha, sal);
		boolean dadosValidos = dao.validarSenhaDoUsuario(login,senha);
		
		if(!dadosValidos) {
			System.out.println("Usuário e/ou senha inválidos \n");
			return false;
		}
		return true;
	}
	
	
	private void updateUsuario() throws Exception {
		// TODO Auto-generated method stub
		
		this.init();
	}


	private void deleteUsuario() throws Exception {
		// TODO Auto-generated method stub
		this.init();
	}
}
