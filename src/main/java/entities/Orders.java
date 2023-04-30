package entities;

import jakarta.persistence.*;

@Entity
public class Orders {
    @Id
    @Column(name = "id", nullable = false)
    private int id;
    @Basic
    @Column(name = "product_names", nullable = false, length = 255)
    private String productNames;
    @Basic
    @Column(name = "status", nullable = false, length = 45)
    private String status;
    @Basic
    @Column(name = "customer_id", nullable = false)
    private int customerId;
    @Basic
    @Column(name = "shipping_id", nullable = true)
    private Integer shippingId;

    @JoinColumn(name = "shipping_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Shippingcompany shippingcompany;

    @JoinColumn(name = "customer_id", referencedColumnName = "custId", nullable = false, insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Customer customer;
    @Basic
    @Column(name = "product_id", nullable = false)
    private int productId;

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
    
    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Integer getShippingId() {
        return shippingId;
    }

    public void setShippingId(Integer shippingId) {
        this.shippingId = shippingId;
    }

    public Shippingcompany getShippingcompany() {
        return shippingcompany;
    }

    public void setShippingcompany(Shippingcompany shippingcompany) {
        this.shippingcompany = shippingcompany;
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

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (productNames != null ? productNames.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + customerId;
        return result;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }
}
