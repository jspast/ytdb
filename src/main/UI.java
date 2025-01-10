package main;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import query.Querier;
import query.Query;

public class UI {
	
	private static Querier querier = null;
	private static Scanner scan = null;
	
	public static void run(String url, String username, String password) {

		try {
			querier = new Querier(url, username, password);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		scan = new Scanner(System.in);
		mainMenu();
		
	}
	
	private static void mainMenu() {

		int nextState = -1;
		
		while(nextState != 0) {
			System.out.println("Seja bem-vindo à interface YouTube. O que deseja fazer?");
			System.out.println("0. Sair\n1. Log in\n2. Consultar\n");
			nextState = scan.nextInt();
			
			switch(nextState) {
				case 1:
					loginPage();
					break;
				case 2:
					queryPage();
					break;
			}
		}
			
	}
	
	private static void loginPage() {
		int nextState = -1;
		while(nextState != 0) {
			
			System.out.println("\nO que deseja fazer?");
			System.out.println("0. Voltar\n1. Listar usuários\n2. log in\n3. Sign in");
			nextState = scan.nextInt();
			
			try {
				switch(nextState) {
					case 1:
						listUsers();
						break;
					case 2:
						login();
						break;
					case 3:
						signin();
						break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	private static void signin() throws SQLException {
		System.out.println("Insira seu e-mail:");
		String email = scan.next();
		Query findEmail = Query.on("SELECT email\n"
								 + "FROM Account\n"
								 + "WHERE email = ?")
						  	   .with(email);
		
		ResultSet rs = querier.query(findEmail);
		if(!rs.next()) {
			System.out.println("Dê um nome a sua nova conta:");
			scan.nextLine();
			String accountName = scan.nextLine();
			Query createAccount = Query.on("INSERT INTO Account (email, name)\n"
										 + "VALUES (?, ?)")
									   .with(email).with(accountName);
			
			querier.update(createAccount);
		}	
		
		System.out.println("Insira seu usuário:");
		String username = scan.next();
		
		Query findUsername = Query.on("SELECT username\n"
									+ "FROM Channel\n"
									+ "WHERE accountEmail = ?\n"
									+ "AND username = ?")
							 .with(email).with(username);
	
		rs = querier.query(findUsername);
		if(!rs.next()) {
			System.out.println("Dê um nome de display a seu novo canal:");
			scan.nextLine();
			String displayName = scan.nextLine();
			String channelUrl = "https://www.youtube.com/@" + username;
			Query createChannel = Query.on("INSERT INTO Channel (username, displayName, creationDate, url, country, accountEmail)\n"
										 + "VALUES (?, ?, CURRENT_TIMESTAMP, ?, 'Brasil', ?)")
									   .with(username).with(displayName).with(channelUrl).with(email);
			
			querier.update(createChannel);		
		} else {
			System.out.println("Esse canal já existe.");
		}
		
	}

	private static void login() throws SQLException {
		
		System.out.println("\nInsira seu e-mail:");
		String email = scan.next();
		
		Query findEmail = Query.on("SELECT email\n"
								 + "FROM Account\n"
								 + "WHERE email = ?")
						  .with(email);
		
		ResultSet rs = querier.query(findEmail);
		if(!rs.next()) {
			System.out.println("E-mail não encontrado");
			return;
		}
		
		System.out.println("Insira seu usuário:");
		String username = scan.next();
		
		Query findUsername = Query.on("SELECT username\n"
									+ "FROM Channel\n"
									+ "WHERE accountEmail = ?\n"
									+ "AND username = ?")
							 .with(email).with(username);
	
		rs = querier.query(findUsername);
		if(!rs.next()) {
			System.out.println("Canal não encontrado");
			return;
		}
		
		loggedInPage(email, username);
	}

	private static void loggedInPage(String email, String username) {
		// TODO Auto-generated method stub
		
	}

	private static void listUsers() throws SQLException {
		
		final Query listAccounts = Query.on("SELECT name\n"
										  + "FROM Account");
		
		int nextState = -1;
		while(nextState != 0) {
			ResultSet rs = querier.query(listAccounts);
			
			System.out.println("\nLista de usuários:");
			System.out.println("0. Voltar");
			for(int i = 1; rs.next(); i++) {
				System.out.println(i + ". " + rs.getString(1));
			}
			nextState = scan.nextInt();	
			
		}	
	}

	public static void queryPage() {}

}