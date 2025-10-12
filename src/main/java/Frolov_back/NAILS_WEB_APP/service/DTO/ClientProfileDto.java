package Frolov_back.NAILS_WEB_APP.service.DTO;

import java.time.LocalDate;


public class ClientProfileDto {
    private Long userId;
    private LocalDate birthdate;
    private Integer bonusPoints;
    private String notes;

    // Геттеры и сеттеры
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public LocalDate getBirthdate() { return birthdate; }
    public void setBirthdate(LocalDate birthdate) { this.birthdate = birthdate; }
    public Integer getBonusPoints() { return bonusPoints; }
    public void setBonusPoints(Integer bonusPoints) { this.bonusPoints = bonusPoints; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}