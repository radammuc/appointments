package demo.radammuc.termine.service;

import demo.radammuc.termine.model.Appointment;
import demo.radammuc.termine.model.OfferedService;
import demo.radammuc.termine.model.ServiceType;
import demo.radammuc.termine.model.Workshop;
import demo.radammuc.termine.repository.AppointmentRepository;
import demo.radammuc.termine.repository.ServiceTypeRepository;
import demo.radammuc.termine.repository.WorkshopRepository;
import demo.radammuc.termine.service.helper.ServiceUtil;
import lombok.extern.jbosslog.JBossLog;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Component
@JBossLog
class AppointmentImporter {

    public static final String CSV_EXTENSION = ".csv";

    final WorkshopRepository workshopRepository;

    final AppointmentRepository appointmentRepository;

    final ServiceTypeRepository serviceTypeRepository;

    final String importDirectory;

    @Value("${appointment.import.enabled}")
    boolean importEnabled;

    public AppointmentImporter(WorkshopRepository workshopRepository, AppointmentRepository appointmentRepository, ServiceTypeRepository serviceTypeRepository,
                               @Value("${appointment.import.directory}") String importDirectory) {
        this.workshopRepository = workshopRepository;
        this.appointmentRepository = appointmentRepository;
        this.serviceTypeRepository = serviceTypeRepository;
        this.importDirectory = importDirectory;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    void init() throws IOException {
        if (importEnabled) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(importDirectory), p -> p.toFile().isFile() && p.toString().endsWith(CSV_EXTENSION))) {
                for (Path path : stream) {
                    String filename = path.toFile().getName();
                    String workshopName = filename.substring(0, filename.indexOf(CSV_EXTENSION));
                    log.infov("found workshopName {0} for path {1}", workshopName, path.toAbsolutePath());

                    List<Workshop> workshops = workshopRepository.findByNameIgnoreCase(workshopName);

                    if (!workshops.isEmpty()) {
                        importAppointments(workshops.getFirst(), path);
                    }
                }
            }
        } else {
            log.info("Import of appointments is not enabled");
        }
    }

    private void importAppointments(Workshop ws, Path path) throws IOException {
        List<String> lines = Files.readAllLines(path);

        if (ws.getAppointments() != null && !ws.getAppointments().isEmpty()) {
            log.infov("Appointments for workshop {0} seem to be already imported", ws.getName());
            return;
        }

        for (String s : lines) {
            if (s.startsWith("APP")) {
                continue;
            }

            String[] line = s.split(",");

            if (!serviceIsOffered(ws, line[1])) {
                continue;
            }

            Optional<ServiceType> serviceTypeOptional = serviceTypeRepository.findById(line[1]);
            ServiceType serviceType = serviceTypeOptional.orElseThrow();

            LocalDateTime von = LocalDateTime.parse(line[0], DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mmXXX"));
            LocalDateTime bis = getBis(von, serviceType.getCode(), ws);

            Appointment appointment = new Appointment();
            appointment.setServiceType(serviceType);
            appointment.setWorkshop(ws);
            appointment.setVon(von);
            appointment.setBis(bis);

            Appointment saved = appointmentRepository.save(appointment);

            ws.getAppointments().add(saved);
        }
    }

    private boolean serviceIsOffered(Workshop workshop, String serviceCode) {
        return workshop.getOfferedServices().stream().anyMatch(s -> s.getServiceType().getCode().equals(serviceCode));
    }

    private LocalDateTime getBis(LocalDateTime von, String serviceCode, Workshop workshop) {
        OfferedService service = ServiceUtil.getService(workshop, serviceCode);
        // returns a new object
        return von.plusMinutes(service.getDuration());
    }
}
