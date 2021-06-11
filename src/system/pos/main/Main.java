package system.pos.main;

/**
 * Main app for startup server and run client which interacts with the server.
 *
 * Name: Jinkun Zhao
 *
 */

import system.pos.client.GUI;
import system.pos.client.GUIAdapter;
import system.pos.client.GUIAdapterImpl;
import system.pos.client.GUIClient;
import system.pos.server.POSServer;
import system.pos.server.Server;

public class Main {
    private static POSServer server; // POS server
    private static GUIAdapter adapter; // POS server adapter

    public static void main(String[] args) {
        try {
            // Start up the POS server.
            server = Server.getOnlyServer("inventory.txt");
            adapter = new GUIAdapterImpl(server);
            // Run clients to access above POS server.
            GUIClient client = GUI.getClient(adapter);
            adapter.newClient(1, client); // Add first client into clients
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
        }
    }
}
