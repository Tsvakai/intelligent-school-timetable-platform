package zw.co.upvalley.authservice.persistence.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import zw.co.upvalley.authservice.persistence.entity.User;
import zw.co.upvalley.authservice.persistence.repository.base.BaseRepository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends BaseRepository<User, Long> {

    Optional<User> findByUsernameAndIsDeletedFalse(String username);

    Optional<User> findByEmailAndIsDeletedFalse(String email);

    Boolean existsByUsernameAndIsDeletedFalse(String username);

    Boolean existsByEmailAndIsDeletedFalse(String email);

    @Query("SELECT u FROM User u WHERE (u.username = :usernameOrEmail OR u.email = :usernameOrEmail) AND u.isDeleted = false")
    Optional<User> findByUsernameOrEmailAndIsDeletedFalse(@Param("usernameOrEmail") String usernameOrEmail);

    @Query("SELECT COUNT(u) > 0 FROM User u WHERE (u.username = :username OR u.email = :email) AND u.id != :excludeId AND u.isDeleted = false")
    Boolean existsByUsernameOrEmailExcludingId(
            @Param("username") String username,
            @Param("email") String email,
            @Param("excludeId") Long excludeId
    );

}