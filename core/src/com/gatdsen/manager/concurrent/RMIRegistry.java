package com.gatdsen.manager.concurrent;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Diese Klasse repräsentiert eine Verbindung zu einem bestimmten RMI-Registry an einem bestimmten Host und Port.
 */
public final class RMIRegistry {

    // Für den Standard-Host wird "localhost" genutzt
    private static final String defaultRegistryHost = "localhost";
    // Für den Standard-Port wird der Standard-Port für RMI-Registries verwendet
    private static final int defaultRegistryPort = Registry.REGISTRY_PORT;

    public final String host;
    public final int port;
    private final Registry registry;

    /**
     * Erstellt eine Verbindung zu einem RMI-Registry an dem Standard-Host und dem Standard-Port.
     * @param host Der Host des RMI-Registries
     * @param port Der Port des RMI-Registries
     */
    public RMIRegistry(String host, Integer port) {
        this.host = host == null ? defaultRegistryHost : host;
        this.port = port == null ? defaultRegistryPort : port;
        registry = getRegistry(this.host, this.port);
    }

    /**
     * Erstellt eine Verbindung zu einem RMI-Registry an einem bestimmten Host und Port.
     * Wenn der Host nicht angegeben oder der Standard-Host ist, wird versucht an dem Port ein RMI-Registry zu
     * erstellen.
     * @param host Der Host des RMI-Registries
     * @param port Der Port des RMI-Registries
     * @return Das Registry-Objekt
     */
    private static Registry getRegistry(String host, int port) {
        // Wenn kein Host angegeben ist oder der Host der Default-Host ist, wird versucht, eine Registry an dem Port zu erstellen
        if (host == null || host.equals(defaultRegistryHost)) {
            try {
                // createRegistry() wirft eine RemoteException, wenn an dem Port bereits ein Registry-Objekt existiert
                return LocateRegistry.createRegistry(port);
            } catch (RemoteException ignored) {
            }
        }
        // In diesem Fall wird die bereits existierende Registry verwendet
        try {
            return LocateRegistry.getRegistry(host, port);
        } catch (RemoteException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Wrapper-Methode, die die {@link Registry#lookup(String)} Methode ausführt:
     * <p>
     * Returns the remote reference bound to the specified
     * <code>name</code> in this registry.
     *
     * @param   name the name for the remote reference to look up
     *
     * @return  a reference to a remote object
     *
     * @throws  NotBoundException if <code>name</code> is not currently bound
     *
     * @throws  RemoteException if remote communication with the
     * registry failed; if exception is a <code>ServerException</code>
     * containing an <code>AccessException</code>, then the registry
     * denies the caller access to perform this operation
     *
     * @throws  AccessException if this registry is local and it denies
     * the caller access to perform this operation
     *
     * @throws  NullPointerException if <code>name</code> is <code>null</code>
     */
    public Remote lookup(String name) throws RemoteException, NotBoundException, AccessException {
        return registry.lookup(name);
    }

    /**
     * Wrapper-Methode, die die {@link Registry#rebind(String, Remote)} Methode ausführt:
     * <p>
     * Replaces the binding for the specified <code>name</code> in
     * this registry with the supplied remote reference.  If there is
     * an existing binding for the specified <code>name</code>, it is
     * discarded.
     *
     * @param   name the name to associate with the remote reference
     * @param   obj a reference to a remote object (usually a stub)
     *
     * @throws  RemoteException if remote communication with the
     * registry failed; if exception is a <code>ServerException</code>
     * containing an <code>AccessException</code>, then the registry
     * denies the caller access to perform this operation (if
     * originating from a non-local host, for example)
     *
     * @throws  AccessException if this registry is local and it denies
     * the caller access to perform this operation
     *
     * @throws  NullPointerException if <code>name</code> is
     * <code>null</code>, or if <code>obj</code> is <code>null</code>
     */
    public void rebind(String name, Remote obj) throws RemoteException, AccessException {
        registry.rebind(name, obj);
    }

    /**
     * Wrapper-Methode, die die {@link Registry#unbind(String)} Methode ausführt:
     * <p>
     * Removes the binding for the specified <code>name</code> in
     * this registry.
     *
     * @param   name the name of the binding to remove
     *
     * @throws  NotBoundException if <code>name</code> is not currently bound
     *
     * @throws  RemoteException if remote communication with the
     * registry failed; if exception is a <code>ServerException</code>
     * containing an <code>AccessException</code>, then the registry
     * denies the caller access to perform this operation (if
     * originating from a non-local host, for example)
     *
     * @throws  AccessException if this registry is local and it denies
     * the caller access to perform this operation
     *
     * @throws  NullPointerException if <code>name</code> is <code>null</code>
     */
    public void unbind(String name) throws RemoteException, NotBoundException, AccessException {
        registry.unbind(name);
    }

    /**
     * Gibt den Schlüssel für die Kombination aus Host und Port zurück.
     * Wenn der Host nicht angegeben oder der Port nicht angegeben ist, wird der Standard-Host bzw. der Standard-Port
     * verwendet.
     * @param host Der Host des RMI-Registries
     * @param port Der Port des RMI-Registries
     * @return Der Schlüssel für die Kombination aus Host und Port in Form von "host:port"
     */
    public static String getHostDetailsKey(String host, Integer port) {
        if (host == null) {
            host = defaultRegistryHost;
        }
        if (port == null) {
            port = defaultRegistryPort;
        }
        return String.join(host, ":", String.valueOf(port));
    }
}
