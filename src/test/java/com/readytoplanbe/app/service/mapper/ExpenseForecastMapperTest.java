package com.readytoplanbe.app.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ExpenseForecastMapperTest {

    private ExpenseForecastMapper expenseForecastMapper;

    @BeforeEach
    public void setUp() {
        expenseForecastMapper = new ExpenseForecastMapperImpl();
    }
}
