package alexfiweb.myPharmacy;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String userId;
    private List<String> inventoryProducts = new ArrayList<>();

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<String> getInventoryProducts() {
        return inventoryProducts;
    }

    public void setInventoryProducts(List<String> inventoryProducts) {
        this.inventoryProducts = inventoryProducts;
    }
}
