package rest;

import entities.Admin;
import entities.Customer;
import entities.Orders;
import entities.Product;
import jakarta.persistence.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import javax.ejb.Stateful;
import javax.ejb.Stateless;
import java.util.List;

@Stateful
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("customer")

public class CustomerREST {
    EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");
    EntityManager em = entityManagerFactory.createEntityManager();

    //1- Register as a new customer
    @POST
    @Path("/signup")
    public String register(Customer cust) {
        em.getTransaction().begin();
        em.persist(cust);
        em.getTransaction().commit();
        return "successfully added customer!";
    }

    //2- login customer
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
                foundCust.setCustState("online");
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

    //3- View current and past purchase orders

    @GET
    @Path("/view/{status}")
    public List<Orders> viewOrders(@PathParam("status") String status) {

        if (status.equals("current")) {

        em.getTransaction().begin();
        TypedQuery<Orders> query = em.createQuery("SELECT o FROM Orders o WHERE o.status= 'current' ", Orders.class);
        List<Orders> orders = query.getResultList();
        em.getTransaction().commit();
        return orders;

    } else if (status.equals("past")){
            em.getTransaction().begin();
            TypedQuery<Orders> query = em.createQuery("SELECT o FROM Orders o WHERE o.status= 'past'", Orders.class);
            List<Orders> orders = query.getResultList();
            em.getTransaction().commit();
            return orders;
        }

        return null;
    }




    //4- Make a new purchase

    @POST
    @Path("/newOrder/{id}")
    public String newPurchase(@PathParam("id") int id, Orders order) {
        Orders newOrder= new Orders();
        em.getTransaction().begin();
        String name = order.getProductNames();
        int product_id = order.getProductId();
        TypedQuery<Product> query= em.createQuery("SELECT p FROM Product p", Product.class);
        List<Product> products = query.getResultList();
        //check if user is logged in
        Customer customer = em.find(Customer.class,id);
        if(customer.getCustState().equals("online")){
            for(int i=0; i<products.size();i++){
                if(products.get(i).getName().equals(name)){
                    newOrder.setProductId(id);
                    newOrder.setProductNames(name);
                    newOrder.setCustomerId(id);
                    newOrder.setCustomer(customer);
                    newOrder.setStatus("current");
                    em.persist(newOrder);
                    em.getTransaction().commit();
                    return "order completed!";
                }
            }
            return "this product:("+name+") is not available";
        }
        return "please login first";



    }

//    @POST
//    @Path("/newOrder/{custName}")
//    public String newPurchase(@PathParam("custName") String custName, Orders order) {
//        Orders newOrder= new Orders();
//        em.getTransaction().begin();
//        //Orders newOrder= new Orders();
//        String name = order.getProductNames();
//        //String customerN= order.getCustomerName();
//        TypedQuery<Product> query= em.createQuery("SELECT p FROM Product p", Product.class);
//        List<Product> products = query.getResultList();
//
//        //check if user is logged in
//        Customer foundCust= em.createQuery("SELECT c FROM Customer c WHERE c.custName=:custName", Customer.class)
//                .setParameter("custName" ,custName)
//                .getSingleResult();
//
//              if(foundCust.getCustState().equals("online") )
//            {
//
//                for(int i=0; i<products.size();i++){
//                    if(products.get(i).getName().equals(name)){
//                        newOrder.setProductNames(name);
//                        newOrder.setCustomerName(custName);
//                        newOrder.setStatus("current");
//                        em.persist(newOrder);
//                        em.getTransaction().commit();
//                        return "order completed!";
//                    }
//            }
//                return "this product:("+name+") is not available";
//        }  return "please login first";
//
//
//
//    }
}


