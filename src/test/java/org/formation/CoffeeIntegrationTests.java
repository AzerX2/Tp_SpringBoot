package org.formation;

import org.formation.catalog.Catalog;
import org.formation.catalog.internal.CoffeeEntity;
import org.formation.catalog.internal.CoffeeRepository;
import org.formation.inventory.internal.StockEntity;
import org.formation.inventory.internal.StockRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext
class CoffeeIntegrationTests {

    @Autowired
    Catalog catalog;

    @Autowired
    CoffeeRepository coffeeRepository;

    @Autowired
    StockRepository stockRepository;

    @Test
    void shouldCreateCoffeeAndTriggerStockCreationViaEvent() {
        // 1. Création d'un café via le service Catalog
        Long coffeeId = catalog.createCoffee("Cappuccino", 12);
        assertThat(coffeeId).isNotNull().isPositive();

        // 2. Vérification que l'entité CoffeeEntity est bien créée dans catalog
        CoffeeEntity coffee = coffeeRepository.findById(coffeeId).orElseThrow();
        assertThat(coffee.getName()).isEqualTo("Cappuccino");
        assertThat(coffee.getPrice()).isEqualTo(12);

        // 3. Vérification que l'entité StockEntity a bien été créée par l'événement dans inventory
        List<StockEntity> stocks = stockRepository.findAll();
        assertThat(stocks).isNotEmpty();
        StockEntity stock = stocks.stream()
                .filter(s -> coffeeId.equals(s.getCoffeeId()))
                .findFirst()
                .orElseThrow();
        assertThat(stock.getQuantity()).isEqualTo(0);
    }
}
