package com.github.bgrebennikov.recommendationservice.controller;

import com.github.bgrebennikov.recommendationservice.data.dto.rule.RuleCreateRequest;
import com.github.bgrebennikov.recommendationservice.data.dto.rule.RuleResponseDto;
import com.github.bgrebennikov.recommendationservice.data.persistence.RuleEntity;
import com.github.bgrebennikov.recommendationservice.service.RuleService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST-контроллер для управления правилами рекомендаций.
 * <p>
 * Предоставляет HTTP-эндпоинты для создания, получения и удаления динамических правил.
 *
 * @author Ekaterina, Boris
 * @version 1.0
 */
@RestController
@RequestMapping("/rule")
public class RuleController {

    private final RuleService ruleService;

    public RuleController(RuleService ruleService) {
        this.ruleService = ruleService;
    }

    /**
     * Создает новое правило рекомендаций.
     *
     * @param request DTO с параметрами создаваемого правила и его условиями
     * @return Созданная сущность {@link RuleEntity} со статусом 200 OK
     */
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public RuleEntity createRule(@RequestBody RuleCreateRequest request) {
        return ruleService.createRule(request);
    }

    /**
     * Возвращает полный список всех зарегистрированных правил.
     *
     * @return Список сущностей {@link RuleEntity} со статусом 200 OK
     */
    @GetMapping
    public RuleResponseDto getAllRules() {
        return ruleService.findAll();
    }

    /**
     * Удаляет правило по его уникальному идентификатору.
     *
     * @param id Уникальный идентификатор правила (UUID)
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRule(@PathVariable UUID id) {
        ruleService.deleteRule(id);
    }
}