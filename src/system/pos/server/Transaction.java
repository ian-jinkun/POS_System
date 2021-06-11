package system.pos.server;
/**
 * An abstract transaction (currently, a purchase or return). Includes the transaction
 * ID, the time the transaction started, and a list of the items and their quantities.
 */

import java.util.HashMap;
import java.util.Map;

public abstract class Transaction {
    private String id;
    private long time;
    private Map<String, TransactionUnit> units;
    private boolean complete;
    private boolean cancelled; // Added by Jinkun Zhao
    private int client; // Added by Jinkun Zhao: ID of the client which creates this transaction.

    public Transaction(String id, long time, int client) {
        this.id = id;
        this.time = time;
        this.units = new HashMap<>();
        this.complete = false;
        this.cancelled = false; // Added by Jinkun Zhao
        this.client = client; // Added by Jinkun Zhao
    }

    /**
     * Add an item to the transaction.
     *
     * @param  item the inventory item to add
     * @param  quantity the quantity of the item
     * @return true if the add was successful or false if not (invalid quantity)
     */
    public boolean addItem(Item item, int quantity) {
        boolean result = true;

        TransactionUnit unit = units.get(item.getCode());
        if (unit == null) {
            if (quantity < 0) {
                result = false;
            } else {
                units.put(item.getCode(), new TransactionUnit(item, quantity));
            }
        } else {
            if (unit.getQuantity() + quantity < 0) {
                result = false;
            } else if (unit.getQuantity() + quantity == 0) {
                units.remove(item.getCode());
            } else {
                unit.changeQuantity(unit.getQuantity() + quantity);
            }
        }

        return result;
    }

    /**
     * Determine if it is possible to complete the transaction for this unit.
     *
     * @param  unit the unit to check
     * @return true if the transaction can be completed, or false otherwise
     */
    public abstract boolean canCompleteTransaction(TransactionUnit unit);

    /**
     * Complete the transaction for this unit.
     *
     * @param unit           one unit from the transaction
     * @param canCompleteAll true if all the units in the transaction returned true for canCompleteTransaction()
     */
    public abstract void completeTransaction(TransactionUnit unit, boolean canCompleteAll);

    /**
     * Complete the transaction.
     */
    public void complete() {
        assert complete != true;

        boolean canComplete = true;

        for (TransactionUnit unit: units.values()) {
            if (!canCompleteTransaction(unit)) {
                canComplete = false;
                break;
            }
        }

        for (TransactionUnit unit: units.values()) {
            completeTransaction(unit, canComplete);
        }

        complete = true;
    }

    /**
     * Cancel the transaction for this unit.
     * *** Added by Jinkun Zhao ***
     *
     * @param unit one unit from the transaction
     */
    public abstract void cancelTransaction(TransactionUnit unit);

    /**
     * Cancel the transaction.
     * *** Added by Jinkun Zhao ***
     */
    public void cancel() {
        assert cancelled != true;

        for (TransactionUnit unit: units.values()) {
            cancelTransaction(unit);
        }

        cancelled = true;
    }

    /**
     * Determine if the given ID matches the transaction ID.
     *
     * @param id the id to check
     * @return true if it is the same as the transaction ID; false otherwise
     */
    public boolean matchID(String id) {
        return id.equals(this.id);
    }

    public String getID() {
        return id;
    }

    public boolean isComplete() {
        return complete;
    }

    // Added by Jinkun Zhao
    public boolean isCancelled() { return cancelled; }

    // Added by Jinkun Zhao
    public int getClient() {
        return client;
    }

    // Added by Jinkun Zhao
    public void setClient(int client) {
        this.client = client;
    }

    public abstract POSServer.TransactionType getType();

    public int itemCount() {
        return units.size();
    }

    /**
     * Get the total quantity of the transaction.
     *
     * @return the sum of the quantities of the items in the transaction
     */
    public int totalQuantity() {
        int count = 0;
        for (TransactionUnit unit: units.values()) {
            count += unit.getQuantity();
        }
        return count;
    }

    /**
     * Get the total cost of the transaction.
     *
     * @return the sum of the cost of the items times quantities in the transaction
     */
    public int totalCost() {
        int total = 0;

        for (TransactionUnit item : units.values())
            total += item.getTotalCost();

        return total;
    }

    @Override
    public String toString() {
        int cost = totalCost();
        return "ID: " + id + " time: " + time + "\n items: " + units + "\n value: $" + (cost / 100) + "." + (cost % 100 < 10 ? "0" : "") + (cost % 100);
    }
}
