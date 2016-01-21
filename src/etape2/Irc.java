

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
	SharedObject		sentence;
	static String		myName;
        public Transaction      transaction;

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
		SharedObject s = Client.lookup("IRC");
		if (s == null) {
			s = Client.create(new Sentence());
			Client.register("IRC", s);
                        System.out.println("s is created");
		}
		// create the graphical part
		new Irc(s);
	}

	public Irc(SharedObject s) {
	
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
                
		Button transaction_button = new Button("transaction");
		transaction_button.addActionListener(new transactionListener(this));
		add(transaction_button);
                
                Button commit_button = new Button("commit");
		commit_button.addActionListener(new commitListener(this));
		add(commit_button);
                
                Button abort_button = new Button("abort");
		abort_button.addActionListener(new abortListener(this));
		add(abort_button);
		
		setSize(470,300);
		text.setBackground(Color.black); 
		show();
		
		sentence = s;
                transaction = new Transaction();
	}
}



class readListener implements ActionListener {
	Irc irc;
	public readListener (Irc i) {
		irc = i;
	}
	public void actionPerformed (ActionEvent e) {
		
		// lock the object in read mode
		irc.sentence.lock_read();
		
		// invoke the method
		String s = ((Sentence)(irc.sentence.obj)).read();
		
                // unlock the object
                irc.sentence.unlock();
                
		
		// display the read value
		irc.text.append(s+"\n");
		
		System.out.println("irc read");
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
		irc.sentence.lock_write();
                
                ((Sentence)(irc.sentence.obj)).write(Irc.myName+" wrote "+s);
		irc.data.setText("");
		
		// invoke the method
		irc.sentence.unlock();
                
                
		System.out.println("irc write");
		
	}
}

class transactionListener implements ActionListener {
	Irc irc;
	public transactionListener (Irc i) {
        	irc = i;
	}
	public void actionPerformed (ActionEvent e) {
                
                
                irc.transaction.start();
		
                // display the read value
		irc.text.append("transactional mode \n");
                
		System.out.println("mode transaction");
	}
}


class commitListener implements ActionListener {
	Irc irc;
	public commitListener (Irc i) {
        	irc = i;
	}
	public void actionPerformed (ActionEvent e) {
				
            irc.transaction.commit();
            
		
		// display the read value
		irc.text.append("commit \n");
		
            System.out.println("irc commit");
	}
}

class abortListener implements ActionListener {
	Irc irc;
	public abortListener (Irc i) {
        	irc = i;
	}
	public void actionPerformed (ActionEvent e) {
            
                irc.transaction.abort();             
		
		// display the read value
		irc.text.append("abort \n");
                
                
		System.out.println("irc abort");
	}
}