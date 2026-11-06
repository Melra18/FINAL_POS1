package com.pos.model;

public class CartItem {
    private final int productId;
    private final String productName;
    private final String unitType; // "Piece" or "Bulk"
    private int quantity;
    private final double unitPrice;
    private final int piecesPerBulk;
    private final Product product; 

    // Kept for compatibility. Note: product stays null with this constructor.
    public CartItem(int productId, String productName, String unitType,
                    int quantity, double unitPrice, int piecesPerBulk) {
        this.product = null;
        this.productId = productId;
        this.productName = productName;
        this.unitType = normalizeUnit(unitType);
        this.quantity = Math.max(1, quantity);
        this.unitPrice = unitPrice;
        this.piecesPerBulk = piecesPerBulk > 0 ? piecesPerBulk : 1;
    }

    public CartItem(Product product, int quantity) {
        this(product, "Piece", quantity);
    }

    public CartItem(Product product, String unitType, int quantity) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null.");
        }
        this.product = product;
        this.productId = product.getProductId();
        this.productName = product.getProductName();
        this.unitType = normalizeUnit(unitType);
        this.quantity = Math.max(1, quantity);
        this.piecesPerBulk = product.getPiecesPerBulk() > 0 ? product.getPiecesPerBulk() : 1;
        this.unitPrice = isBulk() ? product.getPricePerBulk() : product.getPricePerPiece();
    }

    private static String normalizeUnit(String unitType) {
        return "Bulk".equalsIgnoreCase(unitType) ? "Bulk" : "Piece";
    }

    public boolean isBulk() {
        return "Bulk".equals(unitType);
    }

    public int getProductId() { return productId; }
    public String getProductName() { return productName; }
    public String getUnitType() { return unitType; }
    public int getQuantity() { return quantity; }

    public void setQuantity(int quantity) {
        if (quantity < 1) {
            throw new IllegalArgumentException("Quantity must be at least 1.");
        }
        this.quantity = quantity;
    }

    public double getUnitPrice() { return unitPrice; }
    public Product getProduct() { return product; }

    /** Name shown in the cart, e.g. "Vitamin C Serum (Bulk)". */
    public String getDisplayName() {
        return isBulk() ? productName + " (Bulk)" : productName;
    }

    public double getSubtotal() {
        return Math.round(quantity * unitPrice * 100.0) / 100.0;
    }

    public int getTotalPiecesToDeduct() {
        return isBulk() ? quantity * piecesPerBulk : quantity;
    }
}