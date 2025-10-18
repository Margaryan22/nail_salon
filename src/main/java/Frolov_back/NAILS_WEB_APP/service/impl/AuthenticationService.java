package Frolov_back.NAILS_WEB_APP.service.impl;

import Frolov_back.NAILS_WEB_APP.domain.RefreshToken;
import Frolov_back.NAILS_WEB_APP.domain.SystemUser;
import Frolov_back.NAILS_WEB_APP.repository.SystemUserRepository;
import Frolov_back.NAILS_WEB_APP.security.JwtService;
import Frolov_back.NAILS_WEB_APP.service.DTO.JwtResponse;
import Frolov_back.NAILS_WEB_APP.service.DTO.LoginRequest;
import Frolov_back.NAILS_WEB_APP.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final SystemUserRepository systemUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService; // ДОБАВЛЯЕМ

    public JwtResponse authenticate(LoginRequest request) {
        // Аутентификация
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Получаем пользователя
        SystemUser user = systemUserRepository.findByEmail(request.getEmail())
                .orElseThrow();

        // Генерируем access token
        String accessToken = jwtService.generateToken((UserDetails) user);

        // Генерируем refresh token
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return new JwtResponse(
                accessToken,
                refreshToken.getToken(), // ДОБАВЛЯЕМ refresh token
                user.getUserId(),
                user.getEmail(),
                user.getRole().name()
        );
    }

    // Метод для обновления токена
    public JwtResponse refreshToken(String refreshToken) {
        RefreshToken token = refreshTokenService.findByToken(refreshToken)
                .map(refreshTokenService::verifyExpiration)
                .orElseThrow(() -> new RuntimeException("Refresh token не найден"));

        SystemUser user = token.getUser();

        String newAccessToken = jwtService.generateToken((UserDetails) user);
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user);

        return new JwtResponse(
                newAccessToken,
                newRefreshToken.getToken(),
                user.getUserId(),
                user.getEmail(),
                user.getRole().name()
        );
    }
}