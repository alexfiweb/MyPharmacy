package alexfiweb.myPharmacy;

public class Product {
    private String id;
    private String name;
    private String description;
    private String ref;
    private String image;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name.substring(0,1).toUpperCase() + name.substring(1).toLowerCase();
    }

    public void setName(String name) {
        this.name = name.substring(0,1).toUpperCase() + name.substring(1).toLowerCase();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
