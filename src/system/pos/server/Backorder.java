package system.pos.server;
/**
 * An order transaction. In addition to the regular transaction features,
 * an order is like a purchase but it allows some items to be on backorder.
 */

import java.util.ArrayList;

public class Backorder extends Transaction {
    private ArrayList<TransactionUnit> backOrder;

    public Backorder(String id, long time, int client) {
        super(id, time, client);
        backOrder = new ArrayList<TransactionUnit>();
    }

    @Override
    public POSServer.TransactionType getType() {
        return POSServer.TransactionType.BACKORDER;
    }

    @Override
    public boolean canCompleteTransaction(TransactionUnit unit) {
        // Can always complete a backorder
        return true;
    }

    @Override
    public void completeTransaction(TransactionUnit unit, boolean canCompleteAll) {
        int unitQuantity = unit.getQuantity();
        Item item = unit.getItem();
        int itemQuantity = item.getInStock();

        if (unitQuantity <= itemQuantity) {
            item.reduceInStock(unitQuantity);
        } else {
            // backorder
            int unitBackorderQuantity = unitQuantity - itemQuantity;
            item.reduceInStock(itemQuantity);
            item.increaseBackorder(unitBackorderQuantity);
            unit.changeBackorderQuantity(unitBackorderQuantity);
        }
    }

    @Override // Added by Jinkun Zhao
    public void cancelTransaction(TransactionUnit unit) {
        int unitQuantity = unit.getQuantity();
        int unitBackorderQuantity = unit.getBackorderQuantity();
        Item item = unit.getItem();

        item.increaseInStock(unitQuantity-unitBackorderQuantity);
        item.reduceBackorder(unitBackorderQuantity);
    }

    /**
     * Make the leftovers from an unit in a transaction into a back order.
     *
     * @param unit the unit that is back ordered
     * @param quantity the quantity of the back order
     * @return true if there is nothing left in the original unit; false otherwise
     */
    public boolean makeBackOrder(TransactionUnit unit, int quantity) {
        backOrder.add(new TransactionUnit(unit.getItem(), quantity));
        return unit.getQuantity() == 0;
    }

    @Override
    public String toString() {
        return "Order " + super.toString() + (backOrder.size() > 0 ? "\n back order: " + backOrder : "");
    }
}
