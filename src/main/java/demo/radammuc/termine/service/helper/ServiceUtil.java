package demo.radammuc.termine.service.helper;

import demo.radammuc.termine.model.OfferedService;
import demo.radammuc.termine.model.Workshop;

import java.util.Optional;

public class ServiceUtil {

    public static OfferedService getService(Workshop workshop, String serviceCode) {
        Optional<OfferedService> serviceOptional = workshop.getOfferedServices().stream().filter(os -> os.getServiceType().getCode().equals(serviceCode)).findAny();

        return serviceOptional.orElseThrow(() -> new RuntimeException("No service " + serviceCode + " for " + workshop.getName()));
    }
}
