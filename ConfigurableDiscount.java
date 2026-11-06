
package com.pos.model;

public class ConfigurableDiscount extends Discount {

    private final String name;
    private final double rate;

    public ConfigurableDiscount(String name, double percent) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Discount name cannot be empty."
            );
        }

        if (!Double.isFinite(percent)
                || percent < 0.0
                || percent > 100.0) {
            throw new IllegalArgumentException(
                "Discount must be between 0 and 100 percent."
            );
        }

        this.name = name.trim();
        this.rate = percent / 100.0;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double getRate() {
        return rate;
    }

    @Override
    public String toString() {
        return name + " (" + (rate * 100) + "%)";
    }
}
