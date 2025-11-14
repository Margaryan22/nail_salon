package Frolov_back.NAILS_WEB_APP.controller;

import Frolov_back.NAILS_WEB_APP.DTO.ServiceCategoryDto;
import Frolov_back.NAILS_WEB_APP.service.ServiceCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/service-categories")
@RequiredArgsConstructor
@Tag(
        name = "📁 Управление категориями услуг",
        description = "API для работы с категориями услуг салона"
)
public class ServiceCategoryController {

    private final ServiceCategoryService categoryService;

    @Operation(
            summary = "➕ Создать новую категорию",
            description = "Создает новую категорию для группировки услуг"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "✅ Категория успешно создана"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "❌ Ошибка валидации данных"
            )
    })
    @PostMapping
    public ResponseEntity<?> createCategory(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные для создания категории",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = ServiceCategoryDto.class),
                            examples = @ExampleObject(
                                    value = """
                        {
                            "name": "Маникюр",
                            "description": "Услуги по уходу за ногтями",
                            "sortOrder": 1
                        }
                        """
                            )
                    )
            )
            @Valid @RequestBody ServiceCategoryDto categoryDto) {

        try {
            ServiceCategoryDto createdCategory = categoryService.createCategory(categoryDto);
            return ResponseEntity.ok(createdCategory);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    @Operation(
            summary = "📋 Получить все категории",
            description = "Возвращает список всех категорий услуг с сортировкой"
    )
    @GetMapping
    public ResponseEntity<List<ServiceCategoryDto>> getAllCategories() {
        List<ServiceCategoryDto> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @Operation(
            summary = "🔍 Получить категорию по ID",
            description = "Возвращает категорию по её идентификатору"
    )
    @GetMapping("/{categoryId}")
    public ResponseEntity<?> getCategoryById(
            @Parameter(description = "ID категории", example = "1", required = true)
            @PathVariable Long categoryId) {

        return categoryService.getCategoryById(categoryId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "🔄 Обновить категорию",
            description = "Обновляет данные категории услуг"
    )
    @PutMapping("/{categoryId}")
    public ResponseEntity<?> updateCategory(
            @PathVariable Long categoryId,
            @Valid @RequestBody ServiceCategoryDto categoryDto) {

        try {
            return categoryService.updateCategory(categoryId, categoryDto)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    @Operation(
            summary = "🗑️ Удалить категорию",
            description = "Удаляет категорию услуг (только если нет связанных услуг)"
    )
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long categoryId) {
        try {
            categoryService.deleteCategory(categoryId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    @Operation(
            summary = "🎯 Получить категории с активными услугами",
            description = "Возвращает только те категории, в которых есть активные услуги"
    )
    @GetMapping("/with-active-services")
    public ResponseEntity<List<ServiceCategoryDto>> getCategoriesWithActiveServices() {
        List<ServiceCategoryDto> categories = categoryService.getCategoriesWithActiveServices();
        return ResponseEntity.ok(categories);
    }
}
