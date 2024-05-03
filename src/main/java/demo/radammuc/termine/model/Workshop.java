package demo.radammuc.termine.model;


import demo.radammuc.termine.service.helper.DateUtil;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "workshop")
@Getter
@Setter
@ToString
public class Workshop {

    @Id
    private Integer id;

    @Column(unique = true)
    private String name;

    private Integer capacity;

    private String officeHoursStart;
    private String officeHoursEnd;

    @Transient
    private LocalTime officeStartTime;

    @Transient
    private LocalTime officeEndTime;

    @OneToMany(mappedBy = "workshop")
    private List<OfferedService> offeredServices;

    @OneToMany(mappedBy = "workshop", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Appointment> appointments;

    private Boolean locked;

    @Version
    private Integer version;

    public boolean isLocked() {
        return locked != null && locked;
    }

    public LocalTime getOfficeStartTime() {
        if (officeStartTime == null) {
            officeStartTime = DateUtil.timeFromString(officeHoursStart);
        }

        return officeStartTime;
    }

    public LocalTime getOfficeEndTime() {
        if (officeEndTime == null) {
            officeEndTime = DateUtil.timeFromString(officeHoursEnd);
        }

        return officeEndTime;
    }
}
