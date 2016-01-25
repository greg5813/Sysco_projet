

import java.awt.*;
import java.awt.event.*;
import java.rmi.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.*;
import java.rmi.registry.*;


public class Tests implements Runnable{
    
    private ClientTest client;
    private long initialTime;
    private Integer nObjects = 2;
    private Integer nActions = 3;
    
    public Tests(ClientTest client, long initialTime){
        this.client=client;
        this.initialTime=initialTime;
    }
    
    public static void main(String argv[]) {
        ArrayList<ClientTest> clients = new ArrayList<ClientTest>();
        int size= Integer.parseInt(argv[0]);
        for(int i = 1; i<=size ; i++){
            ClientTest c = new ClientTest("Client"+i);
            clients.add(c);
        }
        
        //temps de debut
        long initialTime = System.currentTimeMillis();
        
        ArrayList<Runnable> processes = new ArrayList<Runnable>();
        for(int i = 0; i<size; i++){
            Runnable process = new Tests(clients.get(i), initialTime);
            processes.add(process);
        }
        
        for(int i = 0; i<size; i++){
            new Thread(processes.get(i)).start();
        }
        
        
        
    }

    @Override
    public void run() {
        this.client.action(nObjects, nActions);
    }
        
}
