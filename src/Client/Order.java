package Client;
import java.io.Serializable;

import javax.xml.crypto.Data;

public class Order implements Serializable{
	private String name;
	private String master;
	private String flat;
	private String data;
	
	public Order() {
		// TODO Auto-generated constructor stub
	}
	
	public Order(String name, String master, String flat, String data) {
		
		this.name = name;
		this.master = master;
		this.flat = flat;
		this.data = data;
		
	}
	
	public String getName() {
		return name;
	}
	
	public String getData() {
		return data;
	}
	
	public String getFlat() {
		return flat;
	}
	
	public String getMaster() {
		return master;
	}
	
	public String toString() {
		
		String name = getName();
		String data = getData();
		String flat = getFlat();
		String master = getMaster();
		
		String out = "";
		out = "Name: " + name + ", data: " + data + ", flat: " + flat + ", master: " + master;
		
		return out;
		
	}
	
	

}
