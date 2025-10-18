package Frolov_back.NAILS_WEB_APP.service.DTO;

public class JwtResponse {
    private String accessToken;
    private String refreshToken; // ДОБАВЛЯЕМ
    private String tokenType = "Bearer";
    private Long userId;
    private String email;
    private String role;

    public JwtResponse(String accessToken, String refreshToken, Long userId, String email, String role) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.userId = userId;
        this.email = email;
        this.role = role;
    }

    // Геттеры
    public String getAccessToken() { return accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public String getTokenType() { return tokenType; }
    public Long getUserId() { return userId; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
}