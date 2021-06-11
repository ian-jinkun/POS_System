package system.pos.server;

public class Server {
    private static POSServer onlyServer = null;
    private Server() { }

    public static POSServer getOnlyServer(String inventoryFile) {
        if (onlyServer == null) {
            onlyServer = new ModelPOSServer(inventoryFile);
        }

        return onlyServer;
    }
}
