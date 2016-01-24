import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Transaction {
		boolean transactionnelMode;//savoir si une transaction est en mode transactionnel
		boolean commitvalide;//savoir si le commit est valid�
                HashMap <Integer,Object> memoire; //les objet avant le mode transaction
                HashMap <Integer,SharedObject> transWrite; // les objet modifie dans la transaction de write
                HashMap <Integer,SharedObject> transRead; //les objet modifie dans la transaction  de read
                static Transaction t;
	
	public Transaction() {//////////
		this.transactionnelMode =false;
		this.commitvalide=true;
                this.memoire=new HashMap<Integer, Object>();
                this.transRead = new HashMap<Integer,SharedObject>();
                this.transWrite = new HashMap<Integer,SharedObject>();
	}	
	
	public static Transaction getCurrentTransaction() {
		return t;
	}

	// indique si l'appelant est en mode transactionnel
	public boolean isActive() {
		return transactionnelMode;
	}
	
	// demarre une transaction (passe en mode transactionnel)
	public void start() {
		this.transactionnelMode=true;
                t = this;
	}
	
	// termine une transaction et passe en mode non transactionnel
	public boolean commit(){
                transactionnelMode = false;
                Integer keyRead;
                Iterator<Integer> objectsRead = transRead.keySet().iterator();
                while (objectsRead.hasNext()) {
                    keyRead = objectsRead.next();
                    transRead.remove(keyRead);
                    System.out.println("size memoire "+transRead.size());
                }
                
                Iterator itWrite = transWrite.entrySet().iterator();
                while (itWrite.hasNext()) {
                    Map.Entry pairs = (Map.Entry)itWrite.next();
                    ((SharedObject)pairs.getValue()).unlock();
                    itWrite.remove();
                }
                
                Integer keyMemoire;
                Iterator<Integer> objects = memoire.keySet().iterator();
                while (objects.hasNext()) {
                    keyMemoire = objects.next();
                    memoire.remove(keyMemoire);
                    System.out.println("size memoire "+memoire.size());
                }
		return commitvalide;
                
	}
		
	// abandonne et annule une transaction (et passe en mode non transactionnel)
	public void abort(){
                transactionnelMode = false;
		commitvalide = false;
                
                Integer keyRead;
                Iterator<Integer> objectsRead = transRead.keySet().iterator();
                while (objectsRead.hasNext()) {
                    keyRead = objectsRead.next();
                    transRead.remove(keyRead);
                    System.out.println("size memoire "+transRead.size());
                }
                
                Iterator itWrite = transWrite.entrySet().iterator();
                while (itWrite.hasNext()) {
                    System.out.println("size transWrite "+transWrite.size());
                    Map.Entry pairs = (Map.Entry)itWrite.next();
                    ((SharedObject)pairs.getValue()).unlock();
                    itWrite.remove();
                    System.out.println("size transWrite "+transWrite.size());
                }
                
                
                               
                Integer keyMemoire;
                Iterator<Integer> objects = memoire.keySet().iterator();
                while (objects.hasNext()) {
                    keyMemoire = objects.next();
                    memoire.remove(keyMemoire);
                    System.out.println("size memoire "+memoire.size());
                }
	}
        
        public void addObjectWrite (Integer id, SharedObject so){
            transWrite.put(id, so);
        }
        
        public void addObjectRead (Integer id, SharedObject so){
            transRead.put(id, so);
        }
        
        public void addMemoire (Integer id, Object o){
            memoire.put(id, o);
        }
	public boolean hasBeenModified (Integer id){
            boolean isContained=false;
            isContained = memoire.containsKey(id);
            return isContained;
        }
        
        public boolean isCommitValide (){
            return commitvalide;
        }
        
        public Object getObject (Integer id){
            return memoire.get(id);
        }
                
        public void commitDone (){
            commitvalide=true;
        }
}