package demo.radammuc.termine.service.helper;

import demo.radammuc.termine.model.Workshop;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AppointmentUtilTest {

    // class under test
    AppointmentUtil appointmentUtil = new AppointmentUtil(15);

    @Test
    void getDays_nextDay() {
        LocalDate from = LocalDate.now();
        LocalDate to = from.plusDays(1);

        List<LocalDate> days = appointmentUtil.getDays(from, to);

        assertEquals(2, days.size());
    }

    @Test
    void getDays_sameDay() {
        LocalDate from = LocalDate.now();

        List<LocalDate> days = appointmentUtil.getDays(from, from);

        assertEquals(1, days.size());
    }

    @Test
    void getDays_in2Days() {
        LocalDate from = LocalDate.now();
        LocalDate to = from.plusDays(2);

        List<LocalDate> days = appointmentUtil.getDays(from, to);

        assertEquals(3, days.size());
    }


    @Test
    void getCapacityArray() {

        Workshop workshop = new Workshop();
        workshop.setOfficeHoursStart("07:30");
        workshop.setOfficeHoursEnd("18:30");
        workshop.setCapacity(2);

        appointmentUtil.getCapacityList(LocalDateTime.now(), LocalDateTime.now(), workshop);

        // TODO implement test case
    }

    @Test
    void getCapacitySlots() {
        final int slotDuration = 15;

        Workshop workshop = new Workshop();
        workshop.setCapacity(3);
        workshop.setOfficeHoursStart("07:30");
        workshop.setOfficeHoursEnd("18:30");

        List<CapacitySlot> capacitySlots = appointmentUtil.getCapacitySlots(workshop);


        LocalTime start = workshop.getOfficeStartTime();
        LocalTime end = workshop.getOfficeEndTime();
        int expectedSize = ((end.getHour() * 60 + end.getMinute()) / slotDuration) - ((start.getHour() * 60 + start.getMinute()) / slotDuration);

        assertEquals(expectedSize, capacitySlots.size());
    }

    @Test
    void decreaseCapacity() {
        Workshop workshop = new Workshop();
        workshop.setCapacity(2);
        workshop.setOfficeHoursStart("08:00");
        workshop.setOfficeHoursEnd("18:00");

        Map<LocalDate, List<CapacitySlot>> capacityArray = new HashMap<>();
        LocalDateTime von = LocalDateTime.now().withHour(10).withMinute(0);
        LocalDateTime bis = von.plusMinutes(25);

        List<CapacitySlot> capacitySlots = new ArrayList<>();
        LocalTime startTime = LocalTime.of(von.getHour() - 1, 0, 0);
        IntStream.range(0, 8).forEach(i -> capacitySlots.add(CapacitySlot.builder().capacity(2).start(startTime.plusMinutes(Long.valueOf(i * 15L).intValue()))
                .build()));

        capacityArray.put(von.toLocalDate(), capacitySlots);

        appointmentUtil.decreaseCapacity(capacityArray, von, bis, workshop);

        // 25 min appointment => for 2 slots the capacity must be decreased
        assertEquals(2, capacitySlots.stream().filter(c -> c.getCapacity() == 1).count());

    }

    @Test
    void determineFreeCapacities() {
        List<CapacitySlot> capacitySlots = new ArrayList<>();
        appendCapacitySlot(capacitySlots, 0);
        appendCapacitySlot(capacitySlots, 1);
        appendCapacitySlot(capacitySlots, 2);
        appendCapacitySlot(capacitySlots, 0);
        appendCapacitySlot(capacitySlots, 0);
        appendCapacitySlot(capacitySlots, 1);
        appendCapacitySlot(capacitySlots, 2);
        appendCapacitySlot(capacitySlots, 1);
        appendCapacitySlot(capacitySlots, 2);
        appendCapacitySlot(capacitySlots, 0);
        appendCapacitySlot(capacitySlots, 1);

        List<FreeCapacity> freeCapacities = appointmentUtil.determineFreeCapacities(capacitySlots);

        assertEquals(3, freeCapacities.size());

        assertEquals(2, freeCapacities.get(0).getLength());
        assertEquals(4, freeCapacities.get(1).getLength());
        assertEquals(1, freeCapacities.get(2).getLength());
    }

    private void appendCapacitySlot(List<CapacitySlot> capacitySlots, int capacity) {
        capacitySlots.add(CapacitySlot.builder().capacity(capacity).build());
    }
}