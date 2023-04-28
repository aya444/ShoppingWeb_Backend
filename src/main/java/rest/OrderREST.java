//package rest;
//
//import entities.*;
//import entities.Order;
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.EntityManagerFactory;
//import jakarta.persistence.Persistence;
//import jakarta.persistence.TypedQuery;
//import jakarta.ws.rs.*;
//import jakarta.ws.rs.core.MediaType;
//
//import javax.ejb.Stateless;
//import java.util.List;
//
//@Stateless
//@Consumes(MediaType.APPLICATION_JSON)
//@Produces(MediaType.APPLICATION_JSON)
//@Path("order")
//public class OrderREST {
//    EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");
//    EntityManager em = entityManagerFactory.createEntityManager();
//
//    @POST
//    @Path("/{product_id}/{shipping_id}/add")
//    public String createTrip(@PathParam(value = "product_id") Long product_id, @PathParam(value = "shipping_id") Long shipping_id, Order newOrder) {
//        // map the fields from DTO to entity
//        Order order = new Order();
//        order.setId(newOrder.getId());
//
//        // find the selling company by ID
//        Shippingcompany shippingcompany = em.find(Shippingcompany.class, shipping_id);
//        if (shippingcompany == null) {
//            throw new RuntimeException("Shipping Company Not Found!");
//        }
//        order.setShippingcompany(shippingcompany);
//
//        Product product = em.find(Product.class, product_id);
//        if (product == null) {
//            throw new RuntimeException("Product Not Found!");
//        }
//        order.setProduct(product);
//
//        // persist the entity
//        em.getTransaction().begin();
//        em.persist(order);
//        em.getTransaction().commit();
//
//        return "Done!";
//    }
//
//    @GET
//    @Path("/getall")
//    public List<Order> getAll() {
//        em.getTransaction().begin();
//        TypedQuery<Order> query = em.createQuery("SELECT a FROM Order a", Order.class);
//        List<Order> orders = query.getResultList();
//        em.getTransaction().commit();
//        return orders;
//    }
//}
