package io.github.nandandesai.insecure.repositories;

import io.github.nandandesai.insecure.models.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Repository
public class RoleRepository {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private Connection con;

    public void save(Role role){

    }

    public Optional<Role> findById(String name) throws SQLException {
        String sql = String.format("SELECT * FROM roles WHERE name = '%s'", name);
        logger.info("Executing SQL statement: "+sql);
        ResultSet resultSet = con.createStatement().executeQuery(sql);
        Role role = null;
        while(resultSet.next()){
            role = new Role();
            role.setName(resultSet.getString(1)).setValue(resultSet.getInt(2));
        }
        return Optional.of(role);
    }
}
