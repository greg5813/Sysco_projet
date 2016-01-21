import java.util.HashMap;

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
                this.memoire=new HashMap<Integer, Object>();
                this.transRead = new HashMap<Integer,SharedObject>();
                this.transWrite = new HashMap<Integer,SharedObject>();
                t = this;
	}
	
	// termine une transaction et passe en mode non transactionnel
	public boolean commit(){
                transactionnelMode = false;
		return commitvalide;
	}
		
	// abandonne et annule une transaction (et passe en mode non transactionnel)
	public void abort(){ /////////////////////// hay forma de hacer un callback?
                transactionnelMode = false;
		commitvalide = false;
                System.out.println("DANS TRANSACTION objm:"+memoire.get(1));
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
	public boolean isInTransactionalMode (Integer id){
            boolean isContained=false;
            isContained = transRead.containsKey(id); //////castear si es null
            isContained = transWrite.containsKey(id);
            return isContained;
        }
        
        public boolean isCommitValide (){
            return commitvalide;
        }
        
        public Object getObject (Integer id){
            return memoire.get(id);
        }
        
        public boolean isSaved (Integer id){
            return memoire.containsKey(id);
        }
}