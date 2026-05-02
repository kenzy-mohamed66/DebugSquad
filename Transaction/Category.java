package Transaction;

import java.io.Serializable;

public class Category implements Serializable {

    private int     categoryId;
    private String  name;
    private boolean isCustom;
    private String  iconUrl;

    public Category() {}

    public Category(String name, boolean isCustom) {
        this.name     = name;
        this.isCustom = isCustom;
        this.iconUrl  = "";
    }

    public Category(String name, boolean isCustom, String iconUrl) {
        this.name     = name;
        this.isCustom = isCustom;
        this.iconUrl  = iconUrl;
    }

    public int     getCategoryId() { return categoryId; }
    public void    setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String  getName()       { return name; }
    public void    setName(String name) { this.name = name; }

    public boolean isCustom()      { return isCustom; }
    public void    setCustom(boolean isCustom) { this.isCustom = isCustom; }

    public String  getIconUrl()    { return iconUrl; }
    public void    setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }

    @Override
    public String toString() { return name; }
}
