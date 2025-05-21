package com.readytoplanbe.app.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.readytoplanbe.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BusinessPlanTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BusinessPlan.class);
        BusinessPlan businessPlan1 = new BusinessPlan();
        businessPlan1.setId("id1");
        BusinessPlan businessPlan2 = new BusinessPlan();
        businessPlan2.setId(businessPlan1.getId());
        assertThat(businessPlan1).isEqualTo(businessPlan2);
        businessPlan2.setId("id2");
        assertThat(businessPlan1).isNotEqualTo(businessPlan2);
        businessPlan1.setId(null);
        assertThat(businessPlan1).isNotEqualTo(businessPlan2);
    }
}
