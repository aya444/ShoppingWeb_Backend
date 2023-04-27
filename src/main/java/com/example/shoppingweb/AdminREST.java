package com.example.shoppingweb;

import entities.Admin;
import jakarta.persistence.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path("hello-world")
public class AdminREST {
    EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");
    EntityManager em = entityManagerFactory.createEntityManager();
    EntityTransaction transaction = em.getTransaction();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/register")
    public String register(Admin admin) {
        em.getTransaction().begin();
        em.persist(admin);
        em.getTransaction().commit();
        return "Done!";
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/delete/{id}")
    public String register(@PathParam("id")Integer id) {
        em.getTransaction().begin();
        Admin a= em.find(Admin.class, id);
        em.remove(a);
        em.getTransaction().commit();
        return "Done Delete!";
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/get")
    public String get() {
        return "Done!";
    }

}