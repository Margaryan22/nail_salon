package Frolov_back.NAILS_WEB_APP.service.impl;

import Frolov_back.NAILS_WEB_APP.domain.SystemUser;
import Frolov_back.NAILS_WEB_APP.repository.SystemUserRepository;
import Frolov_back.NAILS_WEB_APP.security.JwtService;
import Frolov_back.NAILS_WEB_APP.service.DTO.JwtResponse;
import Frolov_back.NAILS_WEB_APP.service.DTO.LoginRequest;
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

    public JwtResponse authenticate(LoginRequest request) {
        // Аутентификация через Spring Security
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Пользователь аутентифицирован, генерируем токен
        SystemUser user = systemUserRepository.findByEmail(request.getEmail())
                .orElseThrow();

        UserDetails userDetails = (UserDetails) user;
        String jwtToken = jwtService.generateToken(userDetails);

        return new JwtResponse(
                jwtToken,
                user.getUserId(),
                user.getEmail(),
                user.getRole().name()
        );
    }
}