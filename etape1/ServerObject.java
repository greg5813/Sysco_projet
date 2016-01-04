import java.rmi.RemoteException;

public class ServerObject {
	
	private Object obj;
	int lock = 0; 	// (0) NL: no lock
					// (1) RL: read lock
					// (2) WL: write lock
	
	public ServerObject(Object o) {
		this.obj = o;
	}

	public Object lock_read(Client_itf client) throws RemoteException {
		// TODO Auto-generated method stub
		lock = 1;
		return obj;
	}

	public Object lock_write(Client_itf client) throws RemoteException {
		// TODO Auto-generated method stub
		lock = 2;
		return obj;
	}
	
}
