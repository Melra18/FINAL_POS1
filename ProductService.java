package com.pos.service;

import com.pos.dao.ProductDAO;
import com.pos.model.Product;

import java.sql.SQLException;

public class ProductService {

    private final ProductDAO productDAO;

    public ProductService() {
        this.productDAO = new ProductDAO();
    }

    /**
     * Adds a new product after validating business rules.
     */
  public boolean addProduct(Product product) {

    validateProduct(product);

    try {

        String barcode = product.getBarcode();

        if (barcode != null && !barcode.trim().isEmpty()) {

            barcode = barcode.trim();

            if (!barcode.matches("\\d{8,14}")) {
                throw new IllegalArgumentException(
                        "Barcode must contain 8 to 14 digits."
                );
            }

            if (productDAO.barcodeExists(barcode)) {
                throw new IllegalArgumentException(
                        "This barcode is already assigned to another product."
                );
            }

            product.setBarcode(barcode);
        }

        int productId = productDAO.insert(product);

        return productId > 0;

    } catch (SQLException e) {

        throw new RuntimeException(
                "Unable to save product: "
                + e.getMessage(),
                e
        );
    }
}

    /**
     * Retrieves a product by ID.
     */
    public Product getProduct(int productId) {

        if (productId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid product ID."
            );
        }

        try {

            return productDAO.findById(productId);

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Unable to retrieve product: " + e.getMessage(),
                    e
            );
        }
    }

    /**
     * Archives a product.
     */
    public boolean archiveProduct(int productId) {

        if (productId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid product ID."
            );
        }

        try {

            return productDAO.archive(productId);

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Unable to archive product: " + e.getMessage(),
                    e
            );
        }
    }

    /**
     * Restores an archived product.
     */
    public boolean restoreProduct(int productId) {

        if (productId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid product ID."
            );
        }

        try {

            return productDAO.restore(productId);

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Unable to restore product: " + e.getMessage(),
                    e
            );
        }
    }

    /**
     * Validates product business rules.
     */
    private void validateProduct(Product product) {

        if (product == null) {
            throw new IllegalArgumentException(
                    "Product cannot be null."
            );
        }

        if (product.getProductName() == null ||
            product.getProductName().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Product name is required."
            );
        }

        if (product.getCategoryId() <= 0) {
            throw new IllegalArgumentException(
                    "Please select a valid category."
            );
        }

        if (product.getSupplierId() <= 0) {
            throw new IllegalArgumentException(
                    "Please select a valid supplier."
            );
        }

        if (product.getPiecesPerBulk() <= 0) {
            throw new IllegalArgumentException(
                    "Pieces per bulk must be greater than zero."
            );
        }

        if (product.getPricePerPiece() < 0) {
            throw new IllegalArgumentException(
                    "Price per piece cannot be negative."
            );
        }

        if (product.getPricePerBulk() < 0) {
            throw new IllegalArgumentException(
                    "Price per bulk cannot be negative."
            );
        }
    }
}