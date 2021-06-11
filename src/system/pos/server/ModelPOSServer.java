package system.pos.server;

/**
 * Server class implementing the POSServer interface. Stores inventory
 * and transactions.
 */

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

class ModelPOSServer implements POSServer {
    private TreeMap<String, Item> inventory;
    private HashMap<String, Transaction> transactions;
    private HashMap<String, ItemIterator> iterators;
    private static int lastID = 1;

    /**
     * Get the initial inventory from the given file.
     *
     * @param inventoryFile the name of the file to read inventory from
     */
    public ModelPOSServer(String inventoryFile) {
        inventory = new TreeMap<>();
        transactions = new HashMap<>();
        iterators = new HashMap<>();

        String msg = readInventory(inventoryFile);
        if (msg != null) {
            System.out.println(msg);
        }
    }

    /**
     * Read the contents of a file into the inventory.
     *
     * @param inventoryFile the name of the file
     * @return null if successful; otherwise, a message describing the error(s)
     */
    private String readInventory(String inventoryFile) {
        String msg = "";
        BufferedReader in;
        String line;
        String[] tokens;
        int cost, quantity;

        try {
            in = new BufferedReader(new FileReader(inventoryFile));

            line = in.readLine();
            while (line != null) {
                tokens = line.split(",");
                if (tokens.length != 4) {
                    msg += "Invalid line: " + line + "\n";
                } else {
                    cost = -1;
                    quantity = -1;
                    try {
                        cost = Integer.parseInt(tokens[2]);
                        quantity = Integer.parseInt(tokens[3]);
                    } catch (NumberFormatException nfe) {
                    }
                    if (cost < 0)
                        msg += "Invalid cost: " + line + "\n";
                    else if (quantity < 0)
                        msg += "Invalid quantity: " + line + "\n";
                    else
                        inventory.put(tokens[0], new Item(tokens[0], tokens[1], cost, quantity));
                }
                line = in.readLine();
            }
        } catch (IOException ioe) {
            msg += ioe.getMessage();
        }

        return msg.length() == 0 ? null : msg;
    }

    public String createTransaction(TransactionType type, long time, int client) {
        Transaction t = null;
        String tID = "" + lastID;

        switch (type) {
            case PURCHASE:
                t = new Purchase(tID, time, client);
                break;
            case RETURN:
                t = new Return(tID, time, client);
                break;
            case BACKORDER:
                t = new Backorder(tID, time, client);
                break;
            case RESTOCK: // Added by Jinkun Zhao
                t = new Restock(tID, time, client);
                break;
        }
        lastID++;

        transactions.put(tID, t);

        return t.getID();
    }

    public String addItemToTransaction(String id, String code, int quantity) {
        String result = null;
        Transaction trans = transactions.get(id);
        Item item;

        if (trans == null) {
            result = "Unable to find transaction " + id + " to add an item";
        } else if (code == null) {
            result = "Invalid item code";
        } else if (trans.isComplete()) {
            result = "Transaction " + id + " already completed";
        } else {
            item = inventory.get(code);
            if (item == null) {
                result = "Unable to find item " + code + " in inventory";
            } else {
                if (!trans.addItem(item, quantity)) {
                    result = "Invalid quantity " + quantity + " of item " + code;
                }
            }
        }

        return result;
    }

    public String completeTransaction(String id) {
        String result = null;
        Transaction t = transactions.get(id);

        if (t == null)
            result = "Unable to find transaction " + id;
        else if (t.isComplete())
            result = "Transaction already completed " + id;
        else
            t.complete();

        return result;
    }

    public String queryTransaction(String id, TransactionQuery query) {
        String result = null;
        Transaction t = transactions.get(id);

        if (t != null) {
            switch (query) {
                case TYPE:
                    result = t.getType().toString();
                    break;
                case ITEM_COUNT:
                    result = Integer.toString(t.itemCount());
                    break;
                case TOTAL_QUANTITY:
                    result = Integer.toString(t.totalQuantity());
                    break;
                case TOTAL_COST:
                    result = Integer.toString(t.totalCost());
                    break;
                case IS_COMPLETE:
                    result = Boolean.toString(t.isComplete());
                    break;
            }
        }

        return result;
    }

    @Override
    public String cancelTransaction(String id, long time, int client) {
        String result = null;
        Transaction t = transactions.get(id);

        if (t == null)
            result = "Unable to find transaction " + id;
        else if (t.isCancelled())
            result = "Transaction already cancelled " + id;
        else {
            if (t.getClient() == client)
                t.cancel();
            else
                result = "The transaction is not created by this client!";
        }

        return result;
    }

    public String toString(String id) {
        String result = null;
        Transaction t = transactions.get(id);

        if (t != null) {
            result = t.toString();
            result += "\n" + ((t.isComplete())?" COMPLETED ":" NOT_COMPLETED "); // Added by Jinkun Zhao
            result += "\n" + ((t.isCancelled())?" CANCELLED ":" NOT_CANCELLED "); // Added by Jinkun Zhao
        }

        return result;
    }

    public String queryServer(ServerQuery query) {
        String result = null;

        switch (query) {
            case INVENTORY_COUNT:
                result = Integer.toString(inventory.size());
                break;
            case TRANSACTION_COMPLETED_COUNT:
                result = Integer.toString(countCompleteTransactions());
                break;
            case TRANSACTION_IN_PROGRESS_COUNT:
                result = Integer.toString(transactions.size() - countCompleteTransactions());
                break;
        }

        return result;
    }

    private int countCompleteTransactions() {
        int count = 0;
        for (Map.Entry<String, Transaction> entry: transactions.entrySet()) {
            if (entry.getValue().isComplete()) {
                count++;
            }
        }
        return count;
    }

    @Override
    public String toString() {
        String result = "";

        for (Map.Entry<String,Item> entry: inventory.entrySet()) {
            result += entry.getValue().toString() + "\n";
        }

        return result;
    }

    /**
     * Begin a search through item descriptions.
     *
     * @param pattern the string to search for (partial matches OK)
     * @param order the order that the results will be presented by the iterator
     * @return the iterator ID
     */
    @Override
    public String search(String pattern, ItemField order) {
        if (pattern == null) {
            return null;
        }

        ItemIterator ii = new ItemIterator(Item.getComparator(order));

        iterators.put(ii.getID(), ii);

        for (Map.Entry<String,Item> entry: inventory.entrySet()) {
            Item item = entry.getValue();
            if (item.matches(pattern))
                ii.add(item);
        }

        return ii.getID();
    }

    /**
     * Get the next match for the search. Call this until it returns
     * false; when it returns true, there is a matching item available.
     * Call it once before the first match is available, and repeatedly
     * for each subsequent match.
     *
     * @param iID the iterator ID
     * @return true if the search has more matches; false if done
     */
    @Override
    public boolean next(String iID) {
        ItemIterator ii = iterators.get(iID);
        if (ii != null && ii.hasNext()) {
            ii.next();
            return true;
        }
        iterators.remove(iID);
        return false;
    }

    @Override
    public String queryMatch(String iID, ItemField query) {
        String result = null;
        ItemIterator ii = iterators.get(iID);

        if (ii != null && ii.current() != null) {
            Item item = ii.current();

            switch (query) {
                case CODE:
                    result = item.getCode();
                    break;
                case COST:
                    result = "" + item.getCost();
                    break;
                case DESCRIPTION:
                    result = item.getDescription();
                    break;
                case QUANTITY:
                    result = "" + item.getInStock();
                    break;
                case BACKORDER_QUANTITY:
                    result = "" + item.getBackorder();
                    break;
            }
        }

        return result;
    }
}
