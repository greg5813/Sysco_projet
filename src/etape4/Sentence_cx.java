

public class Sentence_cx implements java.io.Serializable {
	
	String 	data;
	Sentence_itf s;
	
	public Sentence_cx(Sentence_itf o) {
		data = new String("");
		s = o;
	}
	
	@Write
	public void write(String text) {
		data = text;
	}
	
	@Read
	public String read() {
		return data;	
	}
	
	public Sentence_itf getObj() {
		return s;
	}
}
