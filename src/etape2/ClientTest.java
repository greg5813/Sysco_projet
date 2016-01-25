
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
        sentences = new HashMap<Integer,SharedObject>();
    }
    
    public void action( int nObjects, int nActions){
        Client.init();
		
	// look up the IRC object in the name server
	// if not found, create it, and register it in the name server
        
        for(int i = 1; i<=nObjects ; i++){
            SharedObject s = Client.lookup("IRC");
            if(s==null){
                s = Client.create(new Sentence());
                Client.register("IRC", s);
            }
            sentences.put(i, s);
            //System.out.println("ajoute id:"+i+" object:"+((Sentence)s.obj).read());
        }
        
        Random r = new Random();
        
        for(int i = 0; i<nActions; i++){
            int value = r.nextInt(2)+1;
            switch(value){
                case 1:
                    actionRead(r.nextInt(nObjects)+1);
                    break;
                case 2:
                    actionWrite(r.nextInt(nObjects)+1);
                    break;
            }
            
        }
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
        this.waitS(3);
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
        this.waitS(3);
        
    }
    
    private void waitS(int secondes) {
		try {
			Thread.sleep(secondes * 1000);
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
    }
    
}
