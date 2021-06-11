package system.pos.server;

/**
 * A purchase transaction.
 */

public class Purchase extends Transaction {

    public Purchase(String id, long time, int client) {
        super(id, time, client);
    }

    @Override
    public POSServer.TransactionType getType() {
        return POSServer.TransactionType.PURCHASE;
    }

    @Override
    public boolean canCompleteTransaction(TransactionUnit unit) {
        return unit.getQuantity() <= unit.getItem().getInStock();
    }

    @Override
    public void completeTransaction(TransactionUnit unit, boolean canCompleteAll) {
        if (canCompleteAll) {
            unit.getItem().reduceInStock(unit.getQuantity());
        } else {
            // clear the transaction
            unit.changeQuantity(0);
        }
    }

    @Override // Added by Jinkun Zhao
    public void cancelTransaction(TransactionUnit unit) {
        unit.getItem().increaseInStock(unit.getQuantity());
    }

    @Override
    public String toString() {
        return "Purchase " + super.toString();
    }
}
