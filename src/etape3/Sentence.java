

public class Sentence implements java.io.Serializable {
	
	String 	data;
	
	public Sentence() {
		data = new String("");
	}
	
	@Write
	public void write(String text) {
		data = text;
		System.out.println("sentence write" + text + data);
	}
	
	@Read
	public String read() {
		System.out.println("sentence read" + data);
		return data;	
	}
	
}