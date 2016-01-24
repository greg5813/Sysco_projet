
public class Entier implements java.io.Serializable{

	int i;
	
	public Entier() {
		i = 0;
	}
	
	@Write
	public void write(int j) {
		i = j;
	}
	
	@Read
	public int read() {
		return i;	
	}
	
}
