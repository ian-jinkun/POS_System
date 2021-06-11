package system.pos.client;

import system.pos.server.POSServer;

import java.util.ArrayList;
import java.util.HashMap;

public class GUIAdapterImpl implements GUIAdapter {
    private POSServer server;
    private HashMap<Integer, GUIClient> clients;

    public GUIAdapterImpl(POSServer server) {
        this.server = server;
        clients = new HashMap<>();
    }

    @Override
    public String getInventoryCount() {
        return server.queryServer(POSServer.ServerQuery.INVENTORY_COUNT);
    }

    @Override
    public String getTransactionCompletedCount() {
        return server.queryServer(POSServer.ServerQuery.TRANSACTION_COMPLETED_COUNT);
    }

    @Override
    public String getTransactionInProgressCount() {
        return server.queryServer(POSServer.ServerQuery.TRANSACTION_IN_PROGRESS_COUNT);
    }

    @Override
    public String[] search(String pattern, String order) {
        POSServer.ItemField searchOrder = POSServer.ItemField.CODE;
        if (order.equals("CODE")) searchOrder = POSServer.ItemField.CODE;
        else if (order.equals("COST")) searchOrder = POSServer.ItemField.COST;
        else if (order.equals("DESCRIPTION")) searchOrder = POSServer.ItemField.DESCRIPTION;
        else if (order.equals("QUANTITY")) searchOrder = POSServer.ItemField.QUANTITY;
        else if (order.equals("BACKORDER_QUANTITY")) searchOrder = POSServer.ItemField.BACKORDER_QUANTITY;
        String iteratorId = server.search(pattern, searchOrder);
        ArrayList<String> results = new ArrayList<>();
        if (iteratorId != null) {
            while (server.next(iteratorId)) {
                String queryResult = server.queryMatch(iteratorId, searchOrder);
                if (queryResult != null)
                    results.add(queryResult);
                else {
                    System.out.println("QUERYMATCH ERROR!");
                }
            }
        }
        return results.toArray(new String[0]);
    }

    @Override
    public String getTransactionDetails(String transactionID) {
        String result = "";
        if (transactionID != null) {
            String transactionSummary = server.toString(transactionID);
            if (transactionSummary != null) {
                result += transactionSummary + "\n";
            } else
                System.out.println("SUMMARY OF CURRENT TRANSACTION ERROR!");
        }
        return result;
    }

    @Override
    public String getTransactionType(String transactionID) {
        return server.queryTransaction(transactionID, POSServer.TransactionQuery.TYPE);
    }

    @Override
    public String getTransactionItemCount(String transactionID) {
        return server.queryTransaction(transactionID, POSServer.TransactionQuery.ITEM_COUNT);
    }

    @Override
    public String getTransactionQuantity(String transactionID) {
        return server.queryTransaction(transactionID, POSServer.TransactionQuery.TOTAL_QUANTITY);
    }

    @Override
    public String getTransactionCost(String transactionID) {
    	String result = server.queryTransaction(transactionID, POSServer.TransactionQuery.TOTAL_COST);
    	if(result == null) {
    		return "0";
    	}
    	else {
    		return result;
    	}
        //return server.queryTransaction(transactionID, A5POSServer.TransactionQuery.TOTAL_COST);
    }

    @Override
    public String createTransaction(int clientID, String type) {
        String transactionId = null;
        if (type.equals("PURCHASE")) transactionId = server.createTransaction(POSServer.TransactionType.PURCHASE,
                Long.parseLong(clients.get(clientID).getTimeStamp()), clientID);
        else if (type.equals("RETURN")) transactionId = server.createTransaction(POSServer.TransactionType.RETURN,
                Long.parseLong(clients.get(clientID).getTimeStamp()), clientID);
        else if (type.equals("BACKORDER")) transactionId = server.createTransaction(POSServer.TransactionType.BACKORDER,
                Long.parseLong(clients.get(clientID).getTimeStamp()), clientID);
        else if (type.equals("RESTOCK")) transactionId = server.createTransaction(POSServer.TransactionType.RESTOCK,
                Long.parseLong(clients.get(clientID).getTimeStamp()), clientID);
        return transactionId;
    }

    @Override
    public String addToTransaction(int clientID, String transactionID, String item, String quantity) {
        return server.addItemToTransaction(transactionID, item, Integer.parseInt(quantity));
    }

    @Override
    public void endTransaction(int clientID, String type, String transactionID) {
        if (type.equals("COMPLETE")) server.completeTransaction(transactionID);
        else if (type.equals("CANCEL")) server.cancelTransaction(transactionID,
                Long.parseLong(clients.get(clientID).getTimeStamp()), clientID);
    }

    @Override
    public void newClient(int clientID, GUIClient client) {
        clients.put(clientID, client);
    }
}
