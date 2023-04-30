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
    @Path("/create-seller")
    public String createSellers(List<String> companyNames) {
        Admin admin = new Admin();
        List<String> passwords = new ArrayList<>();

        for (int i = 0; i < companyNames.size(); i++) {
            em.getTransaction().begin();
            String password = admin.generateRandomPassword();
            passwords.add(password);
            Sellingcompany seller = new Sellingcompany();
            seller.setUsername(companyNames.get(i));
            seller.setPassword(password);
            seller.setState("NotLoggedIn");
            em.persist(seller);
            em.getTransaction().commit();
        }
        return "Done!";
    }

    @GET
    @Path("/getallcustomers")
    @Transactional
    public List<Map<String, Object>> getAllCustomers() {
        TypedQuery<Customer> query = em.createQuery("SELECT a FROM Customer a", Customer.class);
        List<Customer> customers = query.getResultList();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Customer customer : customers) {
            Map<String, Object> sellingCompanytInfo = new HashMap<>();
            sellingCompanytInfo.put("custName", customer.getCustName());
            sellingCompanytInfo.put("custEmail", customer.getCustEmail());
            sellingCompanytInfo.put("custPassword", customer.getCustPassword());
            sellingCompanytInfo.put("custState", customer.getCustState());
            result.add(sellingCompanytInfo);
        }
        return result;
    }

    @GET
    @Path("/getallshipping")
    @Transactional
    public List<Map<String, Object>> getAllShippingCompanies() {
        TypedQuery<Shippingcompany> query = em.createQuery("SELECT a FROM Shippingcompany a", Shippingcompany.class);
        List<Shippingcompany> shippingcompanies = query.getResultList();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Shippingcompany shippingcompany : shippingcompanies) {
            Map<String, Object> sellingCompanytInfo = new HashMap<>();
            sellingCompanytInfo.put("username", shippingcompany.getUsername());
            sellingCompanytInfo.put("password", shippingcompany.getPassword());
            result.add(sellingCompanytInfo);
        }
        return result;
    }

    @GET
    @Path("/getallselling")
    @Transactional
    public List<Map<String, Object>> getAllSellingCompanies() {
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

        return result;
    }



//    @GET
//    @Path("/getallselling")
//    @Transactional
//    public List<Map<String, Object>> getAllSellingCompanies() {
////        em.getTransaction().begin();
//        TypedQuery<Sellingcompany> query = em.createQuery("SELECT a FROM Sellingcompany a", Sellingcompany.class);
//        List<Sellingcompany> sellingcompanies = query.getResultList();
////        em.getTransaction().commit();
//
//        List<Map<String, Object>> result = new ArrayList<>();
//        for (Sellingcompany sellingcompany : sellingcompanies) {
//            Map<String, Object> sellingCompanytInfo = new HashMap<>();
//            sellingCompanytInfo.put("username", sellingcompany.getUsername());
//            sellingCompanytInfo.put("password", sellingcompany.getPassword());
//            sellingCompanytInfo.put("state", sellingcompany.getState());
//            sellingCompanytInfo.put("Products",sellingcompany.getProducts());
//            result.add(sellingCompanytInfo);
//        }
//        return result;
//    }


    @POST
    @Path("/login")
    @Transactional
    public Response login(Admin admin) {
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
                return Response.ok("Login successful").build();
            } else {
                return Response.status(Response.Status.UNAUTHORIZED).entity("Incorrect password").build();
            }
        } catch (NoResultException e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Admin not found").build();
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


//    EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");
//    EntityManager em = entityManagerFactory.createEntityManager();
//
//    @POST
//    @Path("/register")
//    public String register(Admin admin) {
//        em.getTransaction().begin();
//        em.persist(admin);
//        em.getTransaction().commit();
//        return "Admin Successfully Registered!";
//    }
//
//    @POST
//    @Path("/login")
//    public Response login(Admin admin) {
//        String username = admin.getUsername();
//        String password = admin.getPassword();
//
//        if (username == null || password == null) {
//            return Response.status(Response.Status.BAD_REQUEST).entity("Username and password are required").build();
//        }
//
//        try {
//            Admin foundAdmin = em.createQuery("SELECT a FROM Admin a WHERE a.username = :username", Admin.class)
//                    .setParameter("username", username)
//                    .getSingleResult();
//            if (foundAdmin.getPassword().equals(password)) {
//                em.getTransaction().commit();
//                return Response.ok("Login successful").build();
//            } else {
//                return Response.status(Response.Status.UNAUTHORIZED).entity("Incorrect password").build();
//            }
//        } catch (NoResultException e) {
//            return Response.status(Response.Status.UNAUTHORIZED).entity("Admin not found").build();
//        } finally {
//            em.close();
//        }
//    }
//
//    @POST
//    @Path("/create-seller")
//    public String createSellers(List<String> companyNames) {
//        Admin admin = new Admin();
//        List<String> passwords = new ArrayList<>();
//
//        for (int i = 0; i < companyNames.size(); i++) {
//            em.getTransaction().begin();
//            String password = admin.generateRandomPassword();
//            passwords.add(password);
//            Sellingcompany seller = new Sellingcompany();
//            seller.setUsername(companyNames.get(i));
//            seller.setPassword(password);
//            em.persist(seller);
//            em.getTransaction().commit();
//        }
//        return "Done!";
//    }
//
//    @GET
//    @Path("/getall")
//    public List<Sellingcompany> getAllSellingCompanies() {
//        em.getTransaction().begin();
//        TypedQuery<Sellingcompany> query = em.createQuery("SELECT a FROM Sellingcompany a", Sellingcompany.class);
//        List<Sellingcompany> sellingcompanies = query.getResultList();
//        em.getTransaction().commit();
//        return sellingcompanies;
//    }

//    @GET
//    @Path("/getalltry")
//    public Set<Product> getAll() {
//        TypedQuery<Sellingcompany> query = em.createQuery("SELECT a FROM Sellingcompany a", Sellingcompany.class);
//        List<Sellingcompany> sellingcompanies = query.getResultList();
//        Set<Product> productsBig= new HashSet<>();
//        for(int i=1;i<=sellingcompanies.size();i++){
//
//            for(int j=1;j<=sellingcompanies.get(i).getProducts().size();j++){
//                Set<Product> products= sellingcompanies.get(i).getProducts();
//                productsBig.addAll(products);
//            }
//        }
//        return productsBig;
//    }

//    @GET
//    @Path("/get/{id}")
//    public Admin get(@PathParam("id")Integer id) {
//        em.getTransaction().begin();
//        Admin admin = em.find(Admin.class,id);
//        em.getTransaction().commit();
//        return admin;
//    }
//
//    @GET
//    @Path("/getall")
//    public List<Admin> getAllAdmins() {
//        em.getTransaction().begin();
//        TypedQuery<Admin> query = em.createQuery("SELECT a FROM Admin a", Admin.class);
//        List<Admin> admins = query.getResultList();
//        em.getTransaction().commit();
//        return admins;
//    }
//
//    @PUT
//    @Path("/updateByUsername/{username}")
//    @Consumes("text/plain")
//    public Response updateByUsername(@PathParam("username")String username, String newPassword) {
//        em.getTransaction().begin();
//        Admin admin = em.createQuery("SELECT a FROM Admin a WHERE a.username = :username", Admin.class)
//                .setParameter("username", username)
//                .getSingleResult();
//        if (admin != null) {
//            admin.setPassword(newPassword);
//            em.merge(admin);
//        }
//        em.getTransaction().commit();
//        String message = "Password for admin with username '" + username + "' has been updated successfully.";
//        return Response.status(Response.Status.OK).entity(message).build();
//    }
//
//    @DELETE
//    @Path("/delete/{id}")
//    public String register(@PathParam("id")Integer id) {
//        em.getTransaction().begin();
//        Admin a= em.find(Admin.class, id);
//        em.remove(a);
//        em.getTransaction().commit();
//        return "Admin Successfully Deleted!";
//    }
}