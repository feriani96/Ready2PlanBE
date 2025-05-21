package com.readytoplanbe.app.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.readytoplanbe.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ExpenseForecastTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ExpenseForecast.class);
        ExpenseForecast expenseForecast1 = new ExpenseForecast();
        expenseForecast1.setId("id1");
        ExpenseForecast expenseForecast2 = new ExpenseForecast();
        expenseForecast2.setId(expenseForecast1.getId());
        assertThat(expenseForecast1).isEqualTo(expenseForecast2);
        expenseForecast2.setId("id2");
        assertThat(expenseForecast1).isNotEqualTo(expenseForecast2);
        expenseForecast1.setId(null);
        assertThat(expenseForecast1).isNotEqualTo(expenseForecast2);
    }
}
