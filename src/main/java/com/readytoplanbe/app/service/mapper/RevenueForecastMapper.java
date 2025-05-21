package com.readytoplanbe.app.service.mapper;

import com.readytoplanbe.app.domain.FinancialForecast;
import com.readytoplanbe.app.domain.ProductOrService;
import com.readytoplanbe.app.domain.RevenueForecast;
import com.readytoplanbe.app.service.dto.FinancialForecastDTO;
import com.readytoplanbe.app.service.dto.ProductOrServiceDTO;
import com.readytoplanbe.app.service.dto.RevenueForecastDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link RevenueForecast} and its DTO {@link RevenueForecastDTO}.
 */
@Mapper(componentModel = "spring", uses = {FinancialForecastMapper.class})
public interface RevenueForecastMapper extends EntityMapper<RevenueForecastDTO, RevenueForecast> {

    @Mapping(source = "product", target = "product")
    @Mapping(source = "forecast", target = "forecast")
    RevenueForecastDTO toDto(RevenueForecast revenueForecast);

    @Mapping(source = "product", target = "product")
    @Mapping(source = "forecast", target = "forecast")
    RevenueForecast toEntity(RevenueForecastDTO revenueForecastDTO);

    default RevenueForecast fromId(String id) {
        if (id == null) {
            return null;
        }
        RevenueForecast revenueForecast = new RevenueForecast();
        revenueForecast.setId(id);
        return revenueForecast;
    }
}
