
import java.util.HashMap;
import java.util.Random;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author JorgeEnrique
 */
public class ClientTest {
    HashMap<Integer,SharedObject> sentences;
    String myName;
    Transaction transaction;
    
    public ClientTest(String name){
        this.myName = name;
        transaction = new Transaction();
        
    }
    
    public void action( int nObjects, int nActions){
        Client.init();
	sentences = new HashMap<Integer,SharedObject>();	
	// look up the IRC object in the name server
	// if not found, create it, and register it in the name server
        
        for(int i = 1; i<=nObjects ; i++){
            SharedObject s = Client.lookup("test "+i);
            if(s==null){
                s = Client.create(new Sentence());
                Client.register("test "+i, s);
            }
            sentences.put(i, s);
            //System.out.println("ajoute id:"+i+" object:"+((Sentence)s.obj).read());
        }
        
        Random r = new Random();
        boolean debutTransaction =false;
        boolean finTransaction =false;
        
        for(int i = 0; i<nActions; i++){
            
            for(int j=1; j<=nObjects; j++){
                System.out.println("avant l'action, "+myName+" objet:"+((Sentence)sentences.get(j).obj).read());
            }
            
            int value = r.nextInt(5)+1;
            if(debutTransaction && !finTransaction && i==nActions-1){
                value =r.nextInt(2)+4;
            }
            switch(value){
                case 1:
                    actionRead(r.nextInt(nObjects)+1);
                    break;
                case 2:
                    actionWrite(r.nextInt(nObjects)+1);
                    break;
                case 3:
                    actionStart();
                    debutTransaction=true;
                    break;
                case 4:
                    actionCommit();
                    finTransaction=true;
                    break;
                case 5:
                    actionAbort();
                    finTransaction=true;
                    break;
            }
            
            for(int j=1; j<=nObjects; j++){
                System.out.println("apres l'action, "+myName+" objet:"+((Sentence)sentences.get(j).obj).read());
            }
            
        }
        
        System.out.println("FIN de "+myName);
    }
    
    
    public void actionRead(int id){
        
        System.out.println("id "+id+" read");
        
        // lock the object in read mode
	sentences.get(id).lock_read();
		
	// invoke the method
	String s = ((Sentence)(sentences.get(id).obj)).read();
		
        // unlock the object
        sentences.get(id).unlock();
		
	System.out.println(s+" readed");
        this.waitS(5);
    }   
    
    public void actionWrite(int id){
        
        System.out.println("id "+id+" write");
        
        // get the value to be written from the buffer
        String s = myName+" wrote the object "+id;
        	
        // lock the object in write mode
	sentences.get(id).lock_write();
                
        ((Sentence)(sentences.get(id).obj)).write(s);
		
	// invoke the method
	sentences.get(id).unlock();
                
                
	System.out.println(s+" writed");
        this.waitS(5);
        
    }
    
    //transaction
    public void actionStart(){
        transaction.start();
        System.out.println("start transaction");
    }
    
    public void actionCommit(){
        transaction.commit();
        System.out.println("commit transaction");
    }
    
    public void actionAbort(){
        transaction.abort();
        System.out.println("abort transaction");
    }
    
    //temps d'attende
    private void waitS(int secondes) {
        
        Random r = new Random();
        
		try {
			Thread.sleep(r.nextInt(secondes)+1 * 1000);
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
    }
    
}
