public class Sentence_cx_stub extends SharedObject implements Sentence_cx_itf, java.io.Serializable {

	public Sentence_cx_stub(int id) {
		super(id);
	}

	public Sentence_itf getObj() {
		Sentence_cx o = (Sentence_cx) obj;
		Sentence_itf rt = o.getObj();
		return rt;
	}

	public void write(java.lang.String arg0) {
		this.lock_write();
		Sentence_cx o = (Sentence_cx) obj;
		o.write(arg0);
		this.unlock();
	}

	public java.lang.String read() {
		this.lock_read();
		Sentence_cx o = (Sentence_cx) obj;
		java.lang.String rt = o.read();
		this.unlock();
		return rt;
	}

}