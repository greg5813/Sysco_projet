import java.awt.*;
import java.awt.event.*;
import java.rmi.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.*;
import java.rmi.registry.*;


public class Irc extends Frame {
	public TextArea		text;
	public TextField	data;
	Sentence_cx_itf		sentence;
	static String		myName;

	public static void main(String argv[]) {
		
		if (argv.length != 1) {
			System.out.println("java Irc <name>");
			return;
		}
		myName = argv[0];
	
		// initialize the system
		Client.init();
		
		// look up the IRC object in the name server
		// if not found, create it, and register it in the name server
		Sentence_itf s = (Sentence_itf)Client.lookup("IRC");
		if (s == null) {
			s = (Sentence_itf)Client.create(new Sentence());
			Client.register("IRC", s);
		}
		Sentence_cx_itf s2 = (Sentence_cx_itf)Client.lookup("IRC2");
		if (s2 == null) {
			s2 = (Sentence_cx_itf)Client.create(new Sentence_cx(s));
			Client.register("IRC2", s2);
		}
		// create the graphical part
		new Irc(s,s2);
	}

	public Irc(Sentence_itf s,Sentence_cx_itf s2) {
	
		setLayout(new FlowLayout());
	
		text=new TextArea(10,60);
		text.setEditable(false);
		text.setForeground(Color.red);
		add(text);
	
		data=new TextField(60);
		add(data);
	
		Button write_button = new Button("write");
		write_button.addActionListener(new writeListener(this));
		add(write_button);
		Button read_button = new Button("read");
		read_button.addActionListener(new readListener(this));
		add(read_button);
		
		setSize(470,300);
		text.setBackground(Color.black); 
		show();		
		
		sentence = s2;
	}
}



class readListener implements ActionListener {
	Irc irc;
	public readListener (Irc i) {
		irc = i;
	}
	public void actionPerformed (ActionEvent e) {

		// lock the object in read mode
		//irc.sentence.lock_read();
		
		// invoke the method
		String s = irc.sentence.read() + irc.sentence.getObj().read();
		
		
		// unlock the object
		//irc.sentence.unlock();
		
		// display the read value
		irc.text.append(s+"\n");

	}
}

class writeListener implements ActionListener {
	Irc irc;
	public writeListener (Irc i) {
        	irc = i;
	}
	public void actionPerformed (ActionEvent e) {
		
		// get the value to be written from the buffer
        	String s = irc.data.getText();
        	
        	// lock the object in write mode
		//irc.sentence.lock_write();
		
		// invoke the method
		irc.sentence.write(Irc.myName+" wrote "+s);
		irc.sentence.getObj().write(" and wrote "+s+" in ref");
		irc.data.setText("");
		
		// unlock the object
		//irc.sentence.unlock();

	}
}



