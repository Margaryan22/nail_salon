package Frolov_back.NAILS_WEB_APP.domain.for_shedule_master;

public enum DayOfWeekEnum {
    MONDAY(1, "Понедельник"),
    TUESDAY(2, "Вторник"),
    WEDNESDAY(3, "Среда"),
    THURSDAY(4, "Четверг"),
    FRIDAY(5, "Пятница"),
    SATURDAY(6, "Суббота"),
    SUNDAY(7, "Воскресенье");

    private final int dayNumber;
    private final String russianName;

    DayOfWeekEnum(int dayNumber, String russianName) {
        this.dayNumber = dayNumber;
        this.russianName = russianName;
    }

    public int getDayNumber() {
        return dayNumber;
    }

    public String getRussianName() {
        return russianName;
    }

    public static DayOfWeekEnum fromNumber(int dayNumber) {
        for (DayOfWeekEnum day : values()) {
            if (day.getDayNumber() == dayNumber) {
                return day;
            }
        }
        throw new IllegalArgumentException("Неверный номер дня: " + dayNumber);
    }
}