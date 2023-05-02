package cofiguration;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.sql.DriverManager;
import java.sql.SQLException;

@Startup
@Singleton
public class Connection {
    private  Connection conn;

    @PostConstruct
    public void init() throws SQLException {
        try {
            conn = (Connection) DriverManager.getConnection("jdbc:mysql://localhost:3306/shopping_db", "root", "Podman56");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Project Connected to database!");
    }

    public Connection getConn(){ return conn;}

}
