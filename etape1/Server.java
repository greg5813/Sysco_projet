import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.HashMap;

public class Server implements Server_itf{
	
	private int id = 0;
	private HashMap<Integer,ServerObject> servers;
	private HashMap<String,Integer> names;

	@Override
	//OK
	public int lookup(String name) throws RemoteException {
		return names.get(name);
	}

	@Override
	//OK
	public void register(String name, int id) throws RemoteException {
		names.put(name, id);
	}

	@Override
	//OK
	public int create(Object o) throws RemoteException {
		id++;
		ServerObject so = new ServerObject(o);
		servers.put(id, so);
		return id;
	}

	@Override
	//OK
	public Object lock_read(int id, Client_itf client) throws RemoteException {
		return servers.get(id).lock_read(client);
	}

	@Override
	//OK
	public Object lock_write(int id, Client_itf client) throws RemoteException {
		return servers.get(id).lock_write(client);
	}
	
	public static void main(String[] args) {
		try{
			LocateRegistry.createRegistry(2080);
			Server s = new Server();
			Naming.rebind("//localhost:2080/server", s);
		} catch (Exception e) {
			//
		}
	}

}
