package system.pos.server;

/**
 * A return transaction.
 */

public class Return extends Transaction {

    public Return(String id, long time, int client) {
        super(id, time, client);
    }

    @Override
    public POSServer.TransactionType getType() {
        return POSServer.TransactionType.RETURN;
    }

    @Override
    public boolean canCompleteTransaction(TransactionUnit unit) {
        // Can always complete a return
        return true;
    }

    @Override
    public void completeTransaction(TransactionUnit unit, boolean canCompleteAll) {
        // has no effect on inventory
    }

    @Override // Added by Jinkun Zhao
    public void cancelTransaction(TransactionUnit unit) {
        // has no effect on inventory
    }

    @Override
    public String toString() {
        return "Return " + super.toString();
    }
}
