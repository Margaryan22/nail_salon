package Frolov_back.NAILS_WEB_APP.repository;

import Frolov_back.NAILS_WEB_APP.domain.Review;
import Frolov_back.NAILS_WEB_APP.domain.SystemUser;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends BaseRepository<Review, Long> {

    // Найти отзыв по записи
    Optional<Review> findByAppointment_AppointmentId(Long appointmentId);

    // Найти отзывы мастера
    List<Review> findByMaster(SystemUser master);

    // Найти отзывы клиента
    List<Review> findByClient(SystemUser client);

    // Найти отзывы с рейтингом выше указанного
    List<Review> findByRatingGreaterThanEqual(Short minRating);

    // Найти отзывы мастера с сортировкой по дате
    List<Review> findByMasterOrderByCreatedAtDesc(SystemUser master);

    // Посчитать средний рейтинг мастера
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.master = :master")
    Optional<Double> findAverageRatingByMaster(@Param("master") SystemUser master);

    // Найти отзывы с комментариями
    List<Review> findByCommentIsNotNull();

    // Статистика рейтингов мастера
    @Query("SELECT r.rating, COUNT(r) FROM Review r WHERE r.master = :master GROUP BY r.rating ORDER BY r.rating")
    List<Object[]> getRatingStatistics(@Param("master") SystemUser master);
}