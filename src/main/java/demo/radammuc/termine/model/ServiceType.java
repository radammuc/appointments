package demo.radammuc.termine.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "service_type")
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ServiceType {

    @Id
    private String code;

    private String description;
}
