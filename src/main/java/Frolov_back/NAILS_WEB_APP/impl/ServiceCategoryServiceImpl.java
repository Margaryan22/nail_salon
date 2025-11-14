package Frolov_back.NAILS_WEB_APP.impl;

import Frolov_back.NAILS_WEB_APP.DTO.ServiceCategoryDto;
import Frolov_back.NAILS_WEB_APP.domain.ServiceCategory;
import Frolov_back.NAILS_WEB_APP.repository.ServiceCategoryRepository;
import Frolov_back.NAILS_WEB_APP.service.ServiceCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServiceCategoryServiceImpl implements ServiceCategoryService {

    private final ServiceCategoryRepository categoryRepository;

    @Override
    @Transactional
    public ServiceCategoryDto createCategory(ServiceCategoryDto categoryDto) {
        // Проверяем уникальность имени
        if (categoryRepository.existsByName(categoryDto.getName())) {
            throw new RuntimeException("Категория с таким названием уже существует");
        }

        ServiceCategory category = new ServiceCategory();
        category.setName(categoryDto.getName());
        category.setDescription(categoryDto.getDescription());
        category.setSortOrder(categoryDto.getSortOrder() != null ? categoryDto.getSortOrder() : 0);

        ServiceCategory savedCategory = categoryRepository.save(category);
        return convertToDto(savedCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceCategoryDto> getAllCategories() {
        return categoryRepository.findAllByOrderBySortOrderAsc().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ServiceCategoryDto> getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .map(this::convertToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ServiceCategoryDto> getCategoryByName(String name) {
        return categoryRepository.findByName(name)
                .map(this::convertToDto);
    }

    @Override
    @Transactional
    public Optional<ServiceCategoryDto> updateCategory(Long categoryId, ServiceCategoryDto categoryDto) {
        return categoryRepository.findById(categoryId)
                .map(category -> {
                    // Проверяем уникальность имени (кроме текущей категории)
                    if (categoryRepository.existsByNameAndCategoryIdNot(categoryDto.getName(), categoryId)) {
                        throw new RuntimeException("Категория с таким названием уже существует");
                    }

                    category.setName(categoryDto.getName());
                    category.setDescription(categoryDto.getDescription());
                    if (categoryDto.getSortOrder() != null) {
                        category.setSortOrder(categoryDto.getSortOrder());
                    }

                    ServiceCategory updatedCategory = categoryRepository.save(category);
                    return convertToDto(updatedCategory);
                });
    }

    @Override
    @Transactional
    public void deleteCategory(Long categoryId) {
        ServiceCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Категория не найдена"));

        // Проверяем нет ли связанных услуг
        if (!category.getServices().isEmpty()) {
            throw new RuntimeException("Невозможно удалить категорию: есть связанные услуги");
        }

        categoryRepository.delete(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceCategoryDto> getCategoriesWithActiveServices() {
        return categoryRepository.findCategoriesWithActiveServices().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private ServiceCategoryDto convertToDto(ServiceCategory category) {
        ServiceCategoryDto dto = new ServiceCategoryDto();
        dto.setCategoryId(category.getCategoryId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setSortOrder(category.getSortOrder());
        return dto;
    }
}