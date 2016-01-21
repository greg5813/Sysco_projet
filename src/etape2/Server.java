

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class Server extends UnicastRemoteObject implements Server_itf{
	
	private int id = 0;
	private HashMap<Integer,ServerObject> servers;
	private HashMap<String,Integer> names;
	
	public Server() throws RemoteException {
		super();
		servers = new HashMap<Integer,ServerObject>();
		names = new HashMap<String,Integer>();
	}

	@Override
	public int lookup(String name) throws RemoteException {
		Object i = names.get(name);
		if (i==null){
			i = 0;
		}
		return (int) i;
	}

	@Override
	public void register(String name, int id) throws RemoteException {
		names.put(name, id);
	}

	@Override
	public int create(Object o) throws RemoteException {
		id++;
		ServerObject so = new ServerObject(o,id);
		servers.put(id, so);
		return id;
	}

	@Override
	public Object lock_read(int id, Client_itf client) throws RemoteException {
		return servers.get(id).lock_read(client);
	}

	@Override
	public Object lock_write(int id, Client_itf client) throws RemoteException {
		return servers.get(id).lock_write(client);
	}
	
	public static void main(String[] args){
		try{
			LocateRegistry.createRegistry(1099);
			Server_itf s = new Server();
			Naming.rebind("//localhost:1099/server", s);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
