package rest;

import entities.*;
import jakarta.persistence.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import javax.ejb.Stateful;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @Path("/view/{id}/{status}")
    public Response viewOrders(@PathParam("id") int id,@PathParam("status") String status) {
        em.getTransaction().begin();
        Customer customer = em.find(Customer.class,id);
        if(customer !=null && customer.getCustState().equals("online") && status.equals("current")){
            TypedQuery<Orders> query = em.createQuery("SELECT o FROM Orders o WHERE o.status= 'current' AND o.customerId =:id", Orders.class);
            query.setParameter("id", id);// Set the named parameter "id"
            List<Orders> orders = query.getResultList();

            List<Map<String, Object>> result = new ArrayList<>();
            for (Orders order : orders) {
                Map<String, Object> orderInfo = new HashMap<>();
                orderInfo.put("Product Name", order.getProductNames());
                orderInfo.put("Status", order.getStatus());
                result.add(orderInfo);
            }
            return Response.ok(result).build();

        } else if (customer !=null && customer.getCustState().equals("online") && status.equals("past")) {
            TypedQuery<Orders> query = em.createQuery("SELECT o FROM Orders o WHERE o.status= 'past' AND o.customerId =:id", Orders.class);
            query.setParameter("id", id); // Set the named parameter "id"
            List<Orders> orders = query.getResultList();

            List<Map<String, Object>> result = new ArrayList<>();
            for (Orders order : orders) {
                Map<String, Object> orderInfo = new HashMap<>();
                Shippingcompany shippingcompany = em.find(Shippingcompany.class,order.getShippingId());
                orderInfo.put("Product Name", order.getProductNames());
                orderInfo.put("Status", order.getStatus());
                orderInfo.put("Shipping company Name",shippingcompany.getUsername());
                result.add(orderInfo);
            }
            return Response.ok(result).build();
        } else if(customer == null){
            return Response.status(Response.Status.UNAUTHORIZED).entity("Customer doesn't exist! Please register first!").build();
        }
        else {
            em.getTransaction().rollback();
            return Response.status(Response.Status.UNAUTHORIZED).entity("Customer is not logged in").build();
        }
    }

    //4- Make a new purchase
    @POST
    @Path("/newOrder/{id}")
    public String newPurchase(@PathParam("id") int id, Orders order) {
        Orders newOrder= new Orders();
        em.getTransaction().begin();
        String name = order.getProductNames();
        TypedQuery<Product> query= em.createQuery("SELECT p FROM Product p", Product.class);
        List<Product> products = query.getResultList();
        //check if user is logged in
        Customer customer = em.find(Customer.class,id);
        if(customer.getCustState().equals("online")){
            for(int i=0; i<products.size();i++){
                if(products.get(i).getName().equals(name)){
                    newOrder.setProductId(products.get(i).getId());
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
}


