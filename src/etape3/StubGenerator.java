import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;


public class StubGenerator {
	
	/**
	 * Récupérer les méthodes publiques déclarées dans une classe
	 * @param c classe dont on veut les méthodes
	 * @return un tableau des méthodes publiques déclarées dans c
	 */
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

	/**
	 * Génère les méthodes du stub à partir d'une classe et le stocke dans un stringbuffer
	 * @param c classe dont on veut le stub
	 * @param sb stringbuffer contenant le début du stub
	 * @return stringbuffer contenant en plus les méthodes du stub 
	 */
	public static StringBuffer generateMethods(Class c, StringBuffer sb) {
		
		Method[] methods = getAccessibleMethods(c);
		
		for ( Method m : methods) {
			Parameter[] parameters = m.getParameters();
			int i;
			Read ra = m.getAnnotation(Read.class);
			Write wa = m.getAnnotation(Write.class);
						
			
			sb.append("	"+Modifier.toString(m.getModifiers())+" "+m.getReturnType().getName()+" "+m.getName()+"(");
			i = 0;
			for (Parameter p : parameters) {
				sb.append(p.getType().getName()+" "+p.getName());
				if (i!=parameters.length-1) {
					sb.append(", ");
				}
				i++;
			}
			sb.append(")");
			sb.append(" {\n");
			
			sb.append("		"+c.getName()+" o = ("+c.getName()+") obj;\n");

			if (ra!=null) {
				sb.append("		s.lock_read();\n");
			}
			if (wa!=null) {
				sb.append("		s.lock_write();\n");
			}
			
			if (!m.getReturnType().toString().equals("void")) {
				sb.append("		return o.");
			} else {
				sb.append("		o.");
			}
			sb.append(m.getName()+"(");
			i = 0;
			for (Parameter p : parameters) {
				sb.append(p.getName());
				if (i!=parameters.length-1) {
					sb.append(", ");
				}
				i++;
			}
			sb.append(");\n");
			
			if (ra!=null || wa!=null) {
				sb.append("		s.unlock();\n");
			}
			
			sb.append("	}\n\n");
			
		}
		
		return sb;
	}
	
	/**
	 * Génère un stub à partir d'une classe
	 * @param c classe dont on veut le stub
	 */
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
		
		sb.append("public class "+ c.getName() +"_stub extends SharedObject implements "+ c.getName() +"_itf, java.io.Serializable {\n\n");
		
		sb.append("	public "+c.getName()+"_stub(Object o, int id) {\n");
		sb.append("		super(o, id);\n");
		sb.append("	}\n\n");
	
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
