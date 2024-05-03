package demo.radammuc.termine.service;

import demo.radammuc.termine.service.helper.DateUtil;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DateUtilTest {

    final static int SLOT_LENGTH = 15;

    @Test
    void getSlotStart_0() {
        LocalDateTime dateTime = LocalDateTime.now().withMinute(0);
        LocalDateTime slotStart = DateUtil.getSlotStart(dateTime, SLOT_LENGTH);

        assertEquals(0, slotStart.getMinute());
    }

    @Test
    void getSlotStart_15() {
        LocalDateTime dateTime = LocalDateTime.now().withMinute(15);
        LocalDateTime slotStart = DateUtil.getSlotStart(dateTime, SLOT_LENGTH);

        assertEquals(15, slotStart.getMinute());
    }

    @Test
    void getSlotStart_56() {
        LocalDateTime dateTime = LocalDateTime.now().withMinute(56);
        LocalDateTime slotStart = DateUtil.getSlotStart(dateTime, SLOT_LENGTH);

        assertEquals(45, slotStart.getMinute());
    }

}
