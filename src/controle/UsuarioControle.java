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
		//REALIZA O HASH DO USUÁRIO
		String login = scan.next();
		login = crypto.toSha256(login);
		
		//GERA O SAL
		String sal = crypto.generateSal();
		
		System.out.println("Insira sua senha");
		String senha = scan.next();
		
		//FAZ O HMAC UTILIZANDO SENHA E SAL
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
			System.out.println("USUÁRIO AUTENTICADO COM SUCESSO! \n");
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
			System.out.println("USUÁRIO NÃO CONSTA NA BASE DE DADOS.");
			return false;
		}
		
		//REALIZA O HMAC DA SENHA PARA VALIDAÇÃO COM O USUÁRIO
		senha = crypto.hMAC(senha, sal);
		boolean dadosValidos = dao.validarSenhaDoUsuario(login,senha);
		
		if(!dadosValidos) {
			System.out.println("USUÁRIO E/OU SENHA INVÁLIDOS. \n");
			return false;
		}
		return true;
	}
	
	private void updateUsuario() throws Exception {
		Scanner scan = new Scanner(System.in);

		System.out.println("----- ALTERAÇÃO DE DADOS DO USUÁRIO -----");
		System.out.println("Insira seu login");
		String login = scan.next();
		
		System.out.println("Insira sua senha");
		String senha = scan.next();
		
		if(autenticar(login, senha)) {
			login = crypto.toSha256(login);
			String novoSal = crypto.generateSal();
			System.out.println("Insira seu novo login");
			String novoLogin = scan.next();
			novoLogin = crypto.toSha256(novoLogin);
			
			System.out.println("Insira sua nova senha");
			String novaSenha = scan.next();
			novaSenha = crypto.hMAC(novaSenha, novoSal);
			if(dao.updateUsuario(login, novoLogin, novaSenha, novoSal)) {
				System.out.println("DADOS ATUALIZADOS COM SUCESSO!");
			}
		}
		
		this.init();
	}

	private void deleteUsuario() throws Exception {
		Scanner scan = new Scanner(System.in);

		System.out.println("----- EXCLUSÃO DE USUÁRIO -----");
		System.out.println("Insira seu login");
		String login = scan.next();
		
		System.out.println("Insira sua senha");
		String senha = scan.next();
		
		if(autenticar(login, senha)) {
			login = crypto.toSha256(login);
			if(dao.deleteUsuario(login)) {
				System.out.println("USUÁRIO EXCLUÍDO COM SUCESSO!");
			}
		}
		this.init();
	}
}
