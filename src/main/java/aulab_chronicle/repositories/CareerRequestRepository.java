package aulab_chronicle.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import aulab_chronicle.models.CareerRequest;

public interface CareerRequestRepository extends JpaRepository<CareerRequest, Long> {

    List<CareerRequest> findByIsCheckedFalse();

    @Query("SELECT c.user.id FROM CareerRequest c")
    List<Long> findAllUserIds();

    @Query("SELECT c.role.id FROM CareerRequest c WHERE c.user.id = :userId")
    List<Long> findByUserId(@Param("userId") Long userId);
}