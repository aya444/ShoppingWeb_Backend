package entities;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
public class Product implements Serializable {
    @Id
    @Column(name = "id", nullable = false)
    private int id;
    @Basic
    @Column(name = "name", nullable = false, length = 45)
    private String name;
    @Basic
    @Column(name = "price", nullable = false)
    private Integer price;
    @Basic
    @Column(name = "status", nullable = false, length = 45)
    private String status;
    @Basic
    @Column(name = "sellingcompany_id", nullable = false)
    private int sellingcompanyId;
    @JoinColumn(name = "sellingcompany_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Sellingcompany sellingcompany;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Sellingcompany getSellingcompany() {
        return sellingcompany;
    }

    public void setSellingcompany(Sellingcompany sellingcompany) {
        this.sellingcompany = sellingcompany;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Product product = (Product) o;

        if (id != product.id) return false;
        if (name != null ? !name.equals(product.name) : product.name != null) return false;
        if (price != null ? !price.equals(product.price) : product.price != null) return false;
        if (status != null ? !status.equals(product.status) : product.status != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (price != null ? price.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        return result;
    }

    public int getSellingcompanyId() {
        return sellingcompanyId;
    }

    public void setSellingcompanyId(int sellingcompanyId) {
        this.sellingcompanyId = sellingcompanyId;
    }
}
