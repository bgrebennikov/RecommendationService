package com.github.bgrebennikov.recommendationservice.controller;

import com.github.bgrebennikov.recommendationservice.data.dto.rule.RuleCreateRequest;
import com.github.bgrebennikov.recommendationservice.data.dto.rule.RuleItemDto;
import com.github.bgrebennikov.recommendationservice.data.dto.rule.RuleResponseListDto;
import com.github.bgrebennikov.recommendationservice.service.RuleService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST-контроллер для управления динамическими правилами рекомендаций.
 * <p>
 * Предоставляет HTTP-эндпоинты для создания, получения и удаления правил.
 *
 * @author Ekaterina, Boris
 * @version 1.1
 */
@RestController
@RequestMapping("/rule")
public class RuleController {

    private final RuleService ruleService;

    public RuleController(RuleService ruleService) {
        this.ruleService = ruleService;
    }

    /**
     * Создает новое правило рекомендаций вместе с его условиями.
     *
     * @param request DTO с параметрами создаваемого правила и списком условий
     * @return DTO созданного правила {@link RuleItemDto} со статусом 200 OK
     */
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public RuleItemDto createRule(@RequestBody RuleCreateRequest request) {
        return ruleService.createRule(request);
    }

    /**
     * Возвращает полный список всех зарегистрированных правил.
     *
     * @return DTO-контейнер {@link RuleResponseListDto} со списком правил и статусом 200 OK
     */
    @GetMapping
    public RuleResponseListDto getAllRules() {
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