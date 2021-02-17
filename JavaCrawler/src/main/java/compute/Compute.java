package compute;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author Polis
 */
public interface Compute extends Remote {
    String crawl(String s) throws RemoteException;
}