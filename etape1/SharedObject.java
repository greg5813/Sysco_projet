import java.io.*;

public class SharedObject implements Serializable, SharedObject_itf {
	
	int lock = 0;	// (0) NL: no local lock
					// (1) RLC: read lock cached (not taken)
					// (2) WLC: write lock cached
					// (3) RLT: read lock taken
					// (4) WLT: write lock taken
					// (5) RLT_WLC: read lock taken and write lock cached
	
	public Sentence obj;
	public int id;
	
	// invoked by the user program on the client node
	public void lock_read() {
	}

	// invoked by the user program on the client node
	public void lock_write() {
	}

	// invoked by the user program on the client node
	public synchronized void unlock() {
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
