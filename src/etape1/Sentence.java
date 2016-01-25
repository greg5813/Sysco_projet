public class Sentence implements java.io.Serializable {
	String 		data;
	public Sentence() {
		data = new String("0");
	}
	
	public void write(String text) {
		data = text;
	}
	public String read() {
		return data;	
	}
	
}