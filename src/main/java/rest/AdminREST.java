package rest;

import entities.Admin;
import jakarta.persistence.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import javax.ejb.Stateless;
import java.util.List;

@Stateless
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("admin")
public class AdminREST {
    EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");
    EntityManager em = entityManagerFactory.createEntityManager();

    @POST
    @Path("/register")
    public String register(Admin admin) {
        em.getTransaction().begin();
        em.persist(admin);
        em.getTransaction().commit();
        return "Admin Successfully Registered!";
    }

    @POST
    @Path("/login")
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
    public Admin get(@PathParam("id")Integer id) {
        em.getTransaction().begin();
        Admin admin = em.find(Admin.class,id);
        em.getTransaction().commit();
        return admin;
    }

    @GET
    @Path("/getall")
    public List<Admin> getAllAdmins() {
        em.getTransaction().begin();
        TypedQuery<Admin> query = em.createQuery("SELECT a FROM Admin a", Admin.class);
        List<Admin> admins = query.getResultList();
        em.getTransaction().commit();
        return admins;
    }

    @PUT
    @Path("/updateByUsername/{username}")
    @Consumes("text/plain")
    public Response updateByUsername(@PathParam("username")String username, String newPassword) {
        em.getTransaction().begin();
        Admin admin = em.createQuery("SELECT a FROM Admin a WHERE a.username = :username", Admin.class)
                .setParameter("username", username)
                .getSingleResult();
        if (admin != null) {
            admin.setPassword(newPassword);
            em.merge(admin);
        }
        em.getTransaction().commit();
        String message = "Password for admin with username '" + username + "' has been updated successfully.";
        return Response.status(Response.Status.OK).entity(message).build();
    }

    @DELETE
    @Path("/delete/{id}")
    public String register(@PathParam("id")Integer id) {
        em.getTransaction().begin();
        Admin a= em.find(Admin.class, id);
        em.remove(a);
        em.getTransaction().commit();
        return "Admin Successfully Deleted!";
    }
}