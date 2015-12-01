package Server;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.print.attribute.standard.OutputDeviceAssigned;

import Client.Order;
import Main.Const;

public class Server {
	
	private List<ConnectionT> connectionsT = Collections.synchronizedList(new ArrayList<ConnectionT>());
	private List<ConnectionD> connectionsD = Collections.synchronizedList(new ArrayList<ConnectionD>());
	private ServerSocket server;
	private BufferedReader in;
	private PrintWriter out;
	private LinkedBlockingQueue<Order> orders = new LinkedBlockingQueue<Order>();
	
	
	public Server() {
		
		try {
			
			server = new ServerSocket(Const.Port);
			
			while(true){
				Socket socket = server.accept();
				String inLine = "";
				
				try {
					
					in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					out = new PrintWriter(socket.getOutputStream(), true);
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
				
				inLine = in.readLine();
				if (inLine.equals("d")){
					inLine = in.readLine();
					if(inLine.equals(Const.PAS)){
						out.println("Ok");
						ConnectionD conD = new ConnectionD(socket);
						connectionsD.add(conD);
					
						conD.start();
					}else{
						out.println("Error!");
					}
				}else if(inLine.equals("t")){
					ConnectionT conT = new ConnectionT(socket);
					connectionsT.add(conT);
				
					conT.start();
				}
					
				
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			closeAll();
		}
		
	}

	
	protected void putOrder(Order order){
		if(order == null){
			System.out.println("Error!");
		}
		System.out.println(order.toString());
		try {
			orders.put(order);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(orders.size());
	}
	
	private void closeAll() {
		
		try {
			server.close();
			
			synchronized (connectionsT) {
				
				Iterator<ConnectionT> iterT = connectionsT.iterator();
				while(iterT.hasNext()){
					((ConnectionT) iterT.next()).close();
				}
				
			}
			
			synchronized (connectionsD) {
				
				Iterator<ConnectionD> iterD = connectionsD.iterator();
				while(iterD.hasNext()){
					((ConnectionD) iterD.next()).close();
				}
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}

	private class ConnectionT extends Thread { // квартиросьемщик
		
		private BufferedReader in;
		private PrintWriter out;
		private Socket socket;
		
		private String inLine = "";
		
		public ConnectionT(Socket socket) {
		
			this.socket = socket;
			
			try {
				
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream(), true);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			
		}
		
		public void run(){
			
			System.out.println("T run");
			
			try {
				System.out.println(in.readLine());
								
				ObjectInputStream ois = new ObjectInputStream(this.socket.getInputStream());
				
				Order order = new Order();
				order = (Order) ois.readObject();
				System.out.println("All ok");
			
				putOrder(order);
				in.readLine();
				close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

		private void close() {
			try {
				in.close();
				out.close();
				socket.close();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
	
private class ConnectionD extends Thread { //диспетчер
		
		private BufferedReader in;
		private PrintWriter out;
		private Socket socket;
		
		private String pass = "";
		
		public ConnectionD(Socket socket) {
		
			this.socket = socket;
			
			try {
				
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream(), true);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			
		}
		
		public void run(){
			
			System.out.println("D run");
			if (orders.isEmpty()==false){
				try {
					ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
					oos.writeObject(orders.poll());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}

		private void close() {
			try {
				in.close();
				out.close();
				socket.close();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
}
