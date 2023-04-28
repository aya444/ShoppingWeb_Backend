package rest;

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
@Path("selling")
public class SellingCompanyREST {
    EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");
    EntityManager em = entityManagerFactory.createEntityManager();

    @POST
    @Path("/register")
    public String register(Sellingcompany sellingcompany) {
        em.getTransaction().begin();
        em.persist(sellingcompany);
        em.getTransaction().commit();
        return "Sellingcompany Successfully Registered!";
    }

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
    public Sellingcompany get(@PathParam("id")Integer id) {
        em.getTransaction().begin();
        Sellingcompany sellingcompany = em.find(Sellingcompany.class,id);
        em.getTransaction().commit();
        return sellingcompany;
    }

    @GET
    @Path("/getall")
    public List<Sellingcompany> getAllAdmins() {
        em.getTransaction().begin();
        TypedQuery<Sellingcompany> query = em.createQuery("SELECT a FROM Sellingcompany a", Sellingcompany.class);
        List<Sellingcompany> sellingcompanies = query.getResultList();
        em.getTransaction().commit();
        return sellingcompanies;
    }

    @PUT
    @Path("/updateByUsername/{username}")
    @Consumes("text/plain")
    public Response updateByUsername(@PathParam("username")String username, String newPassword) {
        em.getTransaction().begin();
        Sellingcompany sellingcompany = em.createQuery("SELECT a FROM Sellingcompany a WHERE a.username = :username", Sellingcompany.class)
                .setParameter("username", username)
                .getSingleResult();
        if (sellingcompany != null) {
            sellingcompany.setPassword(newPassword);
            em.merge(sellingcompany);
        }
        em.getTransaction().commit();
        String message = "Password for sellingcompany with username '" + username + "' has been updated successfully.";
        return Response.status(Response.Status.OK).entity(message).build();
    }

    @DELETE
    @Path("/delete/{id}")
    public String register(@PathParam("id")Integer id) {
        em.getTransaction().begin();
        Sellingcompany sellingcompany= em.find(Sellingcompany.class, id);
        em.remove(sellingcompany);
        em.getTransaction().commit();
        return "Sellingcompany Successfully Deleted!";
    }
}