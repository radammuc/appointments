package demo.radammuc.termine.service.helper;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalTime;

@Builder
@Getter
@Setter
@ToString
public class CapacitySlot {
    LocalTime start;
    int capacity;

    public void decreaseCapacity() {
        --capacity;
    }
}
