import java.util.Random;

public class Test {
	
	static SharedObject so1;
	static SharedObject so2;
	
	public static void main(String[] args) {
		Random r = new Random();
		
		Client.init();
		so1 = Client.lookup("test1");
		if (so1 == null) {
			so1 = Client.create(new Sentence());
			Client.register("test1", so1);
		}
		so2 = Client.lookup("test2");
		if (so2 == null) {
			so2 = Client.create(new Sentence());
			Client.register("test2", so2);
		}

		
		for(int k=0; k<5; k++) {
			for(int i=0; i<20; i++) {
				try {
					so1.lock_write();
					((Sentence) so1.obj).write(Integer.toString(Integer.parseInt(((Sentence) so1.obj).read())+i));
					Thread.sleep(r.nextInt(100));
					so1.unlock();
					Thread.sleep(r.nextInt(100));
					
					so2.lock_write();
					((Sentence) so2.obj).write(Integer.toString(Integer.parseInt(((Sentence) so2.obj).read())-i));
					Thread.sleep(r.nextInt(100));
					so2.unlock();
					Thread.sleep(r.nextInt(100));
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			try {
				Thread.sleep(3000);
				so1.lock_read();
				System.out.println(((Sentence) so1.obj).read());
				so1.unlock();
				so2.lock_read();
				System.out.println(((Sentence) so2.obj).read());
				so2.unlock();
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

}
