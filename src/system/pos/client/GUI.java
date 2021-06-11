
package system.pos.client;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class GUI {
    private static Q1Window client;

    public GUI() {
    }

    public static void main(String[] var0) {
        getClient(new GUIDemoAdapter());
    }

    public static GUIClient getClient(GUIAdapter var0) {
        if (client == null) {
            client = new Q1Window(var0);
            client.setTitle("POS Client #1");
            client.pack();
            client.setVisible(true);
        }

        return client;
    }

    private static class Q1Window extends JFrame implements GUIClient {
        private static final String[] SEARCH_ORDERS = new String[]{"Code", "Cost", "Description", "Quantity", "Backorder Quantity"};
        private final int id;
        private final GUIAdapter client;
        private String previousSearch;
        private String transactionID;
        private final JList<String> list;
        private final Q1Window.LabelledTextPanel inventoryCount;
        private final Q1Window.LabelledTextPanel transactionCompletedCount;
        private final Q1Window.LabelledTextPanel tranasctionInProgressCount;
        private final JButton newClient;
        private final JTextField searchText;
        private final JButton searchButton;
        private final JComboBox<String> searchOrder;
        private final Q1Window.LabelledTextPanel selectedItem;
        private final JButton purchaseButton;
        private final JButton returnButton;
        private final JButton backorderButton;
        private final JButton restockButton;
        private final JButton completeButton;
        private final JButton cancelButton;
        private final Q1Window.LabelledTextPanel addQuantity;
        private final JButton addToTransaction;
        private final JTextArea transactionText;
        private final Q1Window.LabelledTextPanel transID;
        private final Q1Window.LabelledTextPanel transType;
        private final Q1Window.LabelledTextPanel transCount;
        private final Q1Window.LabelledTextPanel transQuantity;
        private final Q1Window.LabelledTextPanel transCost;
        private static int lastID = 1;
        private static SimpleDateFormat sdf;

        private Q1Window(GUIAdapter var1) {
            this.previousSearch = null;
            this.transactionID = null;
            this.id = lastID++;
            this.client = var1;
            JPanel var2 = new JPanel(new BorderLayout());
            this.list = new JList();
            this.list.setSelectionMode(0);
            this.list.setVisibleRowCount(8);
            JScrollPane var7 = new JScrollPane(this.list);
            JPanel var3 = new JPanel(new FlowLayout());
            this.inventoryCount = new Q1Window.LabelledTextPanel("Inventory:", 5, false);
            this.transactionCompletedCount = new Q1Window.LabelledTextPanel("Transactions Completed:", 5, false);
            this.tranasctionInProgressCount = new Q1Window.LabelledTextPanel("Transactions In Progress:", 5, false);
            this.newClient = new JButton("New Client");
            var3.add(this.inventoryCount);
            var3.add(this.transactionCompletedCount);
            var3.add(this.tranasctionInProgressCount);
            var3.add(this.newClient);
            var2.add(var3, "North");
            var3 = new JPanel(new BorderLayout());
            TitledBorder var6 = BorderFactory.createTitledBorder("  Inventory  ");
            var6.setTitleJustification(2);
            var3.setBorder(var6);
            JPanel var5 = new JPanel(new FlowLayout());
            this.searchText = new JTextField(40);
            var5.add(this.searchText);
            this.searchButton = new JButton("Search");
            var5.add(this.searchButton);
            this.searchOrder = new JComboBox(SEARCH_ORDERS);
            var5.add(this.searchOrder);
            var3.add(var5, "North");
            var3.add(var7, "South");
            var2.add(var3, "Center");
            JPanel var4 = new JPanel(new BorderLayout());
            var6 = BorderFactory.createTitledBorder("  Transaction  ");
            var6.setTitleJustification(2);
            var4.setBorder(var6);
            var3 = new JPanel(new BorderLayout());
            var5 = new JPanel(new FlowLayout());
            var5.add(new JLabel("Begin"));
            this.purchaseButton = new JButton("Purchase");
            this.returnButton = new JButton("Return");
            this.backorderButton = new JButton("Backorder");
            this.restockButton = new JButton("Restock");
            var5.add(this.purchaseButton);
            var5.add(this.returnButton);
            var5.add(this.backorderButton);
            var5.add(this.restockButton);
            var3.add(var5, "West");
            var5 = new JPanel(new FlowLayout());
            var5.add(new JLabel("End"));
            this.completeButton = new JButton("Complete");
            this.completeButton.setEnabled(false);
            this.cancelButton = new JButton("Cancel");
            this.cancelButton.setEnabled(false);
            var5.add(this.completeButton);
            var5.add(this.cancelButton);
            var3.add(var5, "East");
            var4.add(var3, "North");
            var3 = new JPanel(new BorderLayout());
            this.selectedItem = new Q1Window.LabelledTextPanel("Selected:", 40, false);
            var3.add(this.selectedItem, "North");
            var5 = new JPanel(new FlowLayout());
            this.addQuantity = new Q1Window.LabelledTextPanel("Quantity:", 5, true);
            this.addQuantity.setEnabled(false);
            var5.add(this.addQuantity);
            this.addToTransaction = new JButton("Add");
            this.addToTransaction.setEnabled(false);
            var5.add(this.addToTransaction);
            var3.add(var5, "Center");
            this.transactionText = new JTextArea();
            this.transactionText.setEditable(false);
            this.transactionText.setRows(8);
            var3.add(new JScrollPane(this.transactionText), "South");
            var4.add(var3, "Center");
            var3 = new JPanel(new FlowLayout());
            this.transID = new Q1Window.LabelledTextPanel("ID:", 10, false);
            this.transType = new Q1Window.LabelledTextPanel("Type:", 10, false);
            this.transCount = new Q1Window.LabelledTextPanel("Item Count:", 5, false);
            this.transQuantity = new Q1Window.LabelledTextPanel("Total Quantity:", 5, false);
            this.transCost = new Q1Window.LabelledTextPanel("Total Cost:", 10, false);
            var3.add(this.transType);
            var3.add(this.transID);
            var3.add(this.transCount);
            var3.add(this.transQuantity);
            var3.add(this.transCost);
            var4.add(var3, "South");
            var2.add(var4, "South");
            this.add(var2);
            this.newClient.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent var1) {
                    Q1Window var2 = new Q1Window(Q1Window.this.client);
                    var2.setTitle("POS Client #" + var2.id);
                    var2.pack();
                    var2.setVisible(true);
                    Q1Window.this.client.newClient(var2.id, var2);
                }
            });
            ActionListener var8 = new ActionListener() {
                public void actionPerformed(ActionEvent var1) {
                    if (Q1Window.this.client != null) {
                        Q1Window.this.previousSearch = Q1Window.this.searchText.getText();
                        Q1Window.this.list.setListData(Q1Window.this.client.search(Q1Window.this.previousSearch, Q1Window.this.searchOrder.getSelectedItem().toString().toUpperCase().replace(' ', '_')));
                        Q1Window.this.selectedItem.updateText("");
                    }

                }
            };
            this.searchButton.addActionListener(var8);
            this.searchOrder.addActionListener(var8);
            this.list.addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent var1) {
                    String var2 = "";
                    int var3 = Q1Window.this.list.getSelectedIndex();
                    if (var3 >= 0) {
                        var2 = ((String) Q1Window.this.list.getModel().getElementAt(var3)).toString();
                    }

                    Q1Window.this.selectedItem.updateText(var2);
                    Q1Window.this.addToTransaction.setEnabled(var2.length() > 0 && Q1Window.this.transactionID != null);
                }
            });
            ActionListener var9 = new ActionListener() {
                public void actionPerformed(ActionEvent var1) {
                    if (Q1Window.this.client != null) {
                        Q1Window.this.transactionID = Q1Window.this.client.createTransaction(Q1Window.this.id, var1.getActionCommand().toUpperCase());
                        Q1Window.this.updateTransaction(Q1Window.this.transactionID);
                        Q1Window.this.purchaseButton.setEnabled(false);
                        Q1Window.this.returnButton.setEnabled(false);
                        Q1Window.this.backorderButton.setEnabled(false);
                        Q1Window.this.restockButton.setEnabled(false);
                        Q1Window.this.completeButton.setEnabled(true);
                        Q1Window.this.cancelButton.setEnabled(true);
                        Q1Window.this.addQuantity.updateText("1");
                        Q1Window.this.addQuantity.setEnabled(true);
                        Q1Window.this.addToTransaction.setEnabled(Q1Window.this.selectedItem.getText().length() > 0);
                    }

                }
            };
            this.purchaseButton.addActionListener(var9);
            this.returnButton.addActionListener(var9);
            this.backorderButton.addActionListener(var9);
            this.restockButton.addActionListener(var9);
            var9 = new ActionListener() {
                public void actionPerformed(ActionEvent var1) {
                    if (Q1Window.this.client != null) {
                        Q1Window.this.client.endTransaction(Q1Window.this.id, var1.getActionCommand().toUpperCase(), Q1Window.this.transactionID);
                        Q1Window.this.updateTransaction((String)null);
                        Q1Window.this.purchaseButton.setEnabled(true);
                        Q1Window.this.returnButton.setEnabled(true);
                        Q1Window.this.backorderButton.setEnabled(true);
                        Q1Window.this.restockButton.setEnabled(true);
                        Q1Window.this.completeButton.setEnabled(false);
                        Q1Window.this.cancelButton.setEnabled(false);
                        Q1Window.this.addQuantity.updateText("");
                        Q1Window.this.addQuantity.setEnabled(false);
                        Q1Window.this.addToTransaction.setEnabled(false);
                    }

                }
            };
            this.completeButton.addActionListener(var9);
            this.cancelButton.addActionListener(var9);
            var9 = new ActionListener() {
                public void actionPerformed(ActionEvent var1) {
                    if (Q1Window.this.client != null) {
                        String var2 = Q1Window.this.client.addToTransaction(Q1Window.this.id, Q1Window.this.transactionID, Q1Window.this.selectedItem.getText(), Q1Window.this.addQuantity.getText());
                        if (var2 != null) {
                            JOptionPane.showMessageDialog(Q1Window.this.rootPane, var2, "Error", 0);
                        }

                        Q1Window.this.updateTransaction(Q1Window.this.transactionID);
                    }

                }
            };
            this.addToTransaction.addActionListener(var9);
            this.updateStatistics();
        }

        public void updateStatistics() {
            if (this.client != null) {
                this.inventoryCount.updateText(this.client.getInventoryCount());
                this.transactionCompletedCount.updateText(this.client.getTransactionCompletedCount());
                this.tranasctionInProgressCount.updateText(this.client.getTransactionInProgressCount());
            }

        }

        public void updateInventory() {
            if (this.client != null && this.previousSearch != null) {
                this.list.setListData(this.client.search(this.previousSearch, this.searchOrder.getSelectedItem().toString().toUpperCase().replace(' ', '_')));
            }

        }

        public void updateTransaction(String var1) {
            if (this.client != null) {
                this.transactionText.setText(this.client.getTransactionDetails(var1));
                this.transID.updateText(var1);
                this.transType.updateText(this.client.getTransactionType(var1));
                this.transCount.updateText(this.client.getTransactionItemCount(var1));
                this.transQuantity.updateText(this.client.getTransactionQuantity(var1));
                String var2 = this.client.getTransactionCost(var1);

                try {
                    var2 = String.format("%.2f", (double)Integer.parseInt(var2) / 100.0D);
                } catch (Exception var4) {
                }

                if (var2.length() > 0) {
                    var2 = "$" + var2;
                }

                this.transCost.updateText(var2);
            }

        }

        public String getTimeStamp() {
            if (sdf == null) {
                sdf = new SimpleDateFormat("yyyymmddHHmmss");
            }

            return sdf.format(Calendar.getInstance().getTime());
        }

        private class LabelledTextPanel extends JPanel {
            private JTextField textField;

            public LabelledTextPanel(String var2, int var3, boolean var4) {
                this.add(new JLabel(var2), "West");
                this.textField = new JTextField(var3);
                if (!var4) {
                    this.textField.setEditable(false);
                    this.textField.setHorizontalAlignment(0);
                }

                this.add(this.textField, "East");
            }

            public void updateText(String var1) {
                this.textField.setText(var1);
            }

            public String getText() {
                return this.textField.getText();
            }

            public void setEnabled(boolean var1) {
                this.textField.setEnabled(var1);
            }
        }
    }

    private static class GUIDemoAdapter implements GUIAdapter {
        private static int count = 1;
        private String trans;

        private GUIDemoAdapter() {
            this.trans = null;
        }

        public String[] search(String var1, String var2) {
            String[] var3 = new String[20];

            for(int var4 = 0; var4 < var3.length; ++var4) {
                var3[var4] = "" + (count + var4) + " -> " + var1 + "," + var2;
            }

            count += var3.length;
            return var3;
        }

        public String getInventoryCount() {
            return "" + count * 10;
        }

        public String getTransactionCompletedCount() {
            return "" + count * 2;
        }

        public String getTransactionInProgressCount() {
            return "" + count / 2;
        }

        public String getTransactionType(String var1) {
            return var1 == null ? "" : "Unknown" + count % 10;
        }

        public String getTransactionItemCount(String var1) {
            return var1 == null ? "" : "" + count / 5;
        }

        public String getTransactionQuantity(String var1) {
            return var1 == null ? "" : "" + count / 3;
        }

        public String getTransactionCost(String var1) {
            return var1 == null ? "" : "" + count * 100;
        }

        public String createTransaction(int var1, String var2) {
            this.trans = "New transaction type: " + var2 + " from client: " + var1 + " at " + GUI.client.getTimeStamp() + "\n";
            return "" + count;
        }

        public String addToTransaction(int var1, String var2, String var3, String var4) {
            this.trans = this.trans + "\nAdded to: " + var2 + " -> " + var3 + " (" + var4 + ")";
            return null;
        }

        public void endTransaction(int var1, String var2, String var3) {
            this.trans = this.trans + "\n\nEnding: " + var3 + " -> " + var2;
        }

        public String getTransactionDetails(String var1) {
            return this.trans;
        }

        public void newClient(int var1, GUIClient var2) {
            System.out.println("adding a new client id=" + var1);
        }
    }
}
