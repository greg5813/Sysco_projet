import java.io.*;

public class SharedObject implements Serializable, SharedObject_itf {
	
	int lock = 0;	// (0) NL: no local lock
					// (1) RLC: read lock cached (not taken)
					// (2) WLC: write lock cached
					// (3) RLT: read lock taken
					// (4) WLT: write lock taken
					// (5) RLT_WLC: read lock taken and write lock cached
	
	public Object obj;
	public int id;
	
	public SharedObject(Object o) {
		this.obj = o;
	}
	
	// invoked by the user program on the client node
	public void lock_read() {
		Client.lock_read(id);
		switch (lock) {
		case 0: 		// NL: no local lock
			lock = 3; 	// RLT: read lock taken
			break;
		case 1: 		// RLC: read lock cached (not taken)
			lock = 3; 	// RLT: read lock taken
			break;	
		case 2: 		// WLC: write lock cached
			lock = 5; 	// RLT_WLC: read lock taken and write lock cached
			break;	
		}
	}

	// invoked by the user program on the client node
	public void lock_write() {
		Client.lock_write(id);
		lock = 4;
	}

	// invoked by the user program on the client node
	public synchronized void unlock() {
		switch (lock) {
		case 3: 		// RLT: read lock taken
			lock = 1; 	// RLC: read lock cached (not taken)
			break;
		case 4: 		// WLT: write lock taken
			lock = 2; 	// WLC: write lock cached
			break;	
		}
	}


	// callback invoked remotely by the server
	public synchronized Object reduce_lock() {
		return null;
	}

	// callback invoked remotely by the server
	public synchronized void invalidate_reader() {
	}

	public synchronized Object invalidate_writer() {
		return null;
	}
}
