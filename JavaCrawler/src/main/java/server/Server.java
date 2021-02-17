/**
 * Server downloads the content of the Web page under this URL.
 * Server stores the HTML content into a file. The writing must be in “append” 
 * mode. The file may contain at most 10 Web pages. If this limit is reached, 
 * the file is closed and another file is opened.
 * Server sends back to the Client the filename where the document has 
 * been saved and the location in the file where the document has been saved.
 */
package server;

import client.Client;
import compute.Compute;
import compute.Task;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteStub;
import java.rmi.server.UnicastRemoteObject;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;

/**
 *
 * @author Polis
 */
public class Server implements Compute {
    
    private static Properties properties;
    private static int cFilesNum = 1;
    private static int cFilesBytes = 0;
    private static int cFilesDocs = 0;

    public Server() {
    }
    
    private static void getFileStatus() {
        try (final InputStream input = new FileInputStream("src/main/java/server/config.properties")) {
            
            //load a properties file from class path
            properties.load(input);
            
            //Set value to variables
            cFilesNum=Integer.valueOf(properties.getProperty("cFilesNum"));
            cFilesBytes=Integer.valueOf(properties.getProperty("cFilesBytes"));
            cFilesDocs=Integer.valueOf(properties.getProperty("cFilesDocs"));

            System.out.print("Properties loaded successfully: ");
            //get the property value and print it out
            System.out.print(properties.getProperty("cFilesNum")+" ");
            System.out.print(properties.getProperty("cFilesBytes")+" ");
            System.out.println(properties.getProperty("cFilesDocs"));

        } catch (Exception ex) {
            System.err.println("getFileStatus Error: "+ex.getMessage());
            System.out.println("Creating new properties file...");
            setProperties(); //Create new properties in case of old own not found or not existing
        }
    }
    
    private static void setProperties() {
        try (final OutputStream output = new FileOutputStream("src/main/java/server/config.properties")) {
            
            // set the properties value
            properties.setProperty("cFilesNum", String.valueOf(cFilesNum));
            properties.setProperty("cFilesBytes", String.valueOf(cFilesBytes));
            properties.setProperty("cFilesDocs", String.valueOf(cFilesDocs));
            // save properties to project root folder
            properties.store(output,"");
            
            System.out.print("Saved Properties: ");
            System.out.println(properties);

        } catch (IOException io) {
           System.err.println(io.getMessage());
        }
    }
    
    public <T> T executeTask(Task<T> task) {
            return task.execute();
    }
    
    public static void main(String[] args) {
        
        try {
            Compute engine = new Server();
            //RemoteStub stub = (RemoteStub) UnicastRemoteObject.exportObject(engine, 0);
            Compute stub = (Compute) UnicastRemoteObject.exportObject(engine, 0);
            /**
             * Used for exporting a remote object with JRMP and obtaining a stub that communicates to the remote object.
             * Calling the exportObject(Remote, port) method.
             * Exports the remote object to make it available to receive incoming calls using an anonymous port.
             */
            
            String name = "Compute";
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(name,stub);
            
            properties = new Properties();
            getFileStatus();
            
            System.out.println("Server has started successfully!");
        } catch (RemoteException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    @Override
    public String crawl(String clientURL) throws RemoteException {
        int docNum = (cFilesNum-1)*10 + cFilesDocs +1;  // Current document
        int docStart = cFilesBytes;                     // Start point od current document
        int cBytes = 0;                                 // Size of document -> 1 letter = 1 byte
        int cFile = cFilesNum;                          // Current file
        
        System.out.println("URL to crawl: "+clientURL);
        
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("src/main/java/server/Files/File"+String.valueOf(cFilesNum)+".txt", true)))) {
            String html = Jsoup.connect(clientURL).get().html();    //Save html page
            out.println(html);                                      //Write to file
            cBytes = html.length();                                   //Save extra length
            out.close();                                            //Close PrintWriter
        } catch (Exception e) {
            System.err.println("crawl error: "+e.getMessage());
        }
        
        cFilesDocs++;
        cFilesBytes += cBytes;
        if(cFilesDocs == 10) {
        //Check if documents of a file are on limit
            cFilesNum++;
            cFilesBytes = 0;
            cFilesDocs = 0;
        }
        setProperties();      //Set new properties
        
        return ("Doc"+String.valueOf(docNum)+": File No."+String.valueOf(cFile)+", Start: "+String.valueOf(docStart)); //Return values
    }
    
}
