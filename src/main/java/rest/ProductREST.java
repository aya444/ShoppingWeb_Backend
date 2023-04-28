package rest;

import entities.Product;
import jakarta.persistence.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import javax.ejb.Stateless;
import java.util.List;

import entities.Sellingcompany;

@Stateless
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("product")
public class ProductREST {
    EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");
    EntityManager em = entityManagerFactory.createEntityManager();

    @POST
    @Path("/{id}/add")
    public String createTrip(@PathParam(value = "id") Long id, Product newProduct) {
        // map the fields from DTO to entity
        Product product = new Product();
        product.setName(newProduct.getName());
        product.setPrice(newProduct.getPrice());
        product.setStatus(newProduct.getStatus());

        // find the selling company by ID
        Sellingcompany sellingCompany = em.find(Sellingcompany.class, id);
        if (sellingCompany == null) {
            throw new RuntimeException("Selling Company Not Found!");
        }
        product.setSellingcompany(sellingCompany);

        // persist the entity
        em.getTransaction().begin();
        em.persist(product);
        em.getTransaction().commit();

        return "Done!";
    }


    @POST
    @Path("/findbysellingcompanyid/{id}")
    public List<Product> findProductsBySellingCompany(@PathParam("id")Integer id) {
        em.getTransaction().begin();
        TypedQuery<Product> query = em.createQuery("SELECT p FROM Product p WHERE p.sellingcompany.id = :id", Product.class);
        List<Product> products = query.getResultList();
        em.getTransaction().commit();
        return products;
    }

    @GET
    @Path("/sale")
    public List<Product> get() {
        em.getTransaction().begin();
        TypedQuery<Product> query = em.createQuery("SELECT p FROM Product p WHERE p.status = 'sale'", Product.class);
        List<Product> products = query.getResultList();
        em.getTransaction().commit();
        return products;
    }

    @GET
    @Path("/get/{id}")
    public Product get(@PathParam("id")Integer id) {
        em.getTransaction().begin();
        Product product = em.find(Product.class,id);
        em.getTransaction().commit();
        return product;
    }

    @GET
    @Path("/getall")
    public List<Product> getAllAdmins() {
        em.getTransaction().begin();
        TypedQuery<Product> query = em.createQuery("SELECT a FROM Product a", Product.class);
        List<Product> sellingcompanies = query.getResultList();
        em.getTransaction().commit();
        return sellingcompanies;
    }

    @DELETE
    @Path("/delete/{id}")
    public String register(@PathParam("id")Integer id) {
        em.getTransaction().begin();
        Product product= em.find(Product.class, id);
        em.remove(product);
        em.getTransaction().commit();
        return "Product Successfully Deleted!";
    }
}
