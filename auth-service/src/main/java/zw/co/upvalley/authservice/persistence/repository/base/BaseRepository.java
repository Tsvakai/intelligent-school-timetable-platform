package zw.co.upvalley.authservice.persistence.repository.base;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;
import zw.co.upvalley.authservice.persistence.entity.base.BaseEntity;

import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface BaseRepository<T extends BaseEntity, ID> extends JpaRepository<T, ID> {

    // Use @Query to avoid the generic type comparison issue
    @Query("SELECT e FROM #{#entityName} e WHERE e.id = :id AND e.isDeleted = false")
    Optional<T> findByIdAndIsDeletedFalse(@Param("id") ID id);

    @Query("SELECT e FROM #{#entityName} e WHERE e.isDeleted = false")
    List<T> findAllByIsDeletedFalse();

    @Query("SELECT COUNT(e) > 0 FROM #{#entityName} e WHERE e.id = :id AND e.isDeleted = false")
    boolean existsByIdAndIsDeletedFalse(@Param("id") ID id);

    // Soft delete by ID
    @Modifying
    @Query("UPDATE #{#entityName} e SET e.isDeleted = true WHERE e.id = :id")
    void softDeleteById(@Param("id") ID id);

    // Count active entities
    @Query("SELECT COUNT(e) FROM #{#entityName} e WHERE e.isDeleted = false")
    long countByIsDeletedFalse();
}