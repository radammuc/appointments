package demo.radammuc.termine.service;

import demo.radammuc.termine.dto.Termin;
import demo.radammuc.termine.exception.NotFoundException;
import demo.radammuc.termine.mapper.AppointmentMapper;
import demo.radammuc.termine.model.Appointment;
import demo.radammuc.termine.model.OfferedService;
import demo.radammuc.termine.model.Workshop;
import demo.radammuc.termine.repository.WorkshopRepository;
import demo.radammuc.termine.service.helper.*;
import lombok.extern.jbosslog.JBossLog;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@JBossLog
public class AppointmentSuggestionService {

    final WorkshopRepository workshopRepository;

    final AppointmentMapper appointmentMapper;

    final AppointmentUtil appointmentUtil;

    final int slotDuration;

    public AppointmentSuggestionService(WorkshopRepository workshopRepository, AppointmentMapper appointmentMapper, AppointmentUtil appointmentUtil,
                                        @Value("${appointment.slot.duration.minutes:15}") int slotDuration) {
        this.workshopRepository = workshopRepository;
        this.appointmentMapper = appointmentMapper;
        this.appointmentUtil = appointmentUtil;
        this.slotDuration = slotDuration;
    }

    @Transactional(readOnly = true)
    public List<Termin> getTerminvorschlaege(String werkstattId, String leistungsId, String von, String bis) {

        Optional<Workshop> workshopOptional = workshopRepository.findById(Integer.valueOf(werkstattId));
        Workshop workshop = workshopOptional.orElseThrow(() -> new NotFoundException(""));

        LocalDateTime from = DateUtil.fromString(von);
        LocalDateTime to = DateUtil.fromString(bis);

        // TODO validate if to > from

        List<Appointment> appointments = findSuggestedAppointments(workshop, from, to, leistungsId);
        return appointmentMapper.map(appointments);
    }

    List<Appointment> findSuggestedAppointments(Workshop workshop, LocalDateTime from, LocalDateTime to, String serviceCode) {

        OfferedService offeredService = ServiceUtil.getService(workshop, serviceCode);
        int serviceDuration = offeredService.getDuration();
        int slotsForService = Math.ceilDiv(serviceDuration, slotDuration);

        LocalDateTime firstSlotFrom = DateUtil.getSlotStart(from, slotDuration);
        LocalDateTime lastSlotTo = DateUtil.getSlotStart(to, slotDuration);

        if (from.toLocalTime().isBefore(workshop.getOfficeStartTime())) {
            firstSlotFrom = from.withMinute(workshop.getOfficeStartTime().getMinute()).withHour(workshop.getOfficeStartTime().getHour());
        }

        if (lastSlotTo.plusMinutes(slotDuration).toLocalTime().isAfter(workshop.getOfficeEndTime())) {
            lastSlotTo = lastSlotTo.withMinute(workshop.getOfficeEndTime().getMinute()).withHour(workshop.getOfficeEndTime().getHour()).minusMinutes(slotDuration);
        }

        Map<LocalDate, List<CapacitySlot>> capacityArray = appointmentUtil.determineCapacity(firstSlotFrom, lastSlotTo, workshop);

        List<Appointment> suggestedAppointments = new ArrayList<>();

        LocalDate currentDay = from.toLocalDate();

        while (!currentDay.isAfter(to.toLocalDate())) {
            List<CapacitySlot> capacitySlotList = capacityArray.get(firstSlotFrom.toLocalDate());

            List<FreeCapacity> freeCapacities = appointmentUtil.determineFreeCapacities(capacitySlotList);

            for (FreeCapacity freeCapacity : freeCapacities) {
                while (freeCapacity.getLength() >= slotsForService) {
                    Appointment appointment = new Appointment();
                    LocalDateTime startTime = currentDay.atTime(freeCapacity.getStartSlot().getStart());
                    appointment.setVon(startTime);
                    appointment.setBis(startTime.plusMinutes(offeredService.getDuration()));
                    appointment.setServiceType(offeredService.getServiceType());
                    appointment.setWorkshop(workshop);
                    suggestedAppointments.add(appointment);

                    freeCapacity.setLength(freeCapacity.getLength() - slotsForService);
                    freeCapacity.getStartSlot().setStart(freeCapacity.getStartSlot().getStart().plusMinutes(serviceDuration));
                }
            }

            currentDay = currentDay.plusDays(1);
        }

        return suggestedAppointments;
    }

}
