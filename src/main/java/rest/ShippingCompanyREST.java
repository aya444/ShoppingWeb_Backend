package rest;

import entities.Shippingcompany;
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
    @Path("/register")
    public String register(Shippingcompany shippingcompany) {
        em.getTransaction().begin();
        em.persist(shippingcompany);
        em.getTransaction().commit();
        return "Shippingcompany Successfully Registered!";
    }

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

    @GET
    @Path("/get/{id}")
    public Shippingcompany get(@PathParam("id")Integer id) {
        em.getTransaction().begin();
        Shippingcompany shippingcompany = em.find(Shippingcompany.class,id);
        em.getTransaction().commit();
        return shippingcompany;
    }

    @GET
    @Path("/getall")
    public List<Shippingcompany> getAllAdmins() {
        em.getTransaction().begin();
        TypedQuery<Shippingcompany> query = em.createQuery("SELECT a FROM Shippingcompany a", Shippingcompany.class);
        List<Shippingcompany> sellingcompanies = query.getResultList();
        em.getTransaction().commit();
        return sellingcompanies;
    }

    @PUT
    @Path("/updateByUsername/{username}")
    @Consumes("text/plain")
    public Response updateByUsername(@PathParam("username")String username, String newPassword) {
        em.getTransaction().begin();
        Shippingcompany shippingcompany = em.createQuery("SELECT a FROM Shippingcompany a WHERE a.username = :username", Shippingcompany.class)
                .setParameter("username", username)
                .getSingleResult();
        if (shippingcompany != null) {
            shippingcompany.setPassword(newPassword);
            em.merge(shippingcompany);
        }
        em.getTransaction().commit();
        String message = "Password for shippingcompany with username '" + username + "' has been updated successfully.";
        return Response.status(Response.Status.OK).entity(message).build();
    }

    @DELETE
    @Path("/delete/{id}")
    public String register(@PathParam("id")Integer id) {
        em.getTransaction().begin();
        Shippingcompany shippingcompany= em.find(Shippingcompany.class, id);
        em.remove(shippingcompany);
        em.getTransaction().commit();
        return "Shippingcompany Successfully Deleted!";
    }
}
