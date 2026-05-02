package Models;

public class Category {
    private int categoryId;
    private String name;
    private boolean isCustom;
    private String iconName;
    
    // Constructor
    public Category() {}
    
    public Category(String name, boolean isCustom, String iconName) {
        this.name = name;
        this.isCustom = isCustom;
        this.iconName = iconName;
    }
    
    // Getters and Setters
    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public boolean isCustom() { return isCustom; }
    public void setCustom(boolean isCustom) { this.isCustom = isCustom; }
    
    public String getIconName() { return iconName; }
    public void setIconName(String iconName) { this.iconName = iconName; }
}
    

