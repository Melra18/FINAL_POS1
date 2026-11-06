package com.pos.model;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.Map;

public class Cart {

    private static final double VAT_RATE = 0.12;

    // Product + unit type is the cart key so Piece and Bulk can coexist.
    private final Map<String, CartItem> items = new LinkedHashMap<>();

    // Remembers the order items were added (used by Ctrl+Z)
    private final Deque<String> undoStack = new ArrayDeque<>();

    private Discount discount = new NoDiscount();

    // =====================================================
    // ADD / INCREASE / DECREASE
    // =====================================================

    public boolean add(Product product) {
        return add(product, "Piece", 1);
    }

    public boolean add(Product product, String unitType, int quantity) {
        if (product == null || quantity <= 0) {
            return false;
        }

        String type = "Bulk".equalsIgnoreCase(unitType) ? "Bulk" : "Piece";
        int piecesRequired = "Bulk".equals(type) ? product.getPiecesPerBulk() : 1;
        int additionalPieces = quantity * piecesRequired;

        String key = makeKey(product.getProductId(), type);
        CartItem existing = items.get(key);
        int currentPieces = (existing == null) ? 0 : existing.getTotalPiecesToDeduct();

        // Not enough stock
        if (currentPieces + additionalPieces > product.getStockQuantity()) {
            return false;
        }

        if (existing == null) {
            items.put(key, new CartItem(product, type, quantity));
        } else {
            existing.setQuantity(existing.getQuantity() + quantity);
        }

        undoStack.push(key);
        return true;
    }

    /** + button: adds one more of this item (checks stock, records for undo). */
    public boolean increase(CartItem item) {
        if (item == null || item.getProduct() == null) {
            return false;
        }
        return add(item.getProduct(), item.getUnitType(), 1);
    }

    /** - button: removes one, but never goes below 1 (use remove() to delete). */
    public boolean decrease(CartItem item) {
        if (item == null || item.getQuantity() <= 1) {
            return false;
        }
        item.setQuantity(item.getQuantity() - 1);
        return true;
    }

    private String makeKey(int productId, String unitType) {
        return productId + "|" + unitType;
    }

    // =====================================================
    // UNDO LAST CART ACTION
    // =====================================================

    public boolean undoLast() {
        // Skip stale entries (items already removed or reduced elsewhere)
        while (!undoStack.isEmpty()) {
            String key = undoStack.pop();
            CartItem item = items.get(key);

            if (item == null) {
                continue;
            }

            if (item.getQuantity() <= 1) {
                items.remove(key);
            } else {
                item.setQuantity(item.getQuantity() - 1);
            }
            return true;
        }
        return false;
    }

    // =====================================================
    // REMOVE
    // =====================================================

    public boolean remove(int index) {
        if (index < 0 || index >= items.size()) {
            return false;
        }
        String key = items.keySet().toArray(new String[0])[index];
        items.remove(key);
        undoStack.removeIf(key::equals);
        return true;
    }

    public boolean remove(CartItem item) {
        if (item == null) {
            return false;
        }

        String keyToRemove = null;
        for (Map.Entry<String, CartItem> entry : items.entrySet()) {
            if (entry.getValue() == item) {
                keyToRemove = entry.getKey();
                break;
            }
        }

        if (keyToRemove == null) {
            return false;
        }

        items.remove(keyToRemove);
        final String k = keyToRemove;
        undoStack.removeIf(k::equals);
        return true;
    }

    public void clear() {
        items.clear();
        undoStack.clear();
        discount = new NoDiscount();
    }

    // =====================================================
    // DISCOUNT
    // =====================================================

    public void setDiscount(Discount discount) {
        this.discount = (discount == null) ? new NoDiscount() : discount;
    }

    public Discount getDiscount() {
        return discount;
    }

    // =====================================================
    // ITEMS
    // =====================================================

    public Collection<CartItem> getItems() {
        return items.values();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    // =====================================================
    // MONEY
    // Prices are VAT-inclusive.
    //  - Regular sale : total = subtotal (VAT is already inside it)
    //  - Senior / PWD : VAT removed first, then 20% off the ex-VAT price
    // =====================================================

    private static double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private boolean isVatExempt() {
        return discount.getRate() > 0;
    }

    public double getSubtotal() {
        double subtotal = 0;
        for (CartItem item : items.values()) {
            subtotal += item.getSubtotal();
        }
        return round2(subtotal);
    }

    /** Price without VAT. */
    public double getVatableSales() {
        return round2(getSubtotal() / (1 + VAT_RATE));
    }

    /** Discount amount, computed on the ex-VAT price. */
    public double getDiscountAmount() {
        return round2(discount.compute(getVatableSales()));
    }

    /** VAT that is included in the price (0 when the sale is VAT-exempt). */
    public double getVat() {
        return isVatExempt() ? 0.0 : round2(getSubtotal() - getVatableSales());
    }

    /** VAT removed because of the exemption (0 for regular sales). */
    public double getVatExemption() {
        return isVatExempt() ? round2(getSubtotal() - getVatableSales()) : 0.0;
    }

    public double getTotal() {
        return isVatExempt()
                ? round2(getVatableSales() - getDiscountAmount())
                : getSubtotal();
    }
}