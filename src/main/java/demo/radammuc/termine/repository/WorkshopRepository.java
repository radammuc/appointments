package demo.radammuc.termine.repository;

import demo.radammuc.termine.model.Appointment;
import demo.radammuc.termine.model.Workshop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WorkshopRepository extends JpaRepository<Workshop, Integer> {

    @Query("select a from Workshop w join w.appointments a join a.serviceType s where w.id = :id and a.von >= :from and a.bis <= :to and s.code = :serviceCode")
    List<Appointment> findAppointments(Integer id, LocalDateTime from, LocalDateTime to, String serviceCode);

    List<Workshop> findByNameIgnoreCase(String name);

}
