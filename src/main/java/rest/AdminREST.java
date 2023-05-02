package rest;

import entities.*;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("admin")
public class AdminREST {
    EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");
    EntityManager em = entityManagerFactory.createEntityManager();

    @POST
    @Path("/create-seller/{username}")
    public String createSellers(@PathParam("username") String username, List<String> companyNames) {
        em.getTransaction().begin();
        Admin admin = em.createQuery("SELECT a FROM Admin a WHERE a.username=: username", Admin.class)
                .setParameter("username", username)
                .getSingleResult();
        if (admin != null && admin.getStatus().equals("Logged")) {
            List<String> passwords = new ArrayList<>();
            for (int i = 0; i < companyNames.size(); i++) {
                String password = admin.generateRandomPassword();
                passwords.add(password);
                Sellingcompany seller = new Sellingcompany();
                seller.setUsername(companyNames.get(i));
                seller.setPassword(password);
                seller.setState("NotLoggedIn");
                em.persist(seller);
                em.getTransaction().commit();
            }
            return "Selling Company Account Created!";
        }
        else if(admin == null){
            throw new RuntimeException("Admin Not Registered!");
        }
        throw new RuntimeException("Admin Not Logged In!");
    }

    @POST
    @Path("/create-shipping/{username}")
    public String register(@PathParam("username") String username,Shippingcompany shippingcompany) {
        em.getTransaction().begin();
        Admin admin = em.createQuery("SELECT a FROM Admin a WHERE a.username=: username", Admin.class)
                .setParameter("username", username)
                .getSingleResult();
        if (admin != null && admin.getStatus().equals("Logged")) {
            em.persist(shippingcompany);
            em.getTransaction().commit();
            return "Shipping Company Successfully Registered!";
        }
        else if(admin == null){
            throw new RuntimeException("Admin Not Registered!");
        }
        throw new RuntimeException("Admin Not Logged In!");
    }

    @GET
    @Path("/getallcustomers/{username}")
    @Transactional
    public Response getAllCustomers(@PathParam("username") String username) {
        Admin admin = em.createQuery("SELECT a FROM Admin a WHERE a.username=: username", Admin.class)
                .setParameter("username", username)
                .getSingleResult();
        if (admin != null && admin.getStatus().equals("Logged")) {
            TypedQuery<Customer> query = em.createQuery("SELECT c FROM Customer c LEFT JOIN FETCH c.orders", Customer.class);
            List<Customer> customers = query.getResultList();
            List<Map<String, Object>> result = new ArrayList<>();
            for (Customer customer : customers) {
                Map<String, Object> customerInfo = new HashMap<>();
                customerInfo.put("custName", customer.getCustName());
                customerInfo.put("custEmail", customer.getCustEmail());
                customerInfo.put("custPassword", customer.getCustPassword());
                customerInfo.put("custState", customer.getCustState());

                List<Map<String, Object>> orders = new ArrayList<>();
                if (customer.getOrders() != null) {
                    for (Orders order : customer.getOrders()) {
                        Map<String, Object> orderInfo = new HashMap<>();
                        orderInfo.put("Order Id", order.getId());
                        orderInfo.put("Order Status", order.getStatus());
                        orders.add(orderInfo);
                    }
                }
                customerInfo.put("Orders Status", orders);
                result.add(customerInfo);
            }
            return Response.ok(result).build();
        }else {
            em.getTransaction().rollback();
            return Response.status(Response.Status.UNAUTHORIZED).entity("SellingCompany is not logged in").build();
        }
    }

//    @GET
//    @Path("/getallcustomers/{username}")
//    @Transactional
//    public Response getAllCustomers(@PathParam("username") String username) {
//        Admin admin = em.createQuery("SELECT a FROM Admin a WHERE a.username=: username", Admin.class)
//                .setParameter("username", username)
//                .getSingleResult();
//        if (admin != null && admin.getStatus().equals("Logged")) {
//            TypedQuery<Customer> query = em.createQuery("SELECT a FROM Customer a", Customer.class);
//            List<Customer> customers = query.getResultList();
//            List<Map<String, Object>> result = new ArrayList<>();
//            for (Customer customer : customers) {
//                Map<String, Object> customerInfo = new HashMap<>();
//                customerInfo.put("custName", customer.getCustName());
//                customerInfo.put("custEmail", customer.getCustEmail());
//                customerInfo.put("custPassword", customer.getCustPassword());
//                customerInfo.put("custState", customer.getCustState());
//
//                List<Map<String, Object>> orders = new ArrayList<>();
//                for (Orders order : customer.getOrders()) {
//                    Map<String, Object> orderInfo = new HashMap<>();
//                    orderInfo.put("Order Id", order.getId());
//                    orderInfo.put("Order Status", order.getStatus());
//                    orders.add(orderInfo);
//                }
//                customerInfo.put("Orders Status", orders);
//                result.add(customerInfo);
//            }
//            return Response.ok(result).build();
//        }else {
//            em.getTransaction().rollback();
//            return Response.status(Response.Status.UNAUTHORIZED).entity("Admin is not logged in").build();
//        }
//    }

    @GET
    @Path("/getallshipping/{username}")
    @Transactional
    public Response getAllShippingCompanies(@PathParam("username") String username) {
        Admin admin = em.createQuery("SELECT a FROM Admin a WHERE a.username=: username", Admin.class)
                .setParameter("username", username)
                .getSingleResult();
        if (admin != null && admin.getStatus().equals("Logged")) {
            TypedQuery<Shippingcompany> query = em.createQuery("SELECT a FROM Shippingcompany a", Shippingcompany.class);
            List<Shippingcompany> shippingcompanies = query.getResultList();
            List<Map<String, Object>> result = new ArrayList<>();
            for (Shippingcompany shippingcompany : shippingcompanies) {
                Map<String, Object> shippingCompanyInfo = new HashMap<>();
                shippingCompanyInfo.put("username", shippingcompany.getUsername());
                shippingCompanyInfo.put("password", shippingcompany.getPassword());

                List<Map<String, Object>> regions = new ArrayList<>();
                for (Regions region : shippingcompany.getRegions()) {
                    Map<String, Object> productInfo = new HashMap<>();
                    productInfo.put("Region Name", region.getRegionName());
                    // add other product properties as needed
                    regions.add(productInfo);
                }
                shippingCompanyInfo.put("Supported Regions", regions);
                result.add(shippingCompanyInfo);
            }
            return Response.ok(result).build();
        }else {
            em.getTransaction().rollback();
            return Response.status(Response.Status.UNAUTHORIZED).entity("Admin is not logged in").build();
        }
    }

    @GET
    @Path("/getallselling/{username}")
    @Transactional
    public Response getAllSellingCompanies(@PathParam("username") String username) {
        Admin admin = em.createQuery("SELECT a FROM Admin a WHERE a.username=: username", Admin.class)
                .setParameter("username", username)
                .getSingleResult();
        if (admin != null && admin.getStatus().equals("Logged")) {
        TypedQuery<Sellingcompany> query = em.createQuery("SELECT a FROM Sellingcompany a", Sellingcompany.class);
        List<Sellingcompany> sellingcompanies = query.getResultList();

        List<Map<String, Object>> result = new ArrayList<>();
        for (Sellingcompany sellingcompany : sellingcompanies) {
            Map<String, Object> sellingCompanytInfo = new HashMap<>();
            sellingCompanytInfo.put("username", sellingcompany.getUsername());
            sellingCompanytInfo.put("password", sellingcompany.getPassword());
            sellingCompanytInfo.put("state", sellingcompany.getState());

            // Convert the set of products to a list of product maps
            List<Map<String, Object>> products = new ArrayList<>();
            for (Product product : sellingcompany.getProducts()) {
                Map<String, Object> productInfo = new HashMap<>();
                productInfo.put("name", product.getName());
                productInfo.put("price", product.getPrice());
                // add other product properties as needed
                products.add(productInfo);
            }

            sellingCompanytInfo.put("products", products);
            result.add(sellingCompanytInfo);
        }
            return Response.ok(result).build();
        }else {
            em.getTransaction().rollback();
            return Response.status(Response.Status.UNAUTHORIZED).entity("Admin is not logged in").build();
        }
    }

    @POST
    @Path("/login")
    public Response login(Admin admin) {
        em.getTransaction().begin();
        String username = admin.getUsername();
        String password = admin.getPassword();
        if (username == null || password == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Username and password are required").build();
        }
        try {
            Admin foundAdmin = em.createQuery("SELECT a FROM Admin a WHERE a.username = :username", Admin.class)
                    .setParameter("username", username)
                    .getSingleResult();

            if (foundAdmin.getPassword().equals(password)) {
                foundAdmin.setStatus("Logged");
                em.merge(foundAdmin);
                em.getTransaction().commit();
                return Response.ok("Login successful").build();
            } else {
                return Response.status(Response.Status.UNAUTHORIZED).entity("Incorrect password").build();
            }
        } catch (NoResultException e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Admin not found").build();
        } finally {
        em.close();
    }
    }

    @POST
    @Path("/register")
    public String register(Admin admin) {
        em.getTransaction().begin();
        em.persist(admin);
        em.getTransaction().commit();
        return "Admin Successfully Registered!";
    }

    @GET
    @Path("/get/{username}")
    public Admin getAdmin(@PathParam("username") String username) {
        Admin admin = em.createQuery("SELECT a FROM Admin a WHERE a.username=: username", Admin.class)
                .setParameter("username", username)
                .getSingleResult();
        return admin;
    }
}