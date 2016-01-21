import java.lang.reflect.Method;

public class StubGenerator {
	

	public static void generateStub(Class c) {
		Method[] methods = c.getMethods();
		
		System.out.println("public class "+ c.getName() +"_stub extends SharedObject implements "+ c.getName() +"_itf, java.io.Serializable {");
		
		System.out.println("}");
	}

}
