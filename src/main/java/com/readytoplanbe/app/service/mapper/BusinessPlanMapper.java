package com.readytoplanbe.app.service.mapper;

import com.readytoplanbe.app.domain.BusinessPlan;
import com.readytoplanbe.app.service.dto.BusinessPlanDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link BusinessPlan} and its DTO {@link BusinessPlanDTO}.
 */
@Mapper(componentModel = "spring", uses = { ProductOrServiceMapper.class })
public interface BusinessPlanMapper extends EntityMapper<BusinessPlanDTO, BusinessPlan> {

    @Mapping(source = "products", target = "products")
    @Mapping(source = "forecast", target = "forecast")
    BusinessPlanDTO toDto(BusinessPlan entity);

    @Mapping(source = "products", target = "products")
    @Mapping(source = "forecast", target = "forecast")
    BusinessPlan toEntity(BusinessPlanDTO dto);

    default BusinessPlan fromId(String id) {
        if (id == null) {
            return null;
        }
        BusinessPlan businessPlan = new BusinessPlan();
        businessPlan.setId(id);
        return businessPlan;
    }
}
