import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.TypeParameterElement;

public class StubGenerator {
	
	
	public static Method[] getAccessibleMethods(Class c) {
		
		List<Method> result = new ArrayList<Method>();
		
	    for (Method method : c.getDeclaredMethods()) {
	        int modifiers = method.getModifiers();
	        if (Modifier.isPublic(modifiers)) {
	        	result.add(method);
	        }
	    }
	    
	    return result.toArray(new Method[result.size()]);
	}

	
	public static StringBuffer generateMethods(Class c, StringBuffer sb) {
		
		Method[] methods = getAccessibleMethods(c);
		
		for ( Method m : methods) {
			
			sb.append("	"+Modifier.toString(m.getModifiers())+" "+m.getReturnType().getName()+" "+m.getName()+"(");
			for (Parameter p : m.getParameters()) {
				sb.append(p.getType().getName()+" "+p.getName());
			}
			sb.append(")");
			sb.append(" {\n");
			
			sb.append("		"+c.getName()+" o = ("+c.getName()+") obj;\n");
			
			if (!m.getReturnType().toString().equals("void")) {
				sb.append("		return o.");
			} else {
				sb.append("		o.");
			}
			sb.append(m.getName()+"(");
			for (Parameter p : m.getParameters()) {
				sb.append(p.getName());
			}
			sb.append(");\n");
			
			sb.append("	}\n");
			
		}
		
		return sb;
	}
	
	
	public static void generateStub(Class c) {
		
		File file = new File(c.getName().concat("_stub.java"));
		FileWriter writer = null;
		try {
			writer = new FileWriter(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		StringBuffer sb = new StringBuffer();
		
		sb.append("public class "+ c.getName() +"_stub extends SharedObject implements "+ c.getName() +"_itf, java.io.Serializable {\n");
		
		sb.append("	public "+c.getName()+"_stub(Object o, int id) {\n");
		sb.append("		super(o, id);\n");
		sb.append("	}\n");
	
		sb = generateMethods(c, sb);
		
		sb.append("}");
		
		try {
			writer.write(sb.toString());
			writer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	public static void main (String[] args) { 
		Class c = null;
		try {
			c = Class.forName(args[0]);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		generateStub(c);
	}

}
