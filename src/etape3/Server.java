

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

	// get the id of the shared object by it's name
	@Override
	public int lookup(String name) throws RemoteException {
		Object i = names.get(name);
		if (i==null){
			i = 0;
		}
		return (int) i;
	}

	// bind the shared object of given id and it's name in the server
	@Override
	public void register(String name, int id) throws RemoteException {
		names.put(name, id);
	}

	// create a new shared object and return it's id
	@Override
	public int create(Object o) throws RemoteException {
		id++;
		ServerObject so = new ServerObject(o,id);
		servers.put(id, so);
		return id;
	}

	// request a lock_read of the shared object on the appropriate ServerObject
	@Override
	public Object lock_read(int id, Client_itf client) throws RemoteException {
		return servers.get(id).lock_read(client);
	}

	// request a lock_write of the shared object on the appropriate ServerObject
	@Override
	public Object lock_write(int id, Client_itf client) throws RemoteException {
		return servers.get(id).lock_write(client);
	}
	
	// request class of object id to generate suitable stub in client
	public Class getClass(Integer id) throws RemoteException {
		return servers.get(id).obj.getClass();
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
