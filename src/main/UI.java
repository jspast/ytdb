package main;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
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

	private static void loggedInPage(String email, String username) throws SQLException {
		int nextState = -1;
		
		while(nextState != 0) {
			System.out.println("Seja bem-vindo " + username + ". O que deseja fazer?");
			System.out.println("0. Sair\n1. Ver vídeo\n2. Curtir vídeo\n3. Inscrever-se");
			nextState = scan.nextInt();
			
			switch(nextState) {
				case 1:
					watchVideo(email, username);
					break;
				case 2:
					likeVideo(email, username);
					break;
				case 3:
					subscribe(email, username);
					break;
			}
		}	
	}

	private static void subscribe(String email, String username) throws SQLException {
		int nextState = -1;
		
		while(nextState != 0) {
			System.out.println("A que canal deseja se inscrever?");
			System.out.println("0. Voltar");
			
			Query getChannels = Query.on("SELECT DISTINCT Channel.displayName, username,\n"
									+ "CASE\n"
									+ "	WHEN creatorUsername IN (\n"
									+ "		SELECT creatorUsername FROM Subscription\n"
									+ "		WHERE spectatorUsername = ?\n"
									+ "	)\n"
									+ "	THEN TRUE ELSE FALSE\n"
									+ "END AS isSubscribed\n"
									+ "FROM Subscription\n"
									+ "JOIN Channel ON (creatorUsername = username)")
								  .with(username);
			ResultSet rs = querier.query(getChannels);
		
			ArrayList<String> channels = new ArrayList<String>();
			ArrayList<Boolean> subscriptions = new ArrayList<Boolean>();
			for(int i = 1; rs.next(); i++) {
				channels.add(rs.getString(2));
				subscriptions.add(rs.getBoolean(3));
				System.out.println(i + ". " + rs.getString(1) + " " + (rs.getBoolean(3) ? "(subscribed)" : ""));
			}
			
			nextState = scan.nextInt();
			
			if(nextState > 0 && nextState <= channels.size()) {
				String channelUsername = channels.get(nextState - 1);
				boolean subscribed = subscriptions.get(nextState - 1);
			
				if(!subscribed) {	
					Query subscribe = Query.on("INSERT INTO Subscription (spectatorUsername, creatorUsername)\n"
											 + "VALUES (?, ?)")
										   .with(username).with(channelUsername);
					querier.update(subscribe);
				} else {
					Query unsubscribe = Query.on("DELETE FROM Subscription\n"
											   + "WHERE spectatorUsername = ? AND creatorUsername = ?")
											 .with(username).with(channelUsername);				
					querier.update(unsubscribe);
				}
			}
		}	
	}

	private static void likeVideo(String email, String username) throws SQLException {
		int nextState = -1;
		
		while(nextState != 0) {
			System.out.println("Qual vídeo deseja curtir?");
			System.out.println("0. Voltar");
			
			Query watchedVideos = Query.on("SELECT Video.title, postId, liked FROM VIDEO\n"
											+ "NATURAL JOIN Visualization\n"
											+ "WHERE channelUsername = ?")
										   .with(username);
			ResultSet rs = querier.query(watchedVideos);
		
			ArrayList<Integer> videos = new ArrayList<Integer>();
			ArrayList<Boolean> likes = new ArrayList<Boolean>();
			for(int i = 1; rs.next(); i++) {
				videos.add(rs.getInt(2));
				likes.add(rs.getBoolean(3));
				System.out.println(i + ". " + rs.getString(1) + " " + (rs.getBoolean(3) ? "(liked)" : ""));
			}
			
			nextState = scan.nextInt();
			
			if(nextState > 0 && nextState <= videos.size()) {
				int id = videos.get(nextState - 1);
				boolean liked = likes.get(nextState - 1);
			
				if(liked) {	
					Query addLike = Query.on("UPDATE Visualization\n"
													+ "SET liked = false\n"
													+ "WHERE channelUsername = ?\n"
													+ "AND postId = ?")
											  	  .with(username).with(id);
					querier.update(addLike);
				} else {
					Query removeLike = Query.on("UPDATE Visualization\n"
													+ "SET liked = true\n"
													+ "WHERE channelUsername = ?\n"
													+ "AND postId = ?")
											  	  .with(username).with(id);				
					querier.update(removeLike);
				}
			}
		}	
	}

	private static void watchVideo(String email, String username) throws SQLException {
		int nextState = -1;
		
		while(nextState != 0) {
			System.out.println("Qual vídeo deseja ver?");
			System.out.println("0. Voltar");
			
			Query notWatchedVideos = Query.on("SELECT Video.title, postId FROM VIDEO\n"
											+ "EXCEPT\n"
											+ "SELECT Video.title, postId FROM Video\n"
											+ "NATURAL JOIN Visualization\n"
											+ "WHERE channelUsername = ?")
										   .with(username);
			ResultSet rs = querier.query(notWatchedVideos);
		
			ArrayList<Integer> videos = new ArrayList<Integer>();
			for(int i = 1; rs.next(); i++) {
				videos.add(rs.getInt(2));
				System.out.println(i + ". " + rs.getString(1));
			}
			
			nextState = scan.nextInt();
			
			if(nextState > 0 && nextState <= videos.size()) {
				int id = videos.get(nextState - 1);
				Query addVisualization = Query.on("INSERT INTO Visualization (channelUsername, postId)\n"
												+ "VALUES (?, ?)")
											  .with(username).with(id);
				querier.update(addVisualization);
			}
		}	
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

	public static void queryPage() {
		int nextState = -1;
		while(nextState != 0) {
			
			System.out.println("\nO que deseja fazer?");
			System.out.println("0. Voltar\n1. Consultas pré-definidas\n2. Consultas com parâmetros");
			nextState = scan.nextInt();
			
			try {
				switch(nextState) {
					case 1:
						definedQueries();
						break;
					case 2:
						parameterQueries();
						break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static void parameterQueries() {
		// TODO Auto-generated method stub
		
	}

	private static void definedQueries() throws SQLException {
		
		DefinedQuery[] list = {
			new DefinedQuery("O maior número de likes em um vídeo por categoria que tem vídeos com likes",
					"SELECT videocategory.name AS Category, max(videolikes) AS MaxLikes\n"
					+ "FROM videocategory left join (videolikes natural join video) on (name = videocategoryname)\n"
					+ "GROUP BY videocategory.name\n"
					+ "HAVING max(videolikes) > 0;"),
			new DefinedQuery("A quantidade total de likes em vídeos por canal",
					"SELECT channel.displayname AS Channel, sum(videolikes) AS Likes\n"
					+ "FROM (video natural join videolikes) join post on (video.postid = post.id) join channel on (post.authorusername = channel.username)\n"
					+ "GROUP BY channel.username;"),
			new DefinedQuery("Os vídeos que receberam comentário menos de 1 minuto após a publicação",
					"SELECT video.title AS Video\n"
					+ "FROM video join post as videopost on (video.postid = videopost.id) join\n"
					+ "    (comment join post as commentpost on (comment.postid = commentpost.id)) on (video.postid = comment.attachedpostid)\n"
					+ "WHERE commentpost.date < videopost.date + '1 minute';"),
			new DefinedQuery("Os canais que postaram nenhum vídeo há menos de uma semana",
					"SELECT channel.displayname AS Channel\n"
					+ "FROM channel join (video join post on (video.postid = post.id)) on (channel.username = post.authorusername)\n"
					+ "EXCEPT\n"
					+ "SELECT channel.displayname\n"
					+ "FROM channel join (video join post on (video.postid = post.id)) on (channel.username = post.authorusername)\n"
					+ "WHERE post.date > date_subtract(current_timestamp, '1 week');"),
			new DefinedQuery("Os canais brasileiros com vídeos com legenda em português",
					"SELECT displayname AS Channel\n"
					+ "FROM channel\n"
					+ "WHERE country = 'Brazil' and username in\n"
					+ "    (SELECT post.authorusername\n"
					+ "     FROM video join post on (video.postid = post.id)\n"
					+ "     WHERE exists\n"
					+ "         (SELECT language\n"
					+ "         FROM videosubtitle\n"
					+ "         WHERE language = 'pt'));"),
			new DefinedQuery("Os vídeos com mais de 10 minutos e tag 'vlog' de canais verificados",
					"SELECT video.title AS Video\n"
					+ "FROM channel join (video join post on (video.postid = post.id)) on (channel.username = post.authorusername)\n"
					+ "where video.length > 600 and channel.isverified = TRUE and exists\n"
					+ "    (SELECT *\n"
					+ "     FROM tag\n"
					+ "     WHERE tag = 'vlog' and videoid = video.id);"),
			new DefinedQuery("Os canais de contas premium e seus comentários",
					"SELECT channel.displayname AS Channel, comment.text AS Comment\n"
					+ "FROM account join channel on (account.email = channel.accountemail) right join\n"
					+ "    (comment join post on (comment.postid = post.id)) on (channel.username = post.authorusername)\n"
					+ "where account.ispremium = TRUE;"),
			new DefinedQuery("Os canais brasileiros e o número de publicações e inscritos",
					"SELECT channel.displayname AS Channel, count(distinct post.id) As Posts, count(distinct subscription.spectatorusername) As Subscribers\n"
					+ "FROM channel join post on (channel.username = post.authorusername) join subscription on (channel.username = subscription.creatorusername)\n"
					+ "WHERE country = 'Brazil'\n"
					+ "GROUP BY channel.username;")
		};
		
		int nextState = -1;
		while(nextState != 0) {	
			System.out.println("\nLista de consultas pré-definidas:");
			System.out.println("0. Voltar");
			for(int i = 1; i < 9; i++) {
				System.out.println(i + ". " + list[i - 1].getDescription());
			}
			nextState = scan.nextInt();
			
			Query query = Query.on(list[nextState - 1].getQuery());
			ResultSet rs = querier.query(query);
			printResultSet(rs);
		}
    }
	
    public static void printResultSet(ResultSet rs) throws SQLException {

        ResultSetMetaData metaData = rs.getMetaData();

        // Get the number of columns in the ResultSet
        int columnCount = metaData.getColumnCount();

        // Print column headers
        for (int i = 1; i <= columnCount; i++) {
            String columnName = metaData.getColumnLabel(i);
            System.out.print(columnName + "\t");
        }
        System.out.println();

        // Print the column values for each row
        while (rs.next()) {
            for (int i = 1; i <= columnCount; i++) {
                Object value = rs.getObject(i);
                System.out.print(value + "\t");
            }
            System.out.println();
        }
    }
}

class DefinedQuery {
	private String description;
	private String query;
	
	public String getDescription() {
		return description;
	}

	public String getQuery() {
		return query;
	}
	
	public DefinedQuery(String description, String query) {
		this.description = description;
		this.query = query;
	}
}
