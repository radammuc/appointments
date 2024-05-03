package demo.radammuc.termine.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "offered_service")
@IdClass(OfferedServiceId.class)
@ToString
@Getter
@Setter
public class OfferedService {
    @Id
    @ManyToOne
    @JoinColumn(name = "workshop_id")
    private Workshop workshop;

    @Id
    @ManyToOne
    private ServiceType serviceType;

    /**
     * Duration in minutes
     */
    private Integer duration;
}
