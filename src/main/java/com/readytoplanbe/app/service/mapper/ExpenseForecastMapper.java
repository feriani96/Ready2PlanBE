package com.readytoplanbe.app.service.mapper;

import com.readytoplanbe.app.domain.ExpenseForecast;
import com.readytoplanbe.app.domain.FinancialForecast;
import com.readytoplanbe.app.service.dto.ExpenseForecastDTO;
import com.readytoplanbe.app.service.dto.FinancialForecastDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ExpenseForecast} and its DTO {@link ExpenseForecastDTO}.
 */
@Mapper(componentModel = "spring" )
public interface ExpenseForecastMapper extends EntityMapper<ExpenseForecastDTO, ExpenseForecast> {
    @Mapping(source = "forecast", target = "forecast")
    ExpenseForecastDTO toDto(ExpenseForecast expenseForecast);

    @Mapping(source = "forecast", target = "forecast")
    ExpenseForecast toEntity(ExpenseForecastDTO expenseForecastDTO);

    default ExpenseForecast fromId(String id) {
        if (id == null) {
            return null;
        }
        ExpenseForecast expenseForecast = new ExpenseForecast();
        expenseForecast.setId(id);
        return expenseForecast;
    }
}
