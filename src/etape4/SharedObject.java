
import java.io.*;

public class SharedObject implements Serializable, SharedObject_itf {
	
	private enum Etat {NL, RLT, RLC, WLT, WLC, RLT_WLC};
	private static Etat lock = Etat.NL;
	Object obj;
	int id;
	
	public SharedObject(int id) {
		this.id = id;
	}
	
	// invoked by the user program on the client node
	public void lock_read() {
		switch (lock) {
		case NL:
			obj = Client.lock_read(id);
			lock = Etat.RLT; 	
			break;
		case RLT:
			try {
				throw new NestedLocksException("lock_read imbriqué");
			} catch (NestedLocksException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case RLC: 		
			lock = Etat.RLT; 	
			break;	
		case WLT:
			try {
				throw new NestedLocksException("lock_read imbriqué");
			} catch (NestedLocksException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			};
			break;	
		case WLC: 		
			lock = Etat.RLT_WLC; 	
			break;
		case RLT_WLC:
			try {
				throw new NestedLocksException("lock_read imbriqué");
			} catch (NestedLocksException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		}
		System.out.println("sharedobject lockread id "+id +" state " + lock);
	}

	// invoked by the user program on the client node
	public void lock_write() {
		switch (lock) {
		case NL: 		
			obj = Client.lock_write(id);
			lock = Etat.WLT; 	
			break;
		case RLT:
			try {
				throw new NestedLocksException("lock_write imbriqué");
			} catch (NestedLocksException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case RLC:
			obj = Client.lock_write(id);
			lock = Etat.WLT; 	
			break;
		case WLT:
			try {
				throw new NestedLocksException("lock_write imbriqué");
			} catch (NestedLocksException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;	
		case WLC: 		
			lock = Etat.WLT; 	
			break;	
		case RLT_WLC:
			try {
				throw new NestedLocksException("lock_write imbriqué");
			} catch (NestedLocksException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		}
		System.out.println("sharedobject lockwrite id "+id +" state " + lock);
	}

	// invoked by the user program on the client node
	public synchronized void unlock() {
		switch (lock) {
		case NL:
			break;
		case RLT: 		
			lock = Etat.RLC;
			notifyAll();
			break;
		case RLC:
			break;
		case WLT: 		
			lock = Etat.WLC; 	
			notifyAll();
			break;
		case WLC:
			break;
		case RLT_WLC:			
			lock = Etat.WLC;
			notifyAll();
			break;
		}
		System.out.println("sharedobject unlock id "+id +" state " + lock);
	}


	// callback invoked remotely by the server
	public synchronized Object reduce_lock() {
		switch (lock) {
		case WLT: 	
			while(lock!=Etat.WLC){
				try {
					wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			lock = Etat.RLC; 
			break;
		case WLC: 
			lock = Etat.RLC; 	
			break;	
		case RLT_WLC:	
			lock = Etat.RLT;	
			break;
		}
		System.out.println("sharedobject reducelock id "+id +" state " + lock);
		return obj;
	}

	// callback invoked remotely by the server
	public synchronized void invalidate_reader() {
		while(lock!=Etat.RLC){
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		lock = Etat.NL;
		System.out.println("sharedobject invalidatereader id "+id +" state " + lock);
	}

	// callback invoked remotely by the server
	public synchronized Object invalidate_writer() {
		while(lock!=Etat.WLC){
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		lock = Etat.NL;
		System.out.println("sharedobject invalidatewriter id "+id +" state " + lock);
		return obj;
	}
	
	private Object ReadResolve() {
		return id;
	}
}
