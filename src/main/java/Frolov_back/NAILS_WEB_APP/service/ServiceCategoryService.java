package Frolov_back.NAILS_WEB_APP.service;

import Frolov_back.NAILS_WEB_APP.DTO.ServiceCategoryDto;

import java.util.List;
import java.util.Optional;

public interface ServiceCategoryService {

    // Создать категорию
    ServiceCategoryDto createCategory(ServiceCategoryDto categoryDto);

    // Получить все категории
    List<ServiceCategoryDto> getAllCategories();

    // Получить категорию по ID
    Optional<ServiceCategoryDto> getCategoryById(Long categoryId);

    // Получить категорию по имени
    Optional<ServiceCategoryDto> getCategoryByName(String name);

    // Обновить категорию
    Optional<ServiceCategoryDto> updateCategory(Long categoryId, ServiceCategoryDto categoryDto);

    // Удалить категорию
    void deleteCategory(Long categoryId);

    // Получить категории с активными услугами
    List<ServiceCategoryDto> getCategoriesWithActiveServices();
}
