package demo.radammuc.termine.api;

import demo.radammuc.termine.dto.Termin;
import demo.radammuc.termine.dto.TerminRequest;
import demo.radammuc.termine.service.AppointmentSuggestionService;
import demo.radammuc.termine.service.WorkshopService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class WorkshopController implements WerkstattApi {


    final WorkshopService workshopService;

    final AppointmentSuggestionService appointmentSuggestionService;

    public WorkshopController(WorkshopService service, AppointmentSuggestionService appointmentSuggestionService) {
        this.workshopService = service;
        this.appointmentSuggestionService = appointmentSuggestionService;
    }

    public ResponseEntity<Termin> getTermin(String werkstattId, String terminId) {
        return ResponseEntity.ok(workshopService.getTermin(werkstattId, terminId));
    }

    public ResponseEntity<List<Termin>> getTermine(String werkstattId, String von, String bis, String leistungsId) {
        return ResponseEntity.ok(workshopService.getTermine(werkstattId, von, bis, leistungsId));
    }

    public ResponseEntity<List<Termin>> getTerminvorschlaege(String werkstattId, String leistungsId, String von, String bis) {
        return ResponseEntity.ok(appointmentSuggestionService.getTerminvorschlaege(werkstattId, leistungsId, von, bis));
    }

    public ResponseEntity<Termin> postTermin(String werkstattId, TerminRequest terminRequest) {
        Integer workshopId = Integer.valueOf(werkstattId);

        boolean lockSuccessful = workshopService.lockWorkshop(workshopId);

        if (!lockSuccessful) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        try {
            return ResponseEntity.ok(workshopService.postTermin(werkstattId, terminRequest));
        } finally {
            workshopService.unlockWorkshop(workshopId);
        }
    }
}
