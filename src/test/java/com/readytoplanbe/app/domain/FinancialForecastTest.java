package com.readytoplanbe.app.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.readytoplanbe.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FinancialForecastTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(FinancialForecast.class);
        FinancialForecast financialForecast1 = new FinancialForecast();
        financialForecast1.setId("id1");
        FinancialForecast financialForecast2 = new FinancialForecast();
        financialForecast2.setId(financialForecast1.getId());
        assertThat(financialForecast1).isEqualTo(financialForecast2);
        financialForecast2.setId("id2");
        assertThat(financialForecast1).isNotEqualTo(financialForecast2);
        financialForecast1.setId(null);
        assertThat(financialForecast1).isNotEqualTo(financialForecast2);
    }
}
