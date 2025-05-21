package com.readytoplanbe.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.readytoplanbe.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BusinessPlanDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(BusinessPlanDTO.class);
        BusinessPlanDTO businessPlanDTO1 = new BusinessPlanDTO();
        businessPlanDTO1.setId("id1");
        BusinessPlanDTO businessPlanDTO2 = new BusinessPlanDTO();
        assertThat(businessPlanDTO1).isNotEqualTo(businessPlanDTO2);
        businessPlanDTO2.setId(businessPlanDTO1.getId());
        assertThat(businessPlanDTO1).isEqualTo(businessPlanDTO2);
        businessPlanDTO2.setId("id2");
        assertThat(businessPlanDTO1).isNotEqualTo(businessPlanDTO2);
        businessPlanDTO1.setId(null);
        assertThat(businessPlanDTO1).isNotEqualTo(businessPlanDTO2);
    }
}
