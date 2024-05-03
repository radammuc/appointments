package demo.radammuc.termine.service.helper;

import demo.radammuc.termine.model.Appointment;
import demo.radammuc.termine.model.Workshop;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AppointmentUtil {

    final int slotDuration;

    public AppointmentUtil(@Value("${appointment.slot.duration.minutes:15}") int slotDuration) {
        this.slotDuration = slotDuration;
    }


    public Map<LocalDate, List<CapacitySlot>> determineCapacity(LocalDateTime from, LocalDateTime to, Workshop workshop) {
        Map<LocalDate, List<CapacitySlot>> capacityArray = getCapacityList(from, to, workshop);

        for (Appointment appointment : workshop.getAppointments()) {
            LocalDateTime von = appointment.getVon();
            LocalDateTime bis = appointment.getBis();

            if (von.toLocalDate().equals(bis.toLocalDate())) {
                decreaseCapacity(capacityArray, von, bis, workshop);
            } else {
                // TODO handle appointments over more than 1 day
                throw new UnsupportedOperationException();
            }
        }

        return capacityArray;
    }

    public List<FreeCapacity> determineFreeCapacities(List<CapacitySlot> capacitySlotList) {
        List<FreeCapacity> freeCapacities = new ArrayList<>();

        FreeCapacity freeCapacity = null;

        for (CapacitySlot slot : capacitySlotList) {
            if (slot.getCapacity() > 0) {
                if (freeCapacity != null) {
                    freeCapacity.setLength(freeCapacity.getLength() + 1);
                } else {
                    freeCapacity = new FreeCapacity();
                    freeCapacity.setStartSlot(slot);
                    freeCapacity.setLength(1);
                }
            } else if (freeCapacity != null) {
                freeCapacities.add(freeCapacity);
                freeCapacity = null;
            }
        }

        if (freeCapacity != null) {
            freeCapacities.add(freeCapacity);
        }

        return freeCapacities;
    }

    Map<LocalDate, List<CapacitySlot>> getCapacityList(LocalDateTime from, LocalDateTime to, Workshop workshop) {

        List<CapacitySlot> capacitySlots = getCapacitySlots(workshop);

        LocalDate fromDate = from.toLocalDate();
        LocalDate toDate = to.toLocalDate();

        Map<LocalDate, List<CapacitySlot>> capacityPerDayList = new HashMap<>();

        for (LocalDate date : getDays(fromDate, toDate)) {
            List<CapacitySlot> capacitySlotsCopy = new ArrayList<>();
            for (CapacitySlot slot : capacitySlots) {
                capacitySlotsCopy.add(CapacitySlot.builder().start(slot.getStart()).capacity(slot.getCapacity()).build());
            }

            capacityPerDayList.put(date, capacitySlotsCopy);
        }

        return capacityPerDayList;
    }

    void decreaseCapacity(Map<LocalDate, List<CapacitySlot>> capacityArray, LocalDateTime von, LocalDateTime bis, Workshop workshop) {
        List<CapacitySlot> capacitySlots = capacityArray.getOrDefault(von.toLocalDate(), getCapacitySlots(workshop));

        // avoid problems if appointment times don't align with slot times
        LocalTime slotStartFrom = DateUtil.getSlotStart(LocalTime.of(von.getHour(), von.getMinute()), slotDuration);
        LocalTime slotEndTo = DateUtil.getSlotStart(LocalTime.of(bis.getHour(), bis.getMinute()), slotDuration).plusMinutes(slotDuration);

        for (CapacitySlot slot : capacitySlots) {
            if (!slot.getStart().isBefore(slotStartFrom) && !slot.getStart().plusMinutes(slotDuration).isAfter(slotEndTo)) {
                slot.decreaseCapacity();
            }
        }
    }

    List<CapacitySlot> getCapacitySlots(Workshop workshop) {
        List<CapacitySlot> capacitySlots = new ArrayList<>();

        // TODO adjust for office hours that don't start or end at :00, :15, :30, :45
        LocalTime currentTime = workshop.getOfficeStartTime();

        while (currentTime.isBefore(workshop.getOfficeEndTime())) {
            CapacitySlot slot = CapacitySlot.builder().start(currentTime).capacity(workshop.getCapacity()).build();
            capacitySlots.add(slot);

            currentTime = slot.getStart().plusMinutes(slotDuration);
        }

        return capacitySlots;
    }

    List<LocalDate> getDays(LocalDate fromDate, LocalDate toDate) {
        List<LocalDate> days = new ArrayList<>();

        LocalDate nextDay = fromDate;

        while (!nextDay.isAfter(toDate)) {
            days.add(nextDay);
            nextDay = nextDay.plusDays(1);
        }

        return days;
    }
}
