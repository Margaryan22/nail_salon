package Frolov_back.NAILS_WEB_APP.domain.for_shedule_master;

import java.time.LocalDate;
import java.time.LocalDateTime;

public enum TimeSlotEnum {
    SLOT_9(1, "09:00", "10:00"),
    SLOT_10(2, "10:00", "11:00"),
    SLOT_11(3, "11:00", "12:00"),
    SLOT_12(4, "12:00", "13:00"),
    SLOT_13(5, "13:00", "14:00"),
    SLOT_14(6, "14:00", "15:00"),
    SLOT_15(7, "15:00", "16:00"),
    SLOT_16(8, "16:00", "17:00"),
    SLOT_17(9, "17:00", "18:00"),
    SLOT_18(10, "18:00", "19:00");

    private final int slotNumber;
    private final String startTime;
    private final String endTime;

    TimeSlotEnum(int slotNumber, String startTime, String endTime) {
        this.slotNumber = slotNumber;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int getSlotNumber() {
        return slotNumber;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public static TimeSlotEnum fromNumber(int slotNumber) {
        for (TimeSlotEnum slot : values()) {
            if (slot.getSlotNumber() == slotNumber) {
                return slot;
            }
        }
        throw new IllegalArgumentException("Неверный номер слота: " + slotNumber);
    }

    public static LocalDateTime toStartDateTime(LocalDate date, int slotNumber) {
        TimeSlotEnum slot = fromNumber(slotNumber);
        return date.atTime(java.time.LocalTime.parse(slot.getStartTime()));
    }

    public static LocalDateTime toEndDateTime(LocalDate date, int slotNumber) {
        TimeSlotEnum slot = fromNumber(slotNumber);
        return date.atTime(java.time.LocalTime.parse(slot.getEndTime()));
    }
}