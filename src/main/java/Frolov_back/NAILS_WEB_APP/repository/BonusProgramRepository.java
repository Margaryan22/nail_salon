package Frolov_back.NAILS_WEB_APP.repository;

import Frolov_back.NAILS_WEB_APP.domain.BonusProgram;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BonusProgramRepository extends BaseRepository<BonusProgram, Long> {

    // Найти активные бонусные программы
    List<BonusProgram> findByIsActiveTrue();

    // Найти по имени
    Optional<BonusProgram> findByName(String name);

    // Найти программы с минимальной суммой
    List<BonusProgram> findByMinAmountLessThanEqualOrderByPointsPerAmountDesc(java.math.BigDecimal amount);
}