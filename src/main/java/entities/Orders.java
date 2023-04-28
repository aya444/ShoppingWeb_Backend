package entities;

import jakarta.persistence.*;

import java.util.List;
import java.util.Set;

@Entity
public class Orders {

    @Id
    @Column(name = "id", nullable = false)
    private int id;
//    @OneToMany(mappedBy = "orders",cascade = CascadeType.ALL)
//    private Set<Product> products;
    @Basic
    @Column(name = "state", nullable = false, length = 45)
    private String state;
    @Basic
    @Column(name = "customer_id", nullable = false)
    private int customerId;
    @Basic
    @Column(name = "product_names", nullable = false)
    private String productNames;

    public Orders() {
        super();
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Orders orders = (Orders) o;

        if (id != orders.id) return false;
        if (customerId != orders.customerId) return false;
        if (state != null ? !state.equals(orders.state) : orders.state != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (state != null ? state.hashCode() : 0);
        result = 31 * result + customerId;
        return result;
    }

    public String getProductNames() {
        return productNames;
    }

    public void setProductNames(String productNames) {
        this.productNames = productNames;
    }
}
