package system.pos.server;

/**
 * A restock transaction.
 */

public class Restock extends Transaction {
    public Restock(String id, long time, int client) {
        super(id, time, client);
    }

    @Override
    public boolean canCompleteTransaction(TransactionUnit unit) {
        return true;
    }

    @Override
    public void completeTransaction(TransactionUnit unit, boolean canCompleteAll) {
        int unitQuantity = unit.getQuantity();
        Item item = unit.getItem();
        int itemBackorderQuantity = item.getBackorder();
        if (itemBackorderQuantity >= unitQuantity) {
            item.reduceBackorder(itemBackorderQuantity);
            unit.changeBackorderQuantity(unitQuantity);
        }
        else {
            item.reduceBackorder(itemBackorderQuantity); // Reduce backorder to zero
            item.increaseInStock(unitQuantity-itemBackorderQuantity); // Increase quantity after clear backorder to zero
            unit.changeBackorderQuantity(itemBackorderQuantity);
        }
    }

    @Override
    public void cancelTransaction(TransactionUnit unit) {
        int unitQuantity = unit.getQuantity();
        int unitBackorderQuantity = unit.getBackorderQuantity();
        Item item = unit.getItem();
        if (unitBackorderQuantity < unitQuantity) {
            item.reduceInStock(unitQuantity-unitBackorderQuantity);
            item.increaseBackorder(unitBackorderQuantity);
        } else { // unitBackorderQuantity == unitQuantity which means that InStock quantity not changed.
            item.increaseBackorder(unitBackorderQuantity);
        }
    }

    @Override
    public POSServer.TransactionType getType() {
        return POSServer.TransactionType.RESTOCK;
    }
}
