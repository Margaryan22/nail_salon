package Frolov_back.NAILS_WEB_APP.service.DTO;

public class UserUpdateRequestDto {
    private String firstName;
    private String lastName;
    private String phone;

    // Геттеры и сеттеры
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
