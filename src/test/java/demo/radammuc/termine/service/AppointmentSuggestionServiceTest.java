package demo.radammuc.termine.service;

import demo.radammuc.termine.model.Appointment;
import demo.radammuc.termine.model.OfferedService;
import demo.radammuc.termine.model.ServiceType;
import demo.radammuc.termine.model.Workshop;
import demo.radammuc.termine.repository.WorkshopRepository;
import demo.radammuc.termine.service.helper.AppointmentUtil;
import demo.radammuc.termine.service.helper.CapacitySlot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppointmentSuggestionServiceTest {

    @Mock
    AppointmentUtil appointmentUtil;

    @Mock
    WorkshopRepository workshopRepository;

    AppointmentSuggestionService appointmentSuggestionService;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(appointmentUtil, "slotDuration", 15);
        appointmentSuggestionService = new AppointmentSuggestionService(workshopRepository, null, appointmentUtil, 15);
    }

    @Test
    public void findSuggestedAppointments() {
        Workshop workshop = new Workshop();
        workshop.setOfficeHoursStart("08:00");
        workshop.setOfficeHoursEnd("18:00");
        OfferedService offeredService = new OfferedService();
        offeredService.setDuration(30);
        offeredService.setServiceType(new ServiceType("FIX", ""));
        workshop.setOfferedServices(List.of(offeredService));

        Map<LocalDate, List<CapacitySlot>> capacitySlotMap = new HashMap<>();
        List<CapacitySlot> capacitySlots = new ArrayList<>();
        LocalDateTime von = LocalDateTime.now().withHour(10).withMinute(0);
        LocalTime startTime = LocalTime.of(von.getHour() - 1, 0, 0);
        IntStream.range(0, 8).forEach(i -> capacitySlots.add(CapacitySlot.builder().capacity(2).start(startTime.plusMinutes(Long.valueOf(i * 15L).intValue()))
                .build()));

        capacitySlotMap.put(LocalDate.now(), capacitySlots);

        when(appointmentUtil.determineCapacity(any(LocalDateTime.class), any(LocalDateTime.class), any(Workshop.class))).thenReturn(capacitySlotMap);
        when(appointmentUtil.determineFreeCapacities(Mockito.anyList())).thenCallRealMethod();

        List<Appointment> suggestedAppointments = appointmentSuggestionService.findSuggestedAppointments(workshop, LocalDateTime.now(), LocalDateTime.now(), "FIX");

        assertEquals(4, suggestedAppointments.size());
    }
}