package rest;

import entities.*;
import jakarta.persistence.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import javax.ejb.Stateless;
import java.util.List;
@Stateless
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("shipping")
public class ShippingCompanyREST {
    EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");
    EntityManager em = entityManagerFactory.createEntityManager();

    @POST
    @Path("/login")
    public Response login(Shippingcompany shippingcompany) {
        em.getTransaction().begin();
        String username = shippingcompany.getUsername();
        String password = shippingcompany.getPassword();
        if (username == null || password == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Username and password are required").build();
        }
        try {
            Shippingcompany foundShippingcompany= em.createQuery("SELECT a FROM Shippingcompany a WHERE a.username = :username", Shippingcompany.class)
                    .setParameter("username", username)
                    .getSingleResult();

            if (foundShippingcompany.getPassword().equals(password)) {
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

    //add regions it supports
    @POST
    @Path("/add_region/{id}")
    public String addRegion(@PathParam("id")Integer companyId, Regions region){
        Shippingcompany shippingingCompany = em.find(Shippingcompany.class, companyId);
        Regions newRegion= new Regions();
        newRegion.setCompanyId(companyId);
        newRegion.setRegionName(region.getRegionName());
        newRegion.setShippingcompany(shippingingCompany);
        em.getTransaction().begin();
        em.persist(newRegion);
        em.getTransaction().commit();
        return "Region Successfully added to your supported regions";
    }

    //process current requests
    @GET
    @Path("/process_requests/{shipping_id}")
    public String process (@PathParam("shipping_id")int shipping_id) {
        TypedQuery<Orders> query= em.createQuery("SELECT o FROM Orders o", Orders.class);
        List<Orders> allOrders = query.getResultList();

        for(int i=0; i< allOrders.size(); i++){
            Shippingcompany shippingcompany = em.find(Shippingcompany.class,shipping_id);
            if(allOrders.get(i).getStatus().equals("current") && (shippingcompany != null))
            {
                allOrders.get(i).setShippingId(shipping_id);
                allOrders.get(i).setShippingcompany(shippingcompany);
                return "order of customer "+allOrders.get(i).getCustomerId()+" is processed";
            }
        }
        return null;
    }
}
