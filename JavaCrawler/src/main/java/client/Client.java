/**
 * Client reads a local file that contains URLs and saves them into a list.
 * Client sends the first URL of the list to the Server.
 * Client sends the next URL to the Server to be downloaded.
 * cmd -> start rmiregistry -J-classpath -JD:\Έγγραφα\NetBeansProjects\mavenproject1\JavaCrawler\target\JavaCrawler-1.0-SNAPSHOT.jar
 */
package client;

import compute.Compute;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Polis
 */
public class Client {
    
    public static void main(String[] args) throws IOException {
        /**
         * First, run at cmd:
         * start rmiregistry -J-classpath -JD:\Έγγραφα\NetBeansProjects\mavenproject1\JavaCrawler\target\JavaCrawler-1.0-SNAPSHOT.jar
         */
        try {
            String name = "Compute";
            String host = (args.length <1) ?null : args[0]; 
            Registry registry = LocateRegistry.getRegistry(host);
            
            Compute compute = (Compute)registry.lookup(name);
            
            String result = null;
            //FileReader file = new FileReader("src/main/java/Client/urls.txt");
            try (BufferedReader br = new BufferedReader(new FileReader("src/main/java/client/urls.txt"))) {
                String line = null;
                while ((line = br.readLine()) != null) {
                    result = compute.crawl(line);
                    System.out.println(result);
                }
                System.out.println("Client stopped.");
                br.close();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (NotBoundException | RemoteException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
