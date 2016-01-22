public class Sentence_stub extends SharedObject implements Sentence_itf, java.io.Serializable {

	public Sentence_stub(int id) {
		super(id);
	}

	public void write(java.lang.String arg0) {
		this.lock_write();
		Sentence o = (Sentence) obj;
		o.write(arg0);
		this.unlock();
	}

	public java.lang.String read() {
		this.lock_read();
		Sentence o = (Sentence) obj;
		java.lang.String rt = o.read();
		this.unlock();
		return rt;
	}

}