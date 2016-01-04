import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.rmi.registry.*;
import java.net.*;

public class Client extends UnicastRemoteObject implements Client_itf {
	
	private static HashMap<Integer,SharedObject> objects;
	private static HashMap<SharedObject,Integer> ids;
	private static Server s;

	public Client() throws RemoteException {
		super();
	}


///////////////////////////////////////////////////
//         Interface to be used by applications
///////////////////////////////////////////////////

	// initialization of the client layer
	public static void init() {
		try {
			s = (Server) Naming.lookup("//localhost:2080/server");
		} catch (Exception e) {
			//
		}
	}
	
	// lookup in the name server
	public static SharedObject lookup(String name) {
		SharedObject so = null;
		try {
			Integer id = s.lookup(name);
			if (id != null) {
				so = objects.get(id);
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return so;
	}		
	
	// binding in the name server
	public static void register(String name, SharedObject_itf so) {
		try {
			s.register(name, ids.get(so));
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// creation of a shared object
	public static SharedObject create(Object o) {
		int id = 0;
		try {
			id = s.create(o);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SharedObject so = new SharedObject(o,id);
		objects.put(id, so);
		ids.put(so, id);
		return so;
	}
	
/////////////////////////////////////////////////////////////
//    Interface to be used by the consistency protocol
////////////////////////////////////////////////////////////

	// request a read lock from the server
	public static Object lock_read(int id) {
		return s.lock_read(id, this);
	}

	// request a write lock from the server
	public static Object lock_write (int id) {
		return id;
	}

	// receive a lock reduction request from the server
	public Object reduce_lock(int id) throws java.rmi.RemoteException {
		return id;
	}


	// receive a reader invalidation request from the server
	public void invalidate_reader(int id) throws java.rmi.RemoteException {
	}


	// receive a writer invalidation request from the server
	public Object invalidate_writer(int id) throws java.rmi.RemoteException {
		return id;
	}
	
}
