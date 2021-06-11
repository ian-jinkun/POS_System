
package system.pos.client;

public interface GUIClient {
    void updateStatistics();

    void updateInventory();

    void updateTransaction(String var1);

    String getTimeStamp();
}

