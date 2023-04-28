//package entities;
//
//import jakarta.persistence.*;
//
//import java.sql.Date;
//
//@Entity
//public class Order {
//    @Id
//    @Column(name = "id", nullable = false)
//    private int id;
//
//    @ManyToOne
//    @JoinColumn(name = "productname")
//    private Product product;
//
//    @ManyToOne
//    @JoinColumn(name = "shippingname")
//    private Shippingcompany shippingCompany;
//
//    //    @ManyToOne
////    @JoinColumn(name = "customer_id")
////    private Customer customer;
//
//    public Shippingcompany getSellingcompany() {
//        return shippingCompany;
//    }
//
//    public void setShippingcompany(Shippingcompany shippingCompany) {
//        this.shippingCompany = shippingCompany;
//    }
//
//    public Product getProduct() {
//        return product;
//    }
//
//    public void setProduct(Product product) {
//        this.product = product;
//    }
//
//    public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//
//        Order order = (Order) o;
//
//        if (id != order.id) return false;
//        return true;
//    }
//
//    @Override
//    public int hashCode() {
//        int result = id;
//        return result;
//    }
//}
