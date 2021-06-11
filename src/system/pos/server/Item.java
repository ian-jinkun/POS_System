package system.pos.server;
/**
 * A class representing an item in the inventory. Includes the item
 * code, its description, and its cost.
 */

import java.util.Comparator;

public class Item {

    private String code;
    private String description;
    private int cost;
    private int inStock;
    private int backorder;

    public Item(String code, String description, int cost, int quantity) {
        this.code = code;
        this.description = description;
        this.cost = cost;
        this.inStock = quantity;
    }

    /**
     * Determine if the given code matches this item.
     *
     * @param code the code to check
     * @return true if the code matches this item; false otherwise
     */
    public boolean matchCode(String code) {
        return code.equals(this.code);
    }

    public void reduceInStock(int amount) {
        assert amount <= inStock;
        assert amount >= 0;
        inStock -= amount;
    }

    // Added by Jinkun Zhao
    public void increaseInStock(int amount) {
        assert amount >= 0;
        inStock += amount;
    }

    public void increaseBackorder(int amount) {
        assert inStock == 0;
        assert amount >= 0;
        backorder += amount;
    }

    // Added by Jinkun Zhao
    public void reduceBackorder(int amount) {
        assert amount >= 0;
        backorder -= amount;
    }

    public String getCode() {
        return code;
    }

    public int getCost() {
        return cost;
    }

    public int getInStock() {
        return inStock;
    }

    public int getBackorder() {
        return backorder;
    }

    public String getDescription() {
        return description;
    }

    public boolean matches(String pattern) {
        return code.indexOf(pattern) >= 0 || description.indexOf(pattern) >= 0;
    }

    @Override
    public String toString() {
        return "Code: " + code + " description: " + description + " cost: $" + (cost / 100) + "." + (cost % 100 < 10 ? "0" : "") + (cost % 100) + " (" + inStock + ")";
    }

    public static Comparator<Item> getComparator(POSServer.ItemField order) {
        // This returns one of four comparators. To prevent class explosion,
        // the comparators are defined as "anonymous inner classes" (declared
        // and created on the fly).
        switch(order) {
            case CODE:
                return new Comparator<Item>() {
                    public int compare(Item a, Item b) {
                        return a.code.compareTo(b.code);
                    }
                };
            case COST:
                return new Comparator<Item>() {
                    public int compare(Item a, Item b) {
                        return a.cost - b.cost;
                    }
                };
            case DESCRIPTION:
                return new Comparator<Item>() {
                    public int compare(Item a, Item b) {
                        return a.description.compareTo(b.description);
                    }
                };
            case QUANTITY:
                return new Comparator<Item>() {
                    public int compare(Item a, Item b) {
                        return a.inStock - b.inStock;
                    }
                };
            case BACKORDER_QUANTITY:
                return new Comparator<Item>() {
                    public int compare(Item a, Item b) {
                        return a.backorder - b.backorder;
                    }
                };
        }
        // Should never get here
        return null;
    }
}
