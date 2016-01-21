
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class ServerObject implements Serializable, ServerObject_itf {
	
	private enum Etat {NL, RL, WL};
	Object obj;
	private static Etat lock = Etat.NL;
	private ArrayList<Client_itf> clientsReaders;
	private Client_itf clientWriter;
	private int id;
	
	public ServerObject(Object o, int id) {
		this.clientsReaders = new ArrayList<Client_itf>();
		this.obj = o;
		this.id= id;
		
	}

	// invoked by a client on the server
	public synchronized Object lock_read(Client_itf client) {
		if(clientWriter!=null){
			try {
				obj = clientWriter.reduce_lock(id);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			clientsReaders.add(clientWriter);
			clientWriter=null;
		}
		clientsReaders.add(client);		
		lock = Etat.RL;
		System.out.println("serverobject lockread id "+id +" state " + lock);
		return obj;
	}

	// invoked by a client on the server
	public synchronized Object lock_write(Client_itf client) {
		if(clientWriter!=null){
			try {
				obj = clientWriter.invalidate_writer(id);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			clientWriter=null;
		}
		if(!clientsReaders.isEmpty()){
			List<Thread> invalideurs = new ArrayList<Thread>();
			for(Client_itf c : clientsReaders){
				if (c != client) {
					Thread t = new InvalidateReader(c);
					invalideurs.add(t);
					t.start();
				}
			}
			for (Thread t : invalideurs) {
				try {
					t.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			invalideurs.clear();
			clientsReaders.clear();
		}
		clientWriter=client;
		lock = Etat.WL;
		System.out.println("serverobject lockwrite id "+id +" state " + lock);
		return obj;
	}
	

	// request invalidate_reader on given client in parallel
	class InvalidateReader extends Thread {
		private Client_itf client;
		public InvalidateReader(Client_itf c) {
			this.client = c;
		}
		public void run() {
			try {
				client.invalidate_reader(id);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}
