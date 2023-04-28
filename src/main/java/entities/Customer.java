package entities;

import jakarta.persistence.*;

import java.util.Set;

@Entity
public class Customer {

//    @OneToMany(mappedBy = "customer",cascade = CascadeType.ALL)
//    private Set<Orders> orders;

    @Id
    @Column(name = "custId", nullable = false)
    private int custId;
    @Basic
    @Column(name = "custName", nullable = false, length = 45)
    private String custName;
    @Basic
    @Column(name = "custEmail", nullable = false, length = 45)
    private String custEmail;
    @Basic
    @Column(name = "custPassword", nullable = false, length = 45)
    private String custPassword;

    public int getCustId() {
        return custId;
    }

    public void setCustId(int custId) {
        this.custId = custId;
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public String getCustEmail() {
        return custEmail;
    }

    public void setCustEmail(String custEmail) {
        this.custEmail = custEmail;
    }

    public String getCustPassword() {
        return custPassword;
    }

    public void setCustPassword(String custPassword) {
        this.custPassword = custPassword;
    }












    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Customer customer = (Customer) o;

        if (custId != customer.custId) return false;
        if (custName != null ? !custName.equals(customer.custName) : customer.custName != null) return false;
        if (custEmail != null ? !custEmail.equals(customer.custEmail) : customer.custEmail != null) return false;
        if (custPassword != null ? !custPassword.equals(customer.custPassword) : customer.custPassword != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = custId;
        result = 31 * result + (custName != null ? custName.hashCode() : 0);
        result = 31 * result + (custEmail != null ? custEmail.hashCode() : 0);
        result = 31 * result + (custPassword != null ? custPassword.hashCode() : 0);
        return result;
    }
}
