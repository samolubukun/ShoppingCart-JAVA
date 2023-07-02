import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ShoppingCartSystem {
    private JFrame frame;
    private List<Product> products;
    private List<Product> cart;

    public ShoppingCartSystem() {
        frame = new JFrame("E-commerce Shopping Cart");
        frame.setSize(400, 200);
        frame.setLayout(new FlowLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton browseProductsButton = new JButton("Browse Products");
        JButton viewCartButton = new JButton("View Cart");
        JButton checkoutButton = new JButton("Proceed to Checkout");

        browseProductsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                browseProducts();
            }
        });

        viewCartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewCart();
            }
        });

        checkoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkout();
            }
        });

        frame.add(browseProductsButton);
        frame.add(viewCartButton);
        frame.add(checkoutButton);
        frame.setVisible(true);

        // Initialize products and cart
        products = new ArrayList<>();
        products.add(new Product("iPhone 12", 999, 50));
        products.add(new Product("Samsung Galaxy S21", 899, 30));
        products.add(new Product("Google Pixel 5", 699, 20));
        products.add(new Product("iPad Pro", 1099, 40));
        products.add(new Product("MacBook Pro", 1999, 15));
        products.add(new Product("Dell XPS 15", 1599, 25));
        products.add(new Product("Sony PlayStation 5", 499, 10));
        products.add(new Product("Xbox Series X", 499, 8));
        products.add(new Product("Nintendo Switch", 299, 50));
        products.add(new Product("Sony 65-Inch 4K Smart TV", 1299, 12));
        products.add(new Product("LG 55-Inch OLED TV", 1499, 18));
        products.add(new Product("Bose QuietComfort 35 II", 349, 30));
        products.add(new Product("Apple AirPods Pro", 249, 40));
        products.add(new Product("Fitbit Charge 4", 129, 60));
        products.add(new Product("Amazon Echo Dot", 39, 100));

        cart = new ArrayList<>();
    }

    private void browseProducts() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Create checkboxes for each product
        List<JCheckBox> checkboxes = new ArrayList<>();
        for (Product product : products) {
            JCheckBox checkbox = new JCheckBox(product.getProductDetails());
            checkboxes.add(checkbox);
            panel.add(checkbox);
        }

        // Display the checkboxes in a scrollable dialog
        int result = JOptionPane.showConfirmDialog(frame, new JScrollPane(panel), "Select Products",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            // Add selected products to the cart
            cart.clear();
            for (JCheckBox checkbox : checkboxes) {
                if (checkbox.isSelected()) {
                    String productDetails = checkbox.getText();
                    Product selectedProduct = getProductFromDetails(productDetails);
                    if (selectedProduct != null) {
                        cart.add(selectedProduct);
                    }
                }
            }
            JOptionPane.showMessageDialog(frame, "Selected products added to cart.");
        }
    }

    private Product getProductFromDetails(String productDetails) {
        for (Product product : products) {
            if (product.getProductDetails().equals(productDetails)) {
                return product;
            }
        }
        return null;
    }

    private void viewCart() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Your cart is empty.");
        } else {
            StringBuilder cartText = new StringBuilder("Cart Items:\n");
            for (Product product : cart) {
                cartText.append(product.getProductDetails()).append("\n");
            }
            JOptionPane.showMessageDialog(frame, cartText.toString());
        }
    }

    private void checkout() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Your cart is empty. Please add items to your cart before proceeding to checkout.");
            return;
        }

        String customerName = null;
        while (true) {
            customerName = JOptionPane.showInputDialog(frame, "Enter your name:");
            if (customerName == null) {
                // User clicked Cancel, exit the method
                return;
            }

            // Validate the customer name
            if (!customerName.trim().isEmpty() && customerName.matches("^[a-zA-Z]+$")) {
                break;  // Name is valid, exit the loop
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid name. Please enter a non-empty name without numbers or special characters.");
            }
        }


        String shippingAddress = JOptionPane.showInputDialog(frame, "Enter your shipping address:");
        if (shippingAddress == null || shippingAddress.trim().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Invalid shipping address. Please enter a valid address.");
            return;
        }

        int totalCost = calculateTotalCost();
        String orderSummary = generateOrderSummary(customerName, shippingAddress, totalCost);
        StringBuilder cartItems = new StringBuilder("Selected Products:\n");
        for (Product product : cart) {
            cartItems.append(product.getProductDetails()).append("\n");
        }
        String orderDetails = orderSummary + "\n" + cartItems.toString();
        JOptionPane.showMessageDialog(frame, orderDetails);

        writeOrderToFile(orderDetails);

        clearCart();
        JOptionPane.showMessageDialog(frame, "Thank you for your order! Your items will be shipped to the provided address.");
    }


    private int calculateTotalCost() {
        int totalCost = 0;
        for (Product product : cart) {
            totalCost += product.getPrice();
        }
        return totalCost;
    }

    private String generateOrderSummary(String customerName, String shippingAddress, int totalCost) {
        StringBuilder orderSummary = new StringBuilder("Order Summary:\n");
        orderSummary.append("Customer Name: ").append(customerName).append("\n");
        orderSummary.append("Shipping Address: ").append(shippingAddress).append("\n");
        orderSummary.append("Total Cost: $").append(totalCost).append("\n");
        return orderSummary.toString();
    }

    private void writeOrderToFile(String orderSummary) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("order.txt", true))) {
            writer.write(orderSummary);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void clearCart() {
        cart.clear();
    }

    private class Product {
        private String name;
        private int price;
        private int quantity;

        public Product(String name, int price, int quantity) {
            this.name = name;
            this.price = price;
            this.quantity = quantity;
        }

        public String getName() {
            return name;
        }

        public int getPrice() {
            return price;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public String getProductDetails() {
            return name + " - $" + price;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ShoppingCartSystem();
            }
        });
    }
}
