package demo.radammuc.termine.mapper;

import demo.radammuc.termine.dto.Termin;
import demo.radammuc.termine.model.Appointment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {


    @Mapping(target = "werkstattName", source = "workshop.name")
    @Mapping(target = "leistung", source = "serviceType.description")
    @Mapping(target = "leistungsId", source = "serviceType.code")
    @Mapping(target = "id", source = "id", qualifiedByName = "toString")
    Termin map(Appointment appointment);

    List<Termin> map(List<Appointment> appointments);


    @Named("toString")
    public static String fromInt(Integer id) {
        return id == null ? null : Integer.toString(id);
    }
}
