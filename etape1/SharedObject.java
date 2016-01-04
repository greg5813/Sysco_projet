import java.io.*;

public class SharedObject implements Serializable, SharedObject_itf {
	
	private enum Etat {NL, RLT, RLC, WLT, WLC, RLT_WLC};
	private Etat lock;
	public Object obj;
	public int id;
	
	public SharedObject(Object o, int id) {
		this.obj = o;
		this.id = id;
	}
	
	// invoked by the user program on the client node
	public void lock_read() {
		switch (lock) {
		case NL: 		
			Client.lock_read(id);
			lock = Etat.RLT; 	
			break;
		case RLC: 		
			lock = Etat.RLT; 	
			break;	
		case WLC: 		
			lock = Etat.RLT; 	
			break;	
		}
	}

	// invoked by the user program on the client node
	public void lock_write() {
		switch (lock) {
		case NL: 		
			Client.lock_write(id);
			lock = Etat.WLT; 	
			break;
		case WLC: 		
			lock = Etat.WLT; 	
			break;	
		case RLC:
			Client.lock_write(id);
			lock = Etat.WLT; 	
			break;	
		}
	}

	// invoked by the user program on the client node
	public synchronized void unlock() {
		switch (lock) {
		case RLT: 		
			lock = Etat.RLC; 	
			break;
		case WLT: 		
			lock = Etat.WLC; 	
			break;
		case RLT_WLC:			
			lock = Etat.WLC;	
			break;
		}
	}


	// callback invoked remotely by the server
	public synchronized Object reduce_lock() {
		switch (lock) {
		case RLT: 	
			lock = Etat.RLC; 
			break;
		case WLT: 	
			lock = Etat.WLC; 	
			break;	
		case RLT_WLC:	
			lock = Etat.RLT;	
			break;
		}
		return null;
	}

	// callback invoked remotely by the server
	public synchronized void invalidate_reader() {
		lock = Etat.NL;
	}

	public synchronized Object invalidate_writer() {
		lock = Etat.NL;
		return null;
	}
}
