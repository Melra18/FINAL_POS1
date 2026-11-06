
package com.pos.service;

import com.pos.model.Product;
import java.util.Comparator;

/**
 * Abstraction: defines the contract for product sorting.
 */
public abstract class ProductSortStrategy {

    public abstract Comparator<Product> getComparator();

    /**
     * Factory method: selects the appropriate sorting strategy.
     */
    public static ProductSortStrategy create(String sortBy) {

        if (sortBy == null) {
            return new ProductIdStrategy();
        }

        switch (sortBy) {
            case "Product Name":
                return new ProductNameStrategy();

            case "Price (Piece)":
                return new PiecePriceStrategy();

            case "Price (Bulk)":
                return new BulkPriceStrategy();

            case "Total Stock":
                return new StockStrategy();

            case "Earliest Expiry":
                return new ExpirationStrategy();

            default:
                return new ProductIdStrategy();
        }
    }

    // Inheritance and polymorphism:
    // Each strategy provides its own implementation.

    private static final class ProductNameStrategy
            extends ProductSortStrategy {

        @Override
        public Comparator<Product> getComparator() {
            return Comparator.comparing(
                Product::getProductName,
                Comparator.nullsLast(
                    String.CASE_INSENSITIVE_ORDER
                )
            );
        }
    }

    private static final class PiecePriceStrategy
            extends ProductSortStrategy {

        @Override
        public Comparator<Product> getComparator() {
            return Comparator.comparingDouble(
                Product::getPricePerPiece
            );
        }
    }

    private static final class BulkPriceStrategy
            extends ProductSortStrategy {

        @Override
        public Comparator<Product> getComparator() {
            return Comparator.comparingDouble(
                Product::getPricePerBulk
            );
        }
    }

    private static final class StockStrategy
            extends ProductSortStrategy {

        @Override
        public Comparator<Product> getComparator() {
            return Comparator.comparingInt(
                Product::getTotalStock
            );
        }
    }

    private static final class ExpirationStrategy
            extends ProductSortStrategy {

        @Override
        public Comparator<Product> getComparator() {
            return Comparator.comparing(
                Product::getEarliestExpiration,
                Comparator.nullsLast(
                    Comparator.naturalOrder()
                )
            );
        }
    }

    private static final class ProductIdStrategy
            extends ProductSortStrategy {

        @Override
        public Comparator<Product> getComparator() {
            return Comparator.comparingInt(
                Product::getProductId
            );
        }
    }
}
