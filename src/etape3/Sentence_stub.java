public class Sentence_stub extends SharedObject implements Sentence_itf, java.io.Serializable {

	public Sentence_stub(Object o, int id) {
		super(o, id);
	}

	public void write(java.lang.String arg0) {
		Sentence o = (Sentence) obj;
		lock_write();
		o.write(arg0);
		unlock();
	}

	public java.lang.String read() {
		Sentence o = (Sentence) obj;
		lock_read();
		java.lang.String rt = o.read();
		unlock();
		return rt;
	}

}