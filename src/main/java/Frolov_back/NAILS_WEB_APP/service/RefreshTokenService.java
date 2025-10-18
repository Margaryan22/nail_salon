package Frolov_back.NAILS_WEB_APP.service;

import Frolov_back.NAILS_WEB_APP.domain.RefreshToken;
import Frolov_back.NAILS_WEB_APP.domain.SystemUser;
import Frolov_back.NAILS_WEB_APP.repository.RefreshTokenRepository;
import Frolov_back.NAILS_WEB_APP.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;

    @Value("${jwt.refresh-expiration-time:604800000}") // 7 дней по умолчанию
    private long refreshExpiration;

    public RefreshToken createRefreshToken(SystemUser user) {
        RefreshToken refreshToken = refreshTokenRepository.findByUser(user)
                .map(existingToken -> {
                    existingToken.setToken(generateToken());
                    existingToken.setExpiryDate(calculateExpiryDate());
                    return existingToken;
                })
                .orElse(new RefreshToken(
                        user,
                        generateToken(),
                        calculateExpiryDate()
                ));

        return refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token был просрочен. Пожалуйста, войдите снова.");
        }
        return token;
    }

    @Transactional
    public void deleteByUserId(Long userId) {
        // Реализация удаления по userId
    }

    private String generateToken() {
        return UUID.randomUUID().toString();
    }

    private LocalDateTime calculateExpiryDate() {
        return LocalDateTime.now().plusSeconds(refreshExpiration / 1000);
    }
}