package com.readytoplanbe.app.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BusinessPlanMapperTest {

    private BusinessPlanMapper businessPlanMapper;

    @BeforeEach
    public void setUp() {
        businessPlanMapper = new BusinessPlanMapperImpl();
    }
}
