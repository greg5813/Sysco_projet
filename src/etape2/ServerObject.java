
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class ServerObject implements Serializable, ServerObject_itf {
	
	private enum Etat {NL, RL, WL};
	private Object obj;
	private static Etat lock = Etat.NL;
	private ArrayList<Client_itf> clientsReaders;
	private Client_itf clientWriter;
	private int id;
	
	public ServerObject(Object o, int id) {
		this.clientsReaders = new ArrayList<Client_itf>();
		this.obj = o;
		this.id= id;
		
	}

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
			for(Client_itf c : clientsReaders){
				try {
					if (c != client) {
						c.invalidate_reader(id);
					}
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			clientsReaders.clear();
		}
		clientWriter=client;
		lock = Etat.WL;
		System.out.println("serverobject lockwrite id "+id +" state " + lock);
		return obj;
	}
	
}
