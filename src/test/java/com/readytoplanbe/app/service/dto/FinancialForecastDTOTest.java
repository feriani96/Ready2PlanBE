package com.readytoplanbe.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.readytoplanbe.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FinancialForecastDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(FinancialForecastDTO.class);
        FinancialForecastDTO financialForecastDTO1 = new FinancialForecastDTO();
        financialForecastDTO1.setId("id1");
        FinancialForecastDTO financialForecastDTO2 = new FinancialForecastDTO();
        assertThat(financialForecastDTO1).isNotEqualTo(financialForecastDTO2);
        financialForecastDTO2.setId(financialForecastDTO1.getId());
        assertThat(financialForecastDTO1).isEqualTo(financialForecastDTO2);
        financialForecastDTO2.setId("id2");
        assertThat(financialForecastDTO1).isNotEqualTo(financialForecastDTO2);
        financialForecastDTO1.setId(null);
        assertThat(financialForecastDTO1).isNotEqualTo(financialForecastDTO2);
    }
}
