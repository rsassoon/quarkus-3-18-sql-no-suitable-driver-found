package org.acme;

import io.quarkus.logging.Log;
import io.vertx.core.Vertx;
import io.vertx.jdbcclient.JDBCConnectOptions;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.sqlclient.PoolOptions;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Dependent
// @ApplicationScoped
public class VertxJDBCPoolConfig {

    @ConfigProperty(name = "properties.db.user")
    String dbUser;

    @ConfigProperty(name = "properties.db.password")
    String dbPassword;

    @ConfigProperty(name = "properties.db.url")
    String dbUrl;

    @Produces
    //    @ApplicationScoped
    public JDBCPool createJdbcPool(Vertx vertx) {

        JDBCConnectOptions connectOptions = new JDBCConnectOptions()
                //                .setJdbcUrl(dbUrl)
                .setJdbcUrl(dbUrl.replace("jdbc:mysql:", "jdbc:aws-wrapper:mysql:"))
                //                .setJdbcUrl("jdbc:aws-wrapper:mysql:" + dbUrl)
                //                .setJdbcUrl("jdbc:mysql:" + dbUrl)
                .setUser(dbUser)
                .setPassword(dbPassword);

        Log.warnv("jdbc url: {0}", connectOptions.getJdbcUrl());

        Enumeration<Driver> drivers = DriverManager.getDrivers();

        if (!drivers.hasMoreElements()) {
            Log.warn("No JDBC drivers found in the classpath.");
        } else {
            Log.warn("Available JDBC drivers:");
            int count = 1;
            while (drivers.hasMoreElements()) {
                Driver driver = drivers.nextElement();
                Log.warn(count + ". " + driver.getClass().getName());
                count++;
            }
        }

        PoolOptions poolOptions = new PoolOptions().setMaxSize(5);

        return JDBCPool.pool(vertx, connectOptions, poolOptions);
    }
}
