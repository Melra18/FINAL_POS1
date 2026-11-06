package com.pos.dao;

import com.pos.config.ConnectDb;
import com.pos.model.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    /**
     * Inserts a new product and returns the generated product ID.
     */
   /**
 * Inserts a new product and returns the generated product ID.
 */
public int insert(Product product) throws SQLException {

    String sql =
            "INSERT INTO products "
            + "(barcode, product_name, category_id, "
            + "pieces_per_bulk, price_per_piece, price_per_bulk, "
            + "image_path, status) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, 'Active')";

    try (Connection conn = ConnectDb.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(
                 sql,
                 java.sql.Statement.RETURN_GENERATED_KEYS)) {

        // Barcode
        if (product.getBarcode() == null
                || product.getBarcode().trim().isEmpty()) {

            pstmt.setNull(1, java.sql.Types.VARCHAR);

        } else {

            pstmt.setString(1, product.getBarcode().trim());
        }

        // Product information
        pstmt.setString(2, product.getProductName().trim());
        pstmt.setInt(3, product.getCategoryId());
        pstmt.setInt(4, product.getPiecesPerBulk());
        pstmt.setDouble(5, product.getPricePerPiece());
        pstmt.setDouble(6, product.getPricePerBulk());

        // Image
        if (product.getImagePath() == null
                || product.getImagePath().trim().isEmpty()) {

            pstmt.setNull(7, java.sql.Types.VARCHAR);

        } else {

            pstmt.setString(7, product.getImagePath().trim());
        }

        pstmt.executeUpdate();

        try (ResultSet rs = pstmt.getGeneratedKeys()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        }

        throw new SQLException(
                "Failed to retrieve the generated product ID."
        );
    }
}

    /**
     * Updates an active product.
     */
    public boolean update(Product product) throws SQLException {

        String sql =
                "UPDATE products SET "
                + "product_name = ?, "
                + "category_id = ?, "
                + "pieces_per_bulk = ?, "
                + "price_per_piece = ?, "
                + "price_per_bulk = ?, "
                + "image_path = ? "
                + "WHERE product_id = ? "
                + "AND status = 'Active'";

        try (Connection conn = ConnectDb.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(
                    1,
                    product.getProductName().trim()
            );

            pstmt.setInt(
                    2,
                    product.getCategoryId()
            );

            pstmt.setInt(
                    3,
                    product.getPiecesPerBulk()
            );

            pstmt.setDouble(
                    4,
                    product.getPricePerPiece()
            );

            pstmt.setDouble(
                    5,
                    product.getPricePerBulk()
            );

            pstmt.setString(
                    6,
                    product.getImagePath() == null
                            || product.getImagePath().trim().isEmpty()
                            ? null
                            : product.getImagePath()
            );

            pstmt.setInt(
                    7,
                    product.getProductId()
            );

            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Retrieves all active products with their current valid stock.
     *
     * Expired batches are excluded from available stock.
     */
    public List<Product> findAllActive() throws SQLException {

        List<Product> products = new ArrayList<>();

        String sql =
                "SELECT "
                + "p.product_id, "
                + "p.product_name, "
                + "p.category_id, "
                + "p.supplier_id, "
                + "p.pieces_per_bulk, "
                + "p.price_per_piece, "
                + "p.price_per_bulk, "
                + "c.category_name, "

                + "COALESCE(SUM(CASE "
                + "WHEN b.status = 'Available' "
                + "AND b.remaining_pieces > 0 "
                + "AND b.expiration_date >= CURRENT_DATE() "
                + "THEN b.remaining_pieces "
                + "ELSE 0 END), 0) AS total_stock, "

                + "MIN(CASE "
                + "WHEN b.status = 'Available' "
                + "AND b.remaining_pieces > 0 "
                + "AND b.expiration_date >= CURRENT_DATE() "
                + "THEN b.expiration_date "
                + "ELSE NULL END) AS earliest_expiration "

                + "FROM products p "

                + "LEFT JOIN categories c "
                + "ON p.category_id = c.category_id "

                + "LEFT JOIN inventory_batches b "
                + "ON p.product_id = b.product_id "

                + "WHERE p.status = 'Active' "

                + "GROUP BY "
                + "p.product_id, "
                + "p.product_name, "
                + "p.category_id, "
                + "p.supplier_id, "
                + "p.pieces_per_bulk, "
                + "p.price_per_piece, "
                + "p.price_per_bulk, "
                + "c.category_name "

                + "ORDER BY p.product_id ASC";

        try (Connection conn = ConnectDb.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {

                Product product = new Product();

                product.setProductId(
                        rs.getInt("product_id")
                );

                product.setProductName(
                        rs.getString("product_name")
                );

                product.setCategoryId(
                        rs.getInt("category_id")
                );

                product.setSupplierId(
                        rs.getInt("supplier_id")
                );

                product.setPiecesPerBulk(
                        rs.getInt("pieces_per_bulk")
                );

                product.setPricePerPiece(
                        rs.getDouble("price_per_piece")
                );

                product.setPricePerBulk(
                        rs.getDouble("price_per_bulk")
                );

                product.setCategoryName(
                        rs.getString("category_name")
                );

                product.setTotalStock(
                        rs.getInt("total_stock")
                );

                product.setEarliestExpiration(
                        rs.getDate("earliest_expiration")
                );

                products.add(product);
            }
        }

        return products;
    }

    /**
     * Checks whether a barcode already exists.
     *
     * Kept for future barcode functionality.
     */
    public boolean barcodeExists(String barcode) throws SQLException {

        if (barcode == null || barcode.trim().isEmpty()) {
            return false;
        }

        String sql =
                "SELECT COUNT(*) "
                + "FROM products "
                + "WHERE barcode = ?";

        try (Connection conn = ConnectDb.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, barcode.trim());

            try (ResultSet rs = pstmt.executeQuery()) {

                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    /**
     * Finds one product by ID.
     */
    public Product findById(int productId) throws SQLException {

        String sql =
                "SELECT product_id, barcode, product_name, "
                + "category_id, supplier_id, pieces_per_bulk, "
                + "price_per_piece, price_per_bulk, "
                + "status, image_path "
                + "FROM products "
                + "WHERE product_id = ?";

        try (Connection conn = ConnectDb.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, productId);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {
                    return mapProduct(rs);
                }

                return null;
            }
        }
    }

    /**
     * Archives a product.
     */
    public boolean archive(int productId) throws SQLException {

        String sql =
                "UPDATE products "
                + "SET status = 'Archived' "
                + "WHERE product_id = ?";

        try (Connection conn = ConnectDb.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, productId);

            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Restores an archived product.
     */
    public boolean restore(int productId) throws SQLException {

        String sql =
                "UPDATE products "
                + "SET status = 'Active' "
                + "WHERE product_id = ?";

        try (Connection conn = ConnectDb.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, productId);

            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Converts a database row into a Product object.
     */
    private Product mapProduct(ResultSet rs) throws SQLException {

        Product product = new Product();

        product.setProductId(
                rs.getInt("product_id")
        );

        product.setBarcode(
                rs.getString("barcode")
        );

        product.setProductName(
                rs.getString("product_name")
        );

        product.setCategoryId(
                rs.getInt("category_id")
        );

        product.setSupplierId(
                rs.getInt("supplier_id")
        );

        product.setPiecesPerBulk(
                rs.getInt("pieces_per_bulk")
        );

        product.setPricePerPiece(
                rs.getDouble("price_per_piece")
        );

        product.setPricePerBulk(
                rs.getDouble("price_per_bulk")
        );

        product.setStatus(
                rs.getString("status")
        );

        product.setImagePath(
                rs.getString("image_path")
        );

        return product;
    }
}