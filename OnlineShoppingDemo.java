import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects; // For equals and hashCode if custom ID logic is needed

// =============================================================================
// Enumerations
// =============================================================================

enum UserState {
    NEW, ACTIVE, BLOCKED, BANNED
}

enum OrderStatus {
    NEW, HOLD, SHIPPED, DELIVERED, CLOSED
}

// =============================================================================
// Classes
// =============================================================================

class WebUser {
    private String loggingId; // {id}
    private String password;
    private UserState state;

    public WebUser(String loggingId, String password) {
        this.loggingId = loggingId;
        this.password = password; // In a real system, hash passwords!
        this.state = UserState.NEW; // Default state
        System.out.println("WebUser " + loggingId + " created.");
    }

    // Getters
    public String getLoggingId() { return loggingId; }
    public String getPassword() { return password; } // Be cautious with returning plain password
    public UserState getState() { return state; }

    // Setters (for state changes)
    public void setState(UserState state) {
        this.state = state;
        System.out.println("WebUser " + loggingId + " state changed to " + state);
    }

    // Basic authentication (for demonstration)
    public boolean authenticate(String enteredPassword) {
        return this.password.equals(enteredPassword) && this.state == UserState.ACTIVE;
    }
}

class Customer {
    private String id; // {id}
    private String address; // Assuming simple string for Address
    private String phone;   // Assuming simple string for Phone
    private String email;

    // Association: Customer uses WebUser for login
    private WebUser webUser;

    // Association: Customer has a ShoppingCart
    private ShoppingCart shoppingCart;

    // Association: Customer has an Account (one-to-one)
    private Account account;

    public Customer(String id, String address, String phone, String email, WebUser webUser) {
        this.id = id;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.webUser = webUser;
        this.shoppingCart = new ShoppingCart(); // Customer typically has a shopping cart
        System.out.println("Customer " + id + " created.");
    }

    // Getters
    public String getId() { return id; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public WebUser getWebUser() { return webUser; }
    public ShoppingCart getShoppingCart() { return shoppingCart; }
    public Account getAccount() { return account; }

    // Setters
    public void setAddress(String address) { this.address = address; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setEmail(String email) { this.email = email; }
    public void setAccount(Account account) {
        this.account = account;
        System.out.println("Customer " + id + " linked to Account " + account.getId());
    }

    // Business methods
    public Order placeOrder(List<LineItem> items, String shippingAddress) {
        if (this.account == null) {
            System.out.println("Error: Customer " + id + " does not have an account to place an order.");
            return null;
        }
        Order newOrder = account.createOrder(items, shippingAddress, this); // Account places order on behalf of customer
        if (newOrder != null) {
            this.shoppingCart.clearCart(); // Clear cart after placing order
        }
        return newOrder;
    }

    public Payment makePayment(Order order, BigDecimal amount, String details) {
        if (this.account == null) {
            System.out.println("Error: Customer " + id + " does not have an account to make a payment.");
            return null;
        }
        return account.makePayment(order, amount, details);
    }
}

class ShoppingCart {
    private LocalDateTime created;
    private List<LineItem> lineItems; // {ordered, unique}

    public ShoppingCart() {
        this.created = LocalDateTime.now();
        this.lineItems = new ArrayList<>();
        System.out.println("ShoppingCart created at " + created);
    }

    // Getters
    public LocalDateTime getCreated() { return created; }
    public List<LineItem> getLineItems() {
        // Return an unmodifiable list to maintain uniqueness and order from outside
        return Collections.unmodifiableList(lineItems);
    }

    // Business methods
    public void addLineItem(Product product, int quantity) {
        // Enforce unique product per line item within cart
        for (LineItem item : lineItems) {
            if (item.getProduct().equals(product)) {
                item.setQuantity(item.getQuantity() + quantity);
                System.out.println("Updated quantity for " + product.getName() + " in cart.");
                return;
            }
        }
        LineItem newLineItem = new LineItem(quantity, product);
        this.lineItems.add(newLineItem);
        System.out.println("Added " + quantity + " of " + product.getName() + " to cart.");
    }

    public void removeLineItem(Product product) {
        lineItems.removeIf(item -> item.getProduct().equals(product));
        System.out.println("Removed " + product.getName() + " from cart.");
    }

    public BigDecimal calculateTotal() {
        return lineItems.stream()
                .map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void clearCart() {
        this.lineItems.clear();
        System.out.println("Shopping cart cleared.");
    }
}

class Account {
    private String id; // {id}
    private String billingAddress; // 'billing_address' - Assuming simple string for Address
    private boolean isClosed; // 'is_Closed'
    private LocalDate openDate; // 'open'
    private LocalDate closedDate; // 'Clased' (corrected to closedDate)

    // Association: Account makes Payments
    private List<Payment> payments;

    // Association: Account places Orders
    private List<Order> orders;

    public Account(String id, String billingAddress) {
        this.id = id;
        this.billingAddress = billingAddress;
        this.isClosed = false;
        this.openDate = LocalDate.now();
        this.payments = new ArrayList<>();
        this.orders = new ArrayList<>();
        System.out.println("Account " + id + " created.");
    }

    // Getters
    public String getId() { return id; }
    public String getBillingAddress() { return billingAddress; }
    public boolean isClosed() { return isClosed; }
    public LocalDate getOpenDate() { return openDate; }
    public LocalDate getClosedDate() { return closedDate; }
    public List<Payment> getPayments() { return new ArrayList<>(payments); }
    public List<Order> getOrders() { return new ArrayList<>(orders); }

    // Setters
    public void setBillingAddress(String billingAddress) { this.billingAddress = billingAddress; }
    public void closeAccount() {
        this.isClosed = true;
        this.closedDate = LocalDate.now();
        System.out.println("Account " + id + " closed.");
    }
    public void openAccount() {
        this.isClosed = false;
        this.closedDate = null;
        System.out.println("Account " + id + " reopened.");
    }

    // Business methods
    public Order createOrder(List<LineItem> items, String shippingAddress, Customer customer) {
        if (isClosed) {
            System.out.println("Error: Account " + id + " is closed. Cannot place order.");
            return null;
        }
        if (items == null || items.isEmpty()) {
            System.out.println("Error: Cannot place empty order.");
            return null;
        }
        Order newOrder = new Order("ORD-" + System.currentTimeMillis(), shippingAddress, items); // Simplified order number
        this.orders.add(newOrder);
        System.out.println("Account " + id + " placed new order " + newOrder.getNumber());
        return newOrder;
    }

    public Payment makePayment(Order order, BigDecimal amount, String details) {
        Payment newPayment = new Payment("PAY-" + System.currentTimeMillis(), LocalDate.now(), amount, details); // Simplified payment ID
        this.payments.add(newPayment);
        order.addPayment(newPayment); // Link payment to order
        System.out.println("Account " + id + " made payment " + newPayment.getId() + " for order " + order.getNumber());
        return newPayment;
    }
}

class LineItem {
    private int quantity;
    private BigDecimal price; // 'Price' - BigDecimal for currency
    private Product product;

    public LineItem(int quantity, Product product) {
        this.quantity = quantity;
        this.product = product;
        this.price = product.getBasePrice(); // Assume LineItem price comes from product at time of creation
        System.out.println("LineItem created for " + product.getName() + " (Qty: " + quantity + ").");
    }

    // Getters
    public int getQuantity() { return quantity; }
    public BigDecimal getPrice() { return price; }
    public Product getProduct() { return product; }

    // Setters
    public void setQuantity(int quantity) {
        this.quantity = quantity;
        System.out.println("LineItem quantity updated to " + quantity + " for " + product.getName());
    }

    // For {unique} in ShoppingCart and Order, override equals/hashCode based on Product
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LineItem lineItem = (LineItem) o;
        return Objects.equals(product, lineItem.product); // Uniqueness based on product
    }

    @Override
    public int hashCode() {
        return Objects.hash(product);
    }
}

class Product {
    private String id; // {id}
    private String name;
    private String supplier; // Assuming simple string for Supplier
    private BigDecimal basePrice; // Added for LineItem price reference

    public Product(String id, String name, String supplier, BigDecimal basePrice) {
        this.id = id;
        this.name = name;
        this.supplier = supplier;
        this.basePrice = basePrice;
        System.out.println("Product " + id + " ('" + name + "') created.");
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getSupplier() { return supplier; }
    public BigDecimal getBasePrice() { return basePrice; }

    // For {unique} LineItem logic, useful to have equals/hashCode for Product
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id); // Uniqueness based on product ID
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

class Order {
    private String number; // {id}
    private LocalDateTime orderedDate; // 'ordered'
    private LocalDateTime shippedDate; // 'shipped'
    private String shipToAddress; // 'ship_to' - Assuming simple string for Address
    private OrderStatus status;
    private BigDecimal total; // 'total' - BigDecimal for currency

    // Association: Order contains LineItems
    private List<LineItem> lineItems; // {ordered, unique}

    // Association: Order receives Payments
    private List<Payment> payments;

    public Order(String number, String shipToAddress, List<LineItem> items) {
        this.number = number;
        this.orderedDate = LocalDateTime.now();
        this.shipToAddress = shipToAddress;
        this.status = OrderStatus.NEW; // Default status
        this.lineItems = new ArrayList<>();
        this.payments = new ArrayList<>();
        // Add items, ensuring uniqueness and order as per diagram
        if (items != null) {
            for (LineItem item : items) {
                // To maintain "unique" in lineItems based on product,
                // we'll check if a line item for this product already exists.
                // If it does, update quantity; otherwise, add new line item.
                boolean found = false;
                for (LineItem existingItem : this.lineItems) {
                    if (existingItem.getProduct().equals(item.getProduct())) {
                        existingItem.setQuantity(existingItem.getQuantity() + item.getQuantity());
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    this.lineItems.add(item);
                }
            }
        }
        this.total = calculateTotal(); // Calculate total based on initial items
        System.out.println("Order " + number + " created.");
    }

    // Getters
    public String getNumber() { return number; }
    public LocalDateTime getOrderedDate() { return orderedDate; }
    public LocalDateTime getShippedDate() { return shippedDate; }
    public String getShipToAddress() { return shipToAddress; }
    public OrderStatus getStatus() { return status; }
    public BigDecimal getTotal() { return total; }
    public List<LineItem> getLineItems() {
        return Collections.unmodifiableList(lineItems);
    }
    public List<Payment> getPayments() { return new ArrayList<>(payments); }

    // Setters
    public void setStatus(OrderStatus status) {
        this.status = status;
        System.out.println("Order " + number + " status changed to " + status);
        if (status == OrderStatus.SHIPPED) {
            this.shippedDate = LocalDateTime.now();
        }
    }
    public void setShipToAddress(String shipToAddress) { this.shipToAddress = shipToAddress; }

    // Business methods
    private BigDecimal calculateTotal() {
        return lineItems.stream()
                .map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void addPayment(Payment payment) {
        if (!payments.contains(payment)) {
            this.payments.add(payment);
            System.out.println("Payment " + payment.getId() + " added to Order " + number);
        }
    }

    public void displayOrderDetails() {
        System.out.println("\n--- Order Details (Order #" + number + ") ---");
        System.out.println("Status: " + status);
        System.out.println("Ordered On: " + orderedDate);
        if (shippedDate != null) System.out.println("Shipped On: " + shippedDate);
        System.out.println("Ship To: " + shipToAddress);
        System.out.println("Total Amount: $" + total);
        System.out.println("Items:");
        lineItems.forEach(item ->
            System.out.println("  - " + item.getProduct().getName() + " (Qty: " + item.getQuantity() + ", Price: $" + item.getPrice() + ")")
        );
        System.out.println("Payments:");
        payments.forEach(payment ->
            System.out.println("  - ID: " + payment.getId() + ", Paid On: " + payment.getPaidDate() + ", Amount: $" + payment.getTotal())
        );
        System.out.println("-------------------------------------");
    }
}

class Payment {
    private String id; // {id}
    private LocalDate paidDate; // 'Paid'
    private BigDecimal total; // 'total' - BigDecimal for currency
    private String details;

    public Payment(String id, LocalDate paidDate, BigDecimal total, String details) {
        this.id = id;
        this.paidDate = paidDate;
        this.total = total;
        this.details = details;
        System.out.println("Payment " + id + " created.");
    }

    // Getters
    public String getId() { return id; }
    public LocalDate getPaidDate() { return paidDate; }
    public BigDecimal getTotal() { return total; }
    public String getDetails() { return details; }

    // For uniqueness in lists
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return Objects.equals(id, payment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

// =============================================================================
// Main Demonstration Class
// =============================================================================
public class OnlineShoppingDemo {
    public static void main(String[] args) {
        System.out.println("--- Starting Online Shopping System Demonstration ---");

        // 1. Create Products
        Product laptop = new Product("P001", "Gaming Laptop", "Tech Supplier Inc.", new BigDecimal("1200.00"));
        Product mouse = new Product("P002", "Wireless Mouse", "Accessory Co.", new BigDecimal("25.00"));
        Product keyboard = new Product("P003", "Mechanical Keyboard", "KeyPro", new BigDecimal("75.00"));

        // 2. Create Web User and Customer
        WebUser user1 = new WebUser("john.doe", "securepass");
        user1.setState(UserState.ACTIVE); // Activate user
        Customer customer1 = new Customer("CUST001", "123 Main St, Anytown", "555-1234", "john@example.com", user1);

        // 3. Create Account and link to Customer
        Account account1 = new Account("ACC001", "123 Main St, Anytown (Billing)");
        customer1.setAccount(account1);

        // 4. Customer adds items to Shopping Cart
        System.out.println("\n--- Customer Adds Items to Cart ---");
        ShoppingCart cart = customer1.getShoppingCart();
        cart.addLineItem(laptop, 1);
        cart.addLineItem(mouse, 2);
        cart.addLineItem(laptop, 1); // Add another laptop, should update quantity
        cart.addLineItem(keyboard, 1);
        System.out.println("Cart total: $" + cart.calculateTotal());
        System.out.println("Cart items count: " + cart.getLineItems().size()); // Should be 3 unique products

        // 5. Customer places an Order
        System.out.println("\n--- Customer Places Order ---");
        List<LineItem> itemsToOrder = new ArrayList<>(cart.getLineItems()); // Get items from cart
        Order order1 = customer1.placeOrder(itemsToOrder, "123 Main St, Anytown (Shipping)");
        if (order1 != null) {
            order1.displayOrderDetails();
            System.out.println("Cart after order placement: " + cart.getLineItems().size() + " items"); // Should be 0
        }

        // 6. Simulate Order Status Changes
        System.out.println("\n--- Order Status Changes ---");
        if (order1 != null) {
            order1.setStatus(OrderStatus.HOLD);
            order1.setStatus(OrderStatus.SHIPPED);
            order1.setStatus(OrderStatus.DELIVERED);
            order1.displayOrderDetails();
        }

        // 7. Customer makes a Payment for the Order
        System.out.println("\n--- Customer Makes Payment ---");
        if (order1 != null) {
            BigDecimal paymentAmount = new BigDecimal("1250.00");
            Payment payment1 = customer1.makePayment(order1, paymentAmount, "Credit Card Transaction");
            if (payment1 != null) {
                order1.displayOrderDetails(); // Display updated order details with payment
            }
        }


        // Test another customer and process
        System.out.println("\n--- Second Customer Scenario ---");
        WebUser user2 = new WebUser("jane.doe", "anotherpass");
        user2.setState(UserState.ACTIVE);
        Customer customer2 = new Customer("CUST002", "456 Oak Ave, Villagetown", "555-5678", "jane@example.com", user2);
        Account account2 = new Account("ACC002", "456 Oak Ave, Villagetown (Billing)");
        customer2.setAccount(account2);

        ShoppingCart cart2 = customer2.getShoppingCart();
        cart2.addLineItem(keyboard, 1);
        cart2.addLineItem(mouse, 1);

        Order order2 = customer2.placeOrder(new ArrayList<>(cart2.getLineItems()), "456 Oak Ave, Villagetown");
        if (order2 != null) {
            order2.displayOrderDetails();
            customer2.makePayment(order2, order2.getTotal(), "PayPal");
            order2.setStatus(OrderStatus.CLOSED); // Close order after payment
            order2.displayOrderDetails();
        }

        System.out.println("\n--- Online Shopping System Demonstration Complete ---");
    }
}