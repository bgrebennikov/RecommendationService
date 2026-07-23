package com.github.bgrebennikov.recommendationservice.rule.statics;
import com.github.bgrebennikov.recommendationservice.data.RecommendationItem;
import com.github.bgrebennikov.recommendationservice.data.types.ProductType;
import com.github.bgrebennikov.recommendationservice.repository.RecommendationRepository;
import com.github.bgrebennikov.recommendationservice.rule.RecommendationRuleSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

/**
 * Правило рекомендации кредитного продукта «Простой кредит».
 * <p>
 * Продукт предлагается пользователю при выполнении следующих условий:
 * <ul>
 *   <li>У пользователя отсутствует открытый кредитный продукт ({@link ProductType#CREDIT})</li>
 *   <li>Сумма списаний по дебетовым счетам ({@link ProductType#DEBIT}) строго больше 100 000 ₽</li>
 *   <li>Сумма пополнений по дебетовым счетам превышает сумму списаний</li>
 * </ul>
 *
 * @author Ekaterina
 * @version 1.0
 */
@Component
public class SimpleCreditRuleSet implements RecommendationRuleSet {

    private static final UUID CREDIT_ID = UUID.fromString("ab138afb-f3ba-4a93-b74f-0fcee86d447f");

    private static final String NAME = "Простой кредит";
    private static final String TEXT = "Откройте для себя мир выгодных кредитов вместе с нами!\n\n" +
            "Ищете способ быстро и без лишних хлопот получить нужную сумму? Тогда наш выгодный кредит — именно то, что вам нужно! " +
            "Мы предлагаем низкие процентные ставки, гибкие условия и индивидуальный подход к каждому клиенту.\n\n" +
            "Почему выбирают нас:\n\n" +
            "Быстрое рассмотрение заявки. Мы ценим ваше время, поэтому процесс рассмотрения заявки занимает всего несколько часов.\n\n" +
            "Удобное оформление. Подать заявку на кредит можно онлайн на нашем сайте или в мобильном приложении.\n\n" +
            "Широкий выбор кредитных продуктов. Мы предлагаем кредиты на различные цели: покупку недвижимости, автомобиля, образование, лечение и многое другое.\n\n" +
            "Не упустите возможность воспользоваться выгодными условиями кредитования от нашей компании!";

    private final RecommendationRepository repository;

    public SimpleCreditRuleSet(RecommendationRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<RecommendationItem> evaluate(UUID userId) {
        boolean hasCredit = repository.hasProductType(userId, ProductType.CREDIT);
        if (hasCredit) {
            return Optional.empty();
        }

        BigDecimal debitDeposits = repository.sumOfDepositsByType(userId, ProductType.DEBIT);
        BigDecimal debitWithdrawals = repository.sumOfWithdrawalsByType(userId, ProductType.DEBIT);

        boolean isWithdrawalsOverLimit = debitWithdrawals != null &&
                debitWithdrawals.compareTo(BigDecimal.valueOf(100000)) > 0;

        boolean isDepositsGreater = debitDeposits != null && debitWithdrawals != null &&
                debitDeposits.compareTo(debitWithdrawals) > 0;

        if (isWithdrawalsOverLimit && isDepositsGreater) {
            return Optional.of(new RecommendationItem(
                    CREDIT_ID,
                    NAME,
                    TEXT
            ));
        }

        return Optional.empty();
    }
}