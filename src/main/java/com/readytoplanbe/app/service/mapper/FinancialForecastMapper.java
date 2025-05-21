package com.readytoplanbe.app.service.mapper;

import com.readytoplanbe.app.domain.BusinessPlan;
import com.readytoplanbe.app.domain.FinancialForecast;
import com.readytoplanbe.app.service.dto.BusinessPlanDTO;
import com.readytoplanbe.app.service.dto.FinancialForecastDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link FinancialForecast} and its DTO {@link FinancialForecastDTO}.
 */
@Mapper(componentModel = "spring")
public interface FinancialForecastMapper extends EntityMapper<FinancialForecastDTO, FinancialForecast> {

    @Mapping(source = "revenues", target = "revenues")
    @Mapping(source = "expenses", target = "expenses")
    FinancialForecastDTO toDto(FinancialForecast entity);

    @Mapping(source = "revenues", target = "revenues")
    @Mapping(source = "expenses", target = "expenses")
    FinancialForecast toEntity(FinancialForecastDTO dto);

    default FinancialForecast fromId(String id) {
        if (id == null) {
            return null;
        }
        FinancialForecast financialForecast = new FinancialForecast();
        financialForecast.setId(id);
        return financialForecast;
    }

}
