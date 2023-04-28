package rest;

import entities.Customer;
import entities.Orders;
import entities.Product;
import entities.Sellingcompany;
import jakarta.persistence.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import javax.ejb.Stateless;
import java.util.List;

@Stateless
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("customer")

public class CustomerREST {
    EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");
    EntityManager em = entityManagerFactory.createEntityManager();

    //Register as a new customer
    @POST
    @Path("/signup")
    public String register(Customer cust) {
        em.getTransaction().begin();
        em.persist(cust);
        em.getTransaction().commit();
        return "successfully added customer!";
    }

    //login customer
    @POST
    @Path("/login")
    public Response login(Customer cust) {
        em.getTransaction().begin();
        String username = cust.getCustName();
        String password = cust.getCustPassword();
        if (username == null || password == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Username and password must not be empty").build();
        }
        try {

            Customer foundCust = em.createQuery("SELECT a FROM Customer a WHERE a.custName = :username", Customer.class)
                    .setParameter("username", username)
                    .getSingleResult();

            if (cust.getCustPassword().equals(password)) {
                em.getTransaction().commit();
                return Response.ok("Login successful").build();
            } else {
                return Response.status(Response.Status.UNAUTHORIZED).entity("Incorrect password").build();
            }
        } catch (NoResultException e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Customer not found").build();
        } finally {
            em.close();
        }
    }

    //Make a new purchase

    @POST
    @Path("/newOrder")
    public String newPurchase(Orders order) {
        em.getTransaction().begin();
        Orders newOrder= new Orders();
        String name = order.getProductNames();
        TypedQuery<Product> query= em.createQuery("SELECT p FROM Product p", Product.class);
        List<Product> products = query.getResultList();

        for(int i=0; i<products.size();i++){
            if(name.equals(products.get(i).getName())){
                newOrder.setProductNames(name);
                newOrder.setState("current");
                em.persist(newOrder);
                em.getTransaction().commit();
                return "order completed!";
            }
        }
        return "Product not found!";
    }
}


