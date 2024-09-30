package com.rak.usermanagement.common.repository;

import com.rak.usermanagement.common.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author Mohammmed Javad
 * @version 1.0

 */

public interface UserRepo extends JpaRepository<User, String> {
    Optional<User> findOneByLoginId(String userId);

    User findByLoginId(String userId);

    User findUserById(Long id);

    Integer deleteUserById(Long id);

    /*    Optional<User> findByLoginIdAndPassword(String login);*/
}
