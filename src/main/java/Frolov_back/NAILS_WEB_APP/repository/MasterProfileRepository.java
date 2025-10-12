package Frolov_back.NAILS_WEB_APP.repository;


import Frolov_back.NAILS_WEB_APP.domain.MasterProfile;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MasterProfileRepository extends BaseRepository<MasterProfile, Long> {

    // Найти по ID пользователя
    Optional<MasterProfile> findByUserId(Long userId);

    // Найти активных мастеров
    List<MasterProfile> findByIsActiveTrue();

    // Найти по специализации
    List<MasterProfile> findBySpecializationContainingIgnoreCase(String specialization);

    // Найти мастеров с опытом больше указанного
    List<MasterProfile> findByWorkExperienceGreaterThanEqual(Integer years);

    // Найти мастеров по рейтингу (через JOIN)
    @Query("SELECT mp FROM MasterProfile mp JOIN SystemUser u ON mp.userId = u.userId " +
            "JOIN Review r ON r.master.userId = u.userId " +
            "GROUP BY mp.userId HAVING AVG(r.rating) >= :minRating")
    List<MasterProfile> findMastersWithRatingAbove(@Param("minRating") Double minRating);

    // Найти мастеров с фото
    List<MasterProfile> findByPhotoUrlIsNotNull();
}