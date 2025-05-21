package com.readytoplanbe.app.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FinancialForecastMapperTest {

    private FinancialForecastMapper financialForecastMapper;

    @BeforeEach
    public void setUp() {
        financialForecastMapper = new FinancialForecastMapperImpl();
    }
}
