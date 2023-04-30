package rest;

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
            Sellingcompany foundSellingcompany= em.createQuery("SELECT a FROM Sellingcompany a WHERE a.username = :username", Sellingcompany.class)
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
        Sellingcompany sellingcompany = em.createQuery("SELECT a FROM Sellingcompany a WHERE a.username = :username AND a.state= 'Logged'", Sellingcompany.class)
                .setParameter("username", username)
                .getSingleResult();

        if (sellingcompany != null) {
            TypedQuery<Product> query = em.createQuery("SELECT p FROM Product p WHERE p.status = 'sale'", Product.class);
            List<Product> products = query.getResultList();
            em.getTransaction().commit();

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
        Sellingcompany sellingcompany = em.createQuery("SELECT a FROM Sellingcompany a WHERE a.username = :username AND a.state= 'Logged'", Sellingcompany.class)
                .setParameter("username", username)
                .getSingleResult();

        if (sellingcompany != null) {
            TypedQuery<Orders> query = em.createQuery("SELECT a FROM Orders a", Orders.class);
            List<Orders> orders = query.getResultList();
            em.getTransaction().commit();

            List<Map<String, Object>> result = new ArrayList<>();
            for (Orders order : orders) {
                Map<String, Object> orderInfo = new HashMap<>();
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
    @Path("/add/{id}")
    public String createProduct(@PathParam(value = "id") int id, Product newProduct) {
        Sellingcompany sellingCompany = em.find(Sellingcompany.class, id);
        if (sellingCompany != null && sellingCompany.getState().equals("Logged")) {
            Product product = new Product();
            product.setName(newProduct.getName());
            product.setPrice(newProduct.getPrice());
            product.setStatus(newProduct.getStatus());
            product.setSellingcompanyId(id);
            product.setSellingcompany(sellingCompany);

            em.getTransaction().begin();
            em.persist(product);
            em.getTransaction().commit();

            return "Product added Successfully!";
        }
        else if(sellingCompany == null){
            throw new RuntimeException("Selling Company Not Registered!");
        }
        throw new RuntimeException("Selling Company Not Logged In!");
    }

//    @POST
//    @Path("/add/{id}")
//    public String createProduct(@PathParam(value = "id") Long id, Product newProduct) {
//        Sellingcompany sellingCompany = em.find(Sellingcompany.class, id);
//        if (sellingCompany != null && sellingCompany.getState().equals("Logged")) {
//            Product product = new Product();
//            product.setName(newProduct.getName());
//            product.setPrice(newProduct.getPrice());
//            product.setStatus(newProduct.getStatus());
//            product.setSellingcompany(sellingCompany);
//
//            em.getTransaction().begin();
//            em.persist(product);
//            em.getTransaction().commit();
//
//            return "Product added Successfully!";
//        }
//        else if(sellingCompany == null){
//            throw new RuntimeException("Selling Company Not Registered!");
//        }
//        throw new RuntimeException("Selling Company Not Logged In!");
//    }











    // WORKING but without taking the username in the path
//    @GET
//    @Path("/getorders")
//    public List<Orders> get() {
//        em.getTransaction().begin();
//        Orders order = new Orders();
//        TypedQuery<Orders> query = em.createQuery("SELECT a FROM Orders a", Orders.class);
//        List<Orders> orders = query.getResultList();
//        em.getTransaction().commit();
//        return orders;
//    }

    // WORKING but without taking the username in the path
//    @GET
//    @Path("/sale")
//    public List<Map<String, Object>> getProductsOnSale() {
//        em.getTransaction().begin();
//        TypedQuery<Product> query = em.createQuery("SELECT p FROM Product p WHERE p.status = 'sale'", Product.class);
//        List<Product> products = query.getResultList();
//        em.getTransaction().commit();
//
//        List<Map<String, Object>> result = new ArrayList<>();
//        for (Product product : products) {
//            Map<String, Object> productInfo = new HashMap<>();
//            productInfo.put("name", product.getName());
//            productInfo.put("status", product.getStatus());
//            productInfo.put("price", product.getPrice());
//            result.add(productInfo);
//        }
//
//        return result;
//    }




//
//    @POST
//    @Path("/addproduct/{sellingCompany_id}")
//    public String addProduct(@PathParam(value = "sellingCompany_id") Long sellingCompany_id, Product newProduct) {
//
//        Sellingcompany sellingCompany = em.find(Sellingcompany.class, sellingCompany_id);
//        if (sellingCompany == null) {
//            return "Selling company not found";
//        }
//
//        Product product = new Product();
//        product.setName(newProduct.getName());
//        product.setPrice(newProduct.getPrice());
//        product.setStatus(newProduct.getStatus());
//
//        product.setSellingcompany(sellingCompany);
//
//        Set<Product> products = sellingCompany.getProducts();
//        if (products == null) {
//            products = new HashSet<>();
//        }
//        products.add(product);
//
//        sellingCompany.setProducts(products);
//
//        em.getTransaction().begin();
//        em.persist(product);
//        em.merge(sellingCompany);
//        em.getTransaction().commit();
//
//        return "Product added successfully!";
//    }
    // trip ->>> product
    //sellingCompany ->>> selling

//    @POST
//    @Path("/addproduct/{sellingCompany_id}")
//    public String addProduct(@PathParam(value = "sellingCompany_id") Long sellingCompany_id, Product newProduct) {
//
//        Product product = new Product();
//        TypedQuery<Sellingcompany> query2= em.createQuery("SELECT p FROM Sellingcompany p", Sellingcompany.class);
//        List<Sellingcompany> sellingcompanies = query2.getResultList();
//
//        for(int i=0; i<sellingcompanies.size();i++){
//            if(sellingcompanies.get(i).getId() == sellingCompany_id){
//                Sellingcompany sellingCompany = sellingcompanies.get(i);
//
//                product.setName(newProduct.getName());
//                product.setPrice(newProduct.getPrice());
//                product.setStatus(newProduct.getStatus());
//
//                Set<Product> products = sellingCompany.getProducts();
//                if (products == null) {
//                    products = new HashSet<>();
//                }
//
//                products.add(newProduct);
//                product.setSellingcompany(sellingCompany);
//
//                sellingCompany.setProducts(products);
//
//                em.getTransaction().begin();
//                em.persist(product);
//                em.merge(sellingCompany);
//                em.getTransaction().commit();
//                return "Product added Successfully!";
//            }
//        }
//        return "Selling Company Not Found!";
//    }





//    @POST
//    @Path("/addproduct/{sellingCompany_id}")
//     public String addProduct(@PathParam(value = "sellingCompany_id") Long sellingCompany_id, Product product){
//         Set<Product> products= new HashSet<>();
//         Sellingcompany sellingCompany1 = new Sellingcompany();
//
//         sellingCompany1= em.find(Sellingcompany.class,sellingCompany_id);
//         if (sellingCompany1 == null) {
//             return "Sellingcompany does not exist";
//         }
////         Sellingcompany sellingCompany = sellingCompany1;
//
//         //tie Author to Book
//         product.setSellingcompany(sellingCompany1);
//
////         Product product1 = product;
//         //tie Book to Author
//         products.add(product);
//         sellingCompany1.setProducts(products);
//         return "Done!";
//
//     }

//    @POST
//    @Path("/register")
//    public String register(Sellingcompany sellingcompany) {
//        em.getTransaction().begin();
//        em.persist(sellingcompany);
//        em.getTransaction().commit();
//        return "Sellingcompany Successfully Registered!";
//    }
//
//    @POST
//    @Path("/add/{selling_name}")
//    public String addProduct(@PathParam("selling_name")String selling_name,Product newProduct) {
//        Product product = new Product();
//
//        product.setName(newProduct.getName());
//        product.setPrice(newProduct.getPrice());
//        product.setStatus(newProduct.getStatus());
//
//        TypedQuery<Sellingcompany> query2= em.createQuery("SELECT p FROM Sellingcompany p", Sellingcompany.class);
//        List<Sellingcompany> sellingcompanies = query2.getResultList();
//
//        for(int i=0; i<sellingcompanies.size();i++){
//            if(sellingcompanies.get(i).getUsername().equals(selling_name)){
//                product.setName(newProduct.getName());
//                product.setPrice(newProduct.getPrice());
//                product.setStatus(newProduct.getStatus());
//                product.setSellingcompany(sellingcompanies.get(i));
//                em.persist(product);
//                em.getTransaction().commit();
//                return "Product added Successfully!";
//            }
//        }
//        return "Selling Company Not Found!";
//    }

//    @GET
//    @Path("/get/{id}")
//    public Sellingcompany get(@PathParam("id")Integer id) {
//        em.getTransaction().begin();
//        Sellingcompany sellingcompany = em.find(Sellingcompany.class,id);
//        em.getTransaction().commit();
//        return sellingcompany;
//    }
//
//
//    @PUT
//    @Path("/updateByUsername/{username}")
//    @Consumes("text/plain")
//    public Response updateByUsername(@PathParam("username")String username, String newPassword) {
//        em.getTransaction().begin();
//        Sellingcompany sellingcompany = em.createQuery("SELECT a FROM Sellingcompany a WHERE a.username = :username", Sellingcompany.class)
//                .setParameter("username", username)
//                .getSingleResult();
//        if (sellingcompany != null) {
//            sellingcompany.setPassword(newPassword);
//            em.merge(sellingcompany);
//        }
//        em.getTransaction().commit();
//        String message = "Password for sellingcompany with username '" + username + "' has been updated successfully.";
//        return Response.status(Response.Status.OK).entity(message).build();
//    }
//
//    @DELETE
//    @Path("/delete/{id}")
//    public String register(@PathParam("id")Integer id) {
//        em.getTransaction().begin();
//        Sellingcompany sellingcompany= em.find(Sellingcompany.class, id);
//        em.remove(sellingcompany);
//        em.getTransaction().commit();
//        return "Sellingcompany Successfully Deleted!";
//    }
}








//package rest;
//
//import entities.Orders;
//import entities.Product;
//import entities.Sellingcompany;
//import jakarta.persistence.*;
//import jakarta.ws.rs.*;
//import jakarta.ws.rs.core.MediaType;
//import jakarta.ws.rs.core.Response;
//
//import javax.ejb.Stateless;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Optional;
//import java.util.Set;
//
//@Stateless
//@Consumes(MediaType.APPLICATION_JSON)
//@Produces(MediaType.APPLICATION_JSON)
//@Path("selling")
//public class SellingCompanyREST {
//    EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");
//    EntityManager em = entityManagerFactory.createEntityManager();
//
//    @POST
//    @Path("/login")
//    public Response login(Sellingcompany sellingcompany) {
//        em.getTransaction().begin();
//        String username = sellingcompany.getUsername();
//        String password = sellingcompany.getPassword();
//        if (username == null || password == null) {
//            return Response.status(Response.Status.BAD_REQUEST).entity("Username and password are required").build();
//        }
//        try {
//            Sellingcompany foundSellingcompany= em.createQuery("SELECT a FROM Sellingcompany a WHERE a.username = :username", Sellingcompany.class)
//                    .setParameter("username", username)
//                    .getSingleResult();
//
//            if (foundSellingcompany.getPassword().equals(password)) {
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
//    @GET
//    @Path("/sale")
//    public List<Product> getProductsOnSale() {
//        em.getTransaction().begin();
//        TypedQuery<Product> query = em.createQuery("SELECT p FROM Product p WHERE p.status = 'sale'", Product.class);
//        List<Product> products = query.getResultList();
//        em.getTransaction().commit();
//        return products;
//    }
//
//    @GET
//    @Path("/getorders")
//    public List<Orders> get() {
//        em.getTransaction().begin();
//        Orders order = new Orders();
//        TypedQuery<Orders> query = em.createQuery("SELECT a FROM Orders a", Orders.class);
//        List<Orders> orders = query.getResultList();
//        em.getTransaction().commit();
//        return orders;
//    }
//
//    @POST
//    @Path("/add/{id}")
//    public String createProduct(@PathParam(value = "id") Long id, Product newProduct) {
//        Product product = new Product();
//        product.setName(newProduct.getName());
//        product.setPrice(newProduct.getPrice());
//        product.setStatus(newProduct.getStatus());
//
//        Sellingcompany sellingCompany = em.find(Sellingcompany.class, id);
//        if (sellingCompany == null) {
//            throw new RuntimeException("Selling Company Not Found!");
//        }
//        product.setSellingcompany(sellingCompany);
//
//        em.getTransaction().begin();
//        em.persist(product);
//        em.getTransaction().commit();
//
//        return "Product added Successfully!";
//    }
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
////
////    @POST
////    @Path("/addproduct/{sellingCompany_id}")
////    public String addProduct(@PathParam(value = "sellingCompany_id") Long sellingCompany_id, Product newProduct) {
////
////        Sellingcompany sellingCompany = em.find(Sellingcompany.class, sellingCompany_id);
////        if (sellingCompany == null) {
////            return "Selling company not found";
////        }
////
////        Product product = new Product();
////        product.setName(newProduct.getName());
////        product.setPrice(newProduct.getPrice());
////        product.setStatus(newProduct.getStatus());
////
////        product.setSellingcompany(sellingCompany);
////
////        Set<Product> products = sellingCompany.getProducts();
////        if (products == null) {
////            products = new HashSet<>();
////        }
////        products.add(product);
////
////        sellingCompany.setProducts(products);
////
////        em.getTransaction().begin();
////        em.persist(product);
////        em.merge(sellingCompany);
////        em.getTransaction().commit();
////
////        return "Product added successfully!";
////    }
//    // trip ->>> product
//    //sellingCompany ->>> selling
//
////    @POST
////    @Path("/addproduct/{sellingCompany_id}")
////    public String addProduct(@PathParam(value = "sellingCompany_id") Long sellingCompany_id, Product newProduct) {
////
////        Product product = new Product();
////        TypedQuery<Sellingcompany> query2= em.createQuery("SELECT p FROM Sellingcompany p", Sellingcompany.class);
////        List<Sellingcompany> sellingcompanies = query2.getResultList();
////
////        for(int i=0; i<sellingcompanies.size();i++){
////            if(sellingcompanies.get(i).getId() == sellingCompany_id){
////                Sellingcompany sellingCompany = sellingcompanies.get(i);
////
////                product.setName(newProduct.getName());
////                product.setPrice(newProduct.getPrice());
////                product.setStatus(newProduct.getStatus());
////
////                Set<Product> products = sellingCompany.getProducts();
////                if (products == null) {
////                    products = new HashSet<>();
////                }
////
////                products.add(newProduct);
////                product.setSellingcompany(sellingCompany);
////
////                sellingCompany.setProducts(products);
////
////                em.getTransaction().begin();
////                em.persist(product);
////                em.merge(sellingCompany);
////                em.getTransaction().commit();
////                return "Product added Successfully!";
////            }
////        }
////        return "Selling Company Not Found!";
////    }
//
//
//
//
//
////    @POST
////    @Path("/addproduct/{sellingCompany_id}")
////     public String addProduct(@PathParam(value = "sellingCompany_id") Long sellingCompany_id, Product product){
////         Set<Product> products= new HashSet<>();
////         Sellingcompany sellingCompany1 = new Sellingcompany();
////
////         sellingCompany1= em.find(Sellingcompany.class,sellingCompany_id);
////         if (sellingCompany1 == null) {
////             return "Sellingcompany does not exist";
////         }
//////         Sellingcompany sellingCompany = sellingCompany1;
////
////         //tie Author to Book
////         product.setSellingcompany(sellingCompany1);
////
//////         Product product1 = product;
////         //tie Book to Author
////         products.add(product);
////         sellingCompany1.setProducts(products);
////         return "Done!";
////
////     }
//
////    @POST
////    @Path("/register")
////    public String register(Sellingcompany sellingcompany) {
////        em.getTransaction().begin();
////        em.persist(sellingcompany);
////        em.getTransaction().commit();
////        return "Sellingcompany Successfully Registered!";
////    }
////
////    @POST
////    @Path("/add/{selling_name}")
////    public String addProduct(@PathParam("selling_name")String selling_name,Product newProduct) {
////        Product product = new Product();
////
////        product.setName(newProduct.getName());
////        product.setPrice(newProduct.getPrice());
////        product.setStatus(newProduct.getStatus());
////
////        TypedQuery<Sellingcompany> query2= em.createQuery("SELECT p FROM Sellingcompany p", Sellingcompany.class);
////        List<Sellingcompany> sellingcompanies = query2.getResultList();
////
////        for(int i=0; i<sellingcompanies.size();i++){
////            if(sellingcompanies.get(i).getUsername().equals(selling_name)){
////                product.setName(newProduct.getName());
////                product.setPrice(newProduct.getPrice());
////                product.setStatus(newProduct.getStatus());
////                product.setSellingcompany(sellingcompanies.get(i));
////                em.persist(product);
////                em.getTransaction().commit();
////                return "Product added Successfully!";
////            }
////        }
////        return "Selling Company Not Found!";
////    }
//
////    @GET
////    @Path("/get/{id}")
////    public Sellingcompany get(@PathParam("id")Integer id) {
////        em.getTransaction().begin();
////        Sellingcompany sellingcompany = em.find(Sellingcompany.class,id);
////        em.getTransaction().commit();
////        return sellingcompany;
////    }
////
////
////    @PUT
////    @Path("/updateByUsername/{username}")
////    @Consumes("text/plain")
////    public Response updateByUsername(@PathParam("username")String username, String newPassword) {
////        em.getTransaction().begin();
////        Sellingcompany sellingcompany = em.createQuery("SELECT a FROM Sellingcompany a WHERE a.username = :username", Sellingcompany.class)
////                .setParameter("username", username)
////                .getSingleResult();
////        if (sellingcompany != null) {
////            sellingcompany.setPassword(newPassword);
////            em.merge(sellingcompany);
////        }
////        em.getTransaction().commit();
////        String message = "Password for sellingcompany with username '" + username + "' has been updated successfully.";
////        return Response.status(Response.Status.OK).entity(message).build();
////    }
////
////    @DELETE
////    @Path("/delete/{id}")
////    public String register(@PathParam("id")Integer id) {
////        em.getTransaction().begin();
////        Sellingcompany sellingcompany= em.find(Sellingcompany.class, id);
////        em.remove(sellingcompany);
////        em.getTransaction().commit();
////        return "Sellingcompany Successfully Deleted!";
////    }
//}