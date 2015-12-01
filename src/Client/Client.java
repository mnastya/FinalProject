package Client;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import javax.xml.crypto.Data;

import org.omg.Messaging.SyncScopeHelper;

import Main.Const;

public class Client {
	
	private BufferedReader in;
	private PrintWriter out;

	public Client() {
		
		Scanner scan = new Scanner(System.in);
		
		System.out.println("Enter your IP");
		System.out.println("xxx.xxx.xxx.xxx");
		
		String ip = scan.nextLine();		
		
		try {
			Socket socket = new Socket(ip, Const.Port);
			
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
			
			System.out.println("Who are you? Tenant(t) or dispatcher(d)?");
			
			String scanLine = scan.nextLine();
			Order order = new Order();
			
			if(scanLine.equals("d")){ //Если клиент Диспетчер
				
				out.println(scanLine);
				System.out.println("Enter password");
				scanLine = scan.nextLine();
				out.println(scanLine);
				System.out.println(in.readLine());
				ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
				
				while(true){				
					try {
						System.out.println("Do you want handle Order? Y|N");
						String str = "";
						str = scan.nextLine();
						if(str.equals("Y")){					
						order = (Order)ois.readObject();
						handleOrder(order);	
						}else{
							break;
						}
				
					} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				in.close();
				out.close();
				socket.close();
				
			}else if(scanLine.equals("t")){ // Если клиент квартиросъемщик
				
				out.println(scanLine);
				System.out.println("Hello " + scanLine + "!");
				order = createOrder();
				out.println("Order ready to send");
				ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
				oos.writeObject(order);
				out.println("Order get");
				in.close();
				out.close();
				socket.close();
				
			}else{
				System.out.println("error");
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	
	
	private void handleOrder(Order order) {
			
		Connection connection = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql://localhost/Orders","root","1234");
						
			Statement statement = connection.createStatement();
			
			String insertToTable = "INSERT INTO ORDERS"
		           + "(NAME, FLAT, MASTER, DATE) " + "VALUES"
		           + "('" + order.getName() + "','" + order.getFlat() + "','" + order.getMaster() + "','" + order.getData() + "')";
			
			statement.execute(insertToTable);
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}



	private Order createOrder() {
		Scanner scan = new Scanner(System.in);
		String name = "";
		String flat;
		String master = "";
		String data = "";
		System.out.println("Enter your name, pls");
		name = scan.nextLine();
		System.out.println("Enter your flat, pls");
		flat = scan.nextLine();
		System.out.println("Enter your master, pls");
		master = scan.nextLine();
		System.out.println("Enter data, pls");
		data = scan.nextLine();
		Order order = new Order(name, master, flat, data);
		return order;
	}
	
}
