package demo.radammuc.termine.service;

import demo.radammuc.termine.dto.Termin;
import demo.radammuc.termine.dto.TerminRequest;
import demo.radammuc.termine.exception.NotFoundException;
import demo.radammuc.termine.mapper.AppointmentMapper;
import demo.radammuc.termine.model.Appointment;
import demo.radammuc.termine.model.ServiceType;
import demo.radammuc.termine.model.Workshop;
import demo.radammuc.termine.repository.AppointmentRepository;
import demo.radammuc.termine.repository.ServiceTypeRepository;
import demo.radammuc.termine.repository.WorkshopRepository;
import demo.radammuc.termine.service.helper.DateUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class WorkshopService {

    final AppointmentRepository appointmentRepository;

    final WorkshopRepository workshopRepository;

    final ServiceTypeRepository serviceTypeRepository;

    final AppointmentMapper appointmentMapper;

    public WorkshopService(AppointmentRepository appointmentRepository, WorkshopRepository workshopRepository, ServiceTypeRepository serviceTypeRepository, AppointmentMapper appointmentMapper) {
        this.appointmentRepository = appointmentRepository;
        this.workshopRepository = workshopRepository;
        this.serviceTypeRepository = serviceTypeRepository;
        this.appointmentMapper = appointmentMapper;
    }

    @Transactional(readOnly = true)
    public Termin getTermin(String werkstattId, String terminId) {

        Optional<Appointment> appointmentOptional = appointmentRepository.findById(Integer.valueOf(terminId));
        Appointment appointment = appointmentOptional.orElseThrow(() -> new NotFoundException(""));

        if (!appointment.getWorkshop().getId().equals(Integer.valueOf(werkstattId))) {
            throw new RuntimeException("werkstattId and terminId don't match");
        }

        return appointmentMapper.map(appointment);
    }

    @Transactional(readOnly = true)
    public List<Termin> getTermine(String werkstattId, String von, String bis, String leistungsId) {

        LocalDateTime from = DateUtil.fromString(von);
        LocalDateTime to = DateUtil.fromString(bis);

        // TODO validate if to > from

        List<Appointment> appointments = workshopRepository.findAppointments(Integer.valueOf(werkstattId), from, to, leistungsId);
        return appointmentMapper.map(appointments);
    }

    @Transactional
    public Termin postTermin(String werkstattId, TerminRequest termin) {

        Optional<Workshop> workshopOptional = workshopRepository.findById(Integer.valueOf(werkstattId));
        Workshop workshop = workshopOptional.orElseThrow(() -> new NotFoundException(""));

        // TODO validate if workshop offers the requested service -> 400

        LocalDateTime from = DateUtil.fromString(termin.getVon());
        LocalDateTime to = DateUtil.fromString(termin.getBis());

        // TODO validate if to = from + duration -> 400

        Optional<ServiceType> serviceTypeOptional = serviceTypeRepository.findById(termin.getLeistungsId());
        ServiceType serviceType = serviceTypeOptional.orElseThrow();

        Appointment appointment = new Appointment();
        appointment.setWorkshop(workshop);
        appointment.setServiceType(serviceType);
        appointment.setVon(from);
        appointment.setBis(to);

        Appointment saved = appointmentRepository.save(appointment);

        return appointmentMapper.map(saved);
    }

    @Transactional
    public boolean lockWorkshop(Integer workshopId) {
        Optional<Workshop> workshopOptional = workshopRepository.findById(workshopId);
        Workshop workshop = workshopOptional.orElseThrow(() -> new NotFoundException(""));

        if (workshop.isLocked()) {
            return false;
        }

        workshop.setLocked(true);

        // TODO do we need to flush the hibernate context ?

        return true;
    }

    @Transactional
    public void unlockWorkshop(Integer workshopId) {
        Optional<Workshop> workshopOptional = workshopRepository.findById(workshopId);
        Workshop workshop = workshopOptional.orElseThrow(() -> new NotFoundException(""));

        workshop.setLocked(false);
    }


}

