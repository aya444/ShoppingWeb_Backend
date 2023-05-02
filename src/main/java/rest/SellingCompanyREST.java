package rest;

import entities.Admin;
import entities.Orders;
import entities.Product;
import entities.Sellingcompany;
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
@Path("selling")
public class SellingCompanyREST {
    EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");
    EntityManager em = entityManagerFactory.createEntityManager();

    @POST
    @Path("/login")
    public Response login(Sellingcompany sellingcompany) {
        em.getTransaction().begin();
        String username = sellingcompany.getUsername();
        String password = sellingcompany.getPassword();
        if (username == null || password == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Username and password are required").build();
        }
        try {
            Sellingcompany foundSellingcompany = em.createQuery("SELECT a FROM Sellingcompany a WHERE a.username = :username", Sellingcompany.class)
                    .setParameter("username", username)
                    .getSingleResult();

            if (foundSellingcompany.getPassword().equals(password)) {
                foundSellingcompany.setState("Logged");
                em.merge(foundSellingcompany);
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

    @GET
    @Path("/sale/{username}")
    public Response getProductsOnSale(@PathParam("username") String username) {
        em.getTransaction().begin();
        Sellingcompany sellingcompany = em.createQuery("SELECT a FROM Sellingcompany a WHERE a.username=: username", Sellingcompany.class)
                .setParameter("username", username)
                .getSingleResult();

        if (sellingcompany != null && sellingcompany.getState().equals("Logged")) {
            TypedQuery<Product> query = em.createQuery("SELECT p FROM Product p WHERE p.status = 'sale' AND p.sellingcompany.username = :username", Product.class);
            query.setParameter("username", username);
            List<Product> products = query.getResultList();

            List<Map<String, Object>> result = new ArrayList<>();
            for (Product product : products) {
                Map<String, Object> productInfo = new HashMap<>();
                productInfo.put("name", product.getName());
                productInfo.put("status", product.getStatus());
                productInfo.put("price", product.getPrice());
                result.add(productInfo);
            }
            return Response.ok(result).build();
        } else {
            em.getTransaction().rollback();
            return Response.status(Response.Status.UNAUTHORIZED).entity("SellingCompany is not logged in").build();
        }
    }

    @GET
    @Path("/getorders/{username}")
    public Response get(@PathParam("username") String username) {
        em.getTransaction().begin();
        Sellingcompany sellingcompany = em.createQuery("SELECT a FROM Sellingcompany a WHERE a.username=: username", Sellingcompany.class)
                .setParameter("username", username)
                .getSingleResult();

        if (sellingcompany != null && sellingcompany.getState().equals("Logged")) {
            TypedQuery<Product> query1 = em.createQuery("SELECT p FROM Product p WHERE p.status = 'sale' AND p.sellingcompany.username = :username", Product.class);
            query1.setParameter("username", username); // set the value of the named parameter "id" to the value of the path parameter "id"
            List<Product> products = query1.getResultList();
            TypedQuery<Orders> query = em.createQuery("SELECT o FROM Orders o", Orders.class);
            List<Orders> orders1 = query.getResultList();
            List<Orders> orders = new ArrayList<>();

            for (int i = 0; i < orders1.size(); i++) {
                for (int j = 0; j < products.size(); j++) {
                    if (orders1.get(i).getProductId() == products.get(j).getId()) {
                        orders.add(orders1.get(i));
                    }
                }
            }
            em.getTransaction().commit();

            List<Map<String, Object>> result = new ArrayList<>();
            for (Orders order : orders) {
                Map<String, Object> orderInfo = new HashMap<>();
                orderInfo.put("orderId", order.getId());
                orderInfo.put("productId", order.getProductId());
                orderInfo.put("productNames", order.getProductNames());
                orderInfo.put("status", order.getStatus());
                orderInfo.put("customerId", order.getCustomerId());
                result.add(orderInfo);
            }
            return Response.ok(result).build();
        } else {
            em.getTransaction().rollback();
            return Response.status(Response.Status.UNAUTHORIZED).entity("SellingCompany is not logged in").build();
        }
    }

    @POST
    @Path("/add/{username}")
    public String createProduct(@PathParam("username") String username, Product newProduct) {
        Sellingcompany sellingCompany = em.createQuery("SELECT a FROM Sellingcompany a WHERE a.username=: username", Sellingcompany.class)
                .setParameter("username", username)
                .getSingleResult();
        if (sellingCompany != null && sellingCompany.getState().equals("Logged")) {
            Product product = new Product();
            product.setName(newProduct.getName());
            product.setPrice(newProduct.getPrice());
            product.setStatus(newProduct.getStatus());
            product.setSellingcompanyId(sellingCompany.getId());
            product.setSellingcompany(sellingCompany);

            em.getTransaction().begin();
            em.persist(product);
            em.getTransaction().commit();

            return "Product added Successfully!";
        } else if (sellingCompany == null) {
            throw new RuntimeException("Selling Company Not Registered!");
        }
        throw new RuntimeException("Selling Company Not Logged In!");
    }
}