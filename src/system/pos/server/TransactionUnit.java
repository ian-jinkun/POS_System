package system.pos.server;

/**
 * An item in a transaction. Points to the item in the inventory and the
 * quantity.
 */

public class TransactionUnit {
    private Item item;
    private int quantity;
    private int backorderQuantity; // Added by Jinkun Zhao

    public TransactionUnit(Item item, int quantity) {
        this.item = item;
        this.quantity = quantity;
        this.backorderQuantity = 0;
    }

    public Item getItem() {
        return item;
    }

    public int getQuantity() {
        return quantity;
    }

    public boolean changeQuantity(int quantity) {
        if (quantity < 0) {
            return false;
        }

        this.quantity = quantity;
        return true;
    }

    // Added by Jinkun Zhao
    public int getBackorderQuantity() { return backorderQuantity; }

    // Added by Jinkun Zhao
    public boolean changeBackorderQuantity(int quantity) {
        if (quantity < 0) {
            return false;
        }

        this.backorderQuantity = quantity;
        return true;
    }

    /**
     * Get the total value of this transaction item.
     *
     * @return the cost of the item times the quantity
     */
    public int getTotalCost() {
        return item.getCost() * quantity;
    }

    @Override
    public String toString() {
        return item.getCode() + " (" + quantity + ")";
    }
}
