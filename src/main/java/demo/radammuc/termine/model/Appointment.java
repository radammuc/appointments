package demo.radammuc.termine.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointment")
@Getter
@Setter
@ToString
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private LocalDateTime von;
    private LocalDateTime bis;

    @ManyToOne
//    @JoinColumn(name = "workshop_id")
    private Workshop workshop;

    @ManyToOne
    private ServiceType serviceType;

    @Version
    private Integer version;

}


