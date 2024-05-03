package demo.radammuc.termine.service.helper;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Builder
@Getter
public class CapacityPerDay {
    LocalDate date;
    List<CapacitySlot> capacityList;
}
