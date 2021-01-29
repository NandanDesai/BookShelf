package io.github.nandandesai.insecure.repositories;


import io.github.nandandesai.insecure.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private Connection con;

    @Autowired
    private RoleRepository roleRepository;

    public void save(User user) throws SQLException {
        String sql = "";
        Optional<User> userOptional = findByEmail(user.getEmail());
        //if the user exists, then update the user info
        if(userOptional.isPresent()){
            sql = String.format("UPDATE users SET email='%s', password='%s', fullName='%s', profilePicName='%s', lastLogin='%s', roleName='%s' WHERE id=%d",
                    user.getEmail(), user.getPassword(), user.getFullName(), user.getProfilePicName(), Timestamp.valueOf(user.getLastLogin()), user.getRole().getName(), user.getId());
            logger.info("Executing SQL statement: " + sql);
            con.createStatement().execute(sql);
            logger.info("User successfully updated. ID: " + user.getId());
        }else {
            //this is a new user. So insert the row into the database
            sql = String.format("INSERT INTO users(email, password, fullName, profilePicName, lastLogin, roleName) VALUES('%s','%s','%s','%s','%s','%s')",
                    user.getEmail(), user.getPassword(), user.getFullName(), user.getProfilePicName(), Timestamp.valueOf(user.getLastLogin()), user.getRole().getName());
            logger.info("Executing SQL statement: " + sql);
            Statement stmt = con.createStatement();
            stmt.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
                user.setId(id);
            }
            logger.info("User successfully created with ID: " + user.getId());
        }
        con.commit();
    }

    public Optional<User> findById(Integer id) throws SQLException {
        User user = null;
        String sql = String.format("SELECT * FROM users WHERE id=%d", id);
        logger.info("Executing SQL statement: "+sql);
        ResultSet resultSet = con.createStatement().executeQuery(sql);
        if (resultSet.next()){
            user = new User();
            user.setId(resultSet.getInt(1));
            user.setEmail(resultSet.getString(2));
            user.setPassword(resultSet.getString(3));
            user.setFullName(resultSet.getString(4));
            user.setProfilePicName(resultSet.getString(5));
            user.setLastLogin(resultSet.getTimestamp(6).toLocalDateTime());
            user.setRole(roleRepository.findById(resultSet.getString(7)).get());
        }
        return Optional.ofNullable(user);
    }

    public Optional<User> findByEmail(String email) throws SQLException {
        User user = null;
        String sql = String.format("SELECT * FROM users WHERE email='%s'", email);
        logger.info("Executing SQL statement: "+sql);
        ResultSet resultSet = con.createStatement().executeQuery(sql);
        if (resultSet.next()){
            user = new User();
            user.setId(resultSet.getInt(1));
            user.setEmail(resultSet.getString(2));
            user.setPassword(resultSet.getString(3));
            user.setFullName(resultSet.getString(4));
            user.setProfilePicName(resultSet.getString(5));
            user.setLastLogin(resultSet.getTimestamp(6).toLocalDateTime());
            user.setRole(roleRepository.findById(resultSet.getString(7)).get());
        }
        return Optional.ofNullable(user);
    }

    public List<User> findAll() throws SQLException {
        List<User> userList = new ArrayList<>();
        String sql = "SELECT * FROM users";
        logger.info("Executing SQL statement: "+sql);
        ResultSet resultSet = con.createStatement().executeQuery(sql);
        while (resultSet.next()){
            User user = new User();
            user.setId(resultSet.getInt(1));
            user.setEmail(resultSet.getString(2));
            user.setPassword(resultSet.getString(3));
            user.setFullName(resultSet.getString(4));
            user.setProfilePicName(resultSet.getString(5));
            user.setLastLogin(resultSet.getTimestamp(6).toLocalDateTime());
            user.setRole(roleRepository.findById(resultSet.getString(7)).get());
            userList.add(user);
        }
        return userList;
    }
}
