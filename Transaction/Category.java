package Transaction;

import java.io.Serializable;

/**
 * Represents a category for grouping transactions.
 *
 * <p>A category can be either a system default or a user-defined custom category.
 * Implements {@link Serializable} for data persistence.
 *
 * @author DebugSquad
 * @version 1.0
 */
public class Category implements Serializable {

    private int     categoryId;
    private String  name;
    private boolean isCustom;
    private String  iconUrl;

    /**
     * Constructs an empty {@code Category}.
     */
    public Category() {}

    /**
     * Constructs a new {@code Category} with a specific name.
     *
     * @param name     the name of the category
     * @param isCustom {@code true} if user-defined, {@code false} if default
     */
    public Category(String name, boolean isCustom) {
        this.name     = name;
        this.isCustom = isCustom;
        this.iconUrl  = "";
    }

    /**
     * Constructs a new {@code Category} with a specific name and icon.
     *
     * @param name     the name of the category
     * @param isCustom {@code true} if user-defined, {@code false} if default
     * @param iconUrl  the URL or path to the category icon
     */
    public Category(String name, boolean isCustom, String iconUrl) {
        this.name     = name;
        this.isCustom = isCustom;
        this.iconUrl  = iconUrl;
    }

    /** @return the category ID */
    public int     getCategoryId() { return categoryId; }
    /** @param categoryId the new category ID */
    public void    setCategoryId(int categoryId) { this.categoryId = categoryId; }

    /** @return the category name */
    public String  getName()       { return name; }
    /** @param name the new category name */
    public void    setName(String name) { this.name = name; }

    /** @return {@code true} if this is a custom category */
    public boolean isCustom()      { return isCustom; }
    /** @param isCustom {@code true} to mark as custom */
    public void    setCustom(boolean isCustom) { this.isCustom = isCustom; }

    /** @return the category icon URL */
    public String  getIconUrl()    { return iconUrl; }
    /** @param iconUrl the new icon URL */
    public void    setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }

    /**
     * Returns a string representation of this category.
     *
     * @return the category name
     */
    @Override
    public String toString() { return name; }
}
