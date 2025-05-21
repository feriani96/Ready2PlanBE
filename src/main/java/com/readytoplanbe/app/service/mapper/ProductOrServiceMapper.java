package com.readytoplanbe.app.service.mapper;

import com.readytoplanbe.app.domain.BusinessPlan;
import com.readytoplanbe.app.domain.ProductOrService;
import com.readytoplanbe.app.service.dto.BusinessPlanDTO;
import com.readytoplanbe.app.service.dto.ProductOrServiceDTO;
import org.mapstruct.*;
/**
 * Mapper pour l'entité {@link ProductOrService} et son DTO {@link ProductOrServiceDTO}.
 */

@Mapper(componentModel = "spring", uses = { RevenueForecastMapper.class })
public interface ProductOrServiceMapper extends EntityMapper<ProductOrServiceDTO, ProductOrService> {

    @Mapping(source = "businessPlan", target = "businessPlan")
    ProductOrServiceDTO toDto(ProductOrService entity);

    @Mapping(source = "businessPlan", target = "businessPlan")
    ProductOrService toEntity(ProductOrServiceDTO dto);

    default ProductOrService fromId(String id) {
        if (id == null) {
            return null;
        }
        ProductOrService productOrService = new ProductOrService();
        productOrService.setId(id);
        return productOrService;
    }

    }
