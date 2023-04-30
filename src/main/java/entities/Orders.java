package entities;

import jakarta.persistence.*;

@Entity
public class Orders {
    @Id
    @Column(name = "id")
    private int id;
    @Basic
    @Column(name = "product_names")
    private String productNames;
    @Basic
    @Column(name = "status")
    private String status;
    @Basic
    @Column(name = "customer_id")
    private int customerId;
    @Basic
    @Column(name = "customer_name")
    private String customerName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProductNames() {
        return productNames;
    }

    public void setProductNames(String productNames) {
        this.productNames = productNames;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Orders orders = (Orders) o;

        if (id != orders.id) return false;
        if (customerId != orders.customerId) return false;
        if (productNames != null ? !productNames.equals(orders.productNames) : orders.productNames != null)
            return false;
        if (status != null ? !status.equals(orders.status) : orders.status != null) return false;
        if (customerName != null ? !customerName.equals(orders.customerName) : orders.customerName != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (productNames != null ? productNames.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + customerId;
        result = 31 * result + (customerName != null ? customerName.hashCode() : 0);
        return result;
    }
}
