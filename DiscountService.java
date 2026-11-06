
package com.pos.service;

import com.pos.dao.DiscountDAO;
import com.pos.model.Discount;
import com.pos.model.User;

import java.util.Collections;
import java.util.List;

public class DiscountService {

    private final DiscountDAO discountDAO;

    public DiscountService() {
        this.discountDAO = new DiscountDAO();
    }

    // Only Store Manager and Admin can change discount settings.
    private boolean canManageDiscounts(User user) {
        if (user == null || user.getRole() == null) {
            return false;
        }

        String role = user.getRole().trim();

        return role.equalsIgnoreCase("Store Manager")
                || role.equalsIgnoreCase("Admin");
    }

    // Logged-in users can view active discount options.
    public List<Discount> getActiveDiscounts(User user) {
        if (user == null || user.isArchived()) {
            return Collections.emptyList();
        }

        return discountDAO.getActiveDiscounts();
    }

    // Add a discount.
    public boolean addDiscount(
            User manager, String name, double percent) {

        if (!canManageDiscounts(manager)) {
            return false;
        }

        return discountDAO.addDiscount(name, percent);
    }

    // Update an existing active discount.
    public boolean updateDiscount(
            User manager,
            int discountId,
            String name,
            double percent) {

        if (!canManageDiscounts(manager)) {
            return false;
        }

        if (discountId <= 0) {
            return false;
        }

        return discountDAO.updateDiscount(
                discountId, name, percent);
    }

    // Archive an existing active discount.
    public boolean archiveDiscount(
            User manager, int discountId) {

        if (!canManageDiscounts(manager)) {
            return false;
        }

        if (discountId <= 0) {
            return false;
        }

        return discountDAO.archiveDiscount(discountId);
    }
}
