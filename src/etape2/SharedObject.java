
import java.io.*;

public class SharedObject implements Serializable, SharedObject_itf {
	
	private enum Etat {NL, RLT, RLC, WLT, WLC, RLT_WLC};
	private Etat lock = Etat.NL;
	Object obj;
	int id;
	Transaction transObj;
        
	public SharedObject(Object o, int id) {
		this.obj = o;
		this.id = id;
                this.lock = Etat.NL;
	}
	
	// invoked by the user program on the client node
	public void lock_read() {
            transObj = Transaction.getCurrentTransaction();
            if (transObj == null){
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
	        }else{
	            if (transObj.isActive()) {
	                    transObj.addObjectRead(id, this);                
	            }
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
            }
	}

	// invoked by the user program on the client node
	public void lock_write() {
            transObj = Transaction.getCurrentTransaction();
            if(transObj == null){
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
            }else{
                if (transObj.isActive()) {
                    transObj.addObjectWrite(id, this);
                    if (!transObj.hasBeenModified(id)) {
                        Object aux = deepClone(obj);
                        transObj.addMemoire(id, aux);
                        System.out.println("                add obj "+((Sentence)transObj.getObject(id)).data + " to memoire");
                    }
                    
                    System.out.println("TRANSACTION STRUCTURE \n -objw.size:"+transObj.transWrite.size()+"\n -objr.size:"+transObj.transRead.size()+"\n -objm:"+((Sentence)transObj.getObject(id)).data);
                }
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
	}

	// invoked by the user program on the client node
	public synchronized void unlock() {
            transObj = Transaction.getCurrentTransaction();
            if (transObj == null){
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
            }else{
                if(transObj.isActive() && transObj.hasBeenModified(id)){ ///////////////////differentier les read y les writes
                    System.out.println("sharedobjet doesn't released, transactional mode");
                }else{
                    if (!transObj.isCommitValide()) {
                        System.out.println("DANS UNLOCK");
                        System.out.println("TRANSACTION STRUCTURE \n -objw.size:"+transObj.transWrite.size()+"\n -objr.size:"+transObj.transRead.size()+"\n -objm:"+((Sentence)transObj.getObject(id)).data);
                        obj = deepClone(transObj.getObject(id));
                        
                        System.out.println(" ABORT PREVIOUS OBJ "+((Sentence)transObj.getObject(id)).data);
                        transObj.commitDone();
                    }
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
            }
            
		
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
        
        
	 public static Object deepClone(Object object) {
		   try {
		     ByteArrayOutputStream baos = new ByteArrayOutputStream();
		     ObjectOutputStream oos = new ObjectOutputStream(baos);
		     oos.writeObject(object);
		     
		     ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		     ObjectInputStream ois = new ObjectInputStream(bais);
		     return ois.readObject();
		   }
		   catch (Exception e) {
		     e.printStackTrace();
		     return null;
		   }
		 }
}
