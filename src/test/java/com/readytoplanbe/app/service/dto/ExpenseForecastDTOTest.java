package com.readytoplanbe.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.readytoplanbe.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ExpenseForecastDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ExpenseForecastDTO.class);
        ExpenseForecastDTO expenseForecastDTO1 = new ExpenseForecastDTO();
        expenseForecastDTO1.setId("id1");
        ExpenseForecastDTO expenseForecastDTO2 = new ExpenseForecastDTO();
        assertThat(expenseForecastDTO1).isNotEqualTo(expenseForecastDTO2);
        expenseForecastDTO2.setId(expenseForecastDTO1.getId());
        assertThat(expenseForecastDTO1).isEqualTo(expenseForecastDTO2);
        expenseForecastDTO2.setId("id2");
        assertThat(expenseForecastDTO1).isNotEqualTo(expenseForecastDTO2);
        expenseForecastDTO1.setId(null);
        assertThat(expenseForecastDTO1).isNotEqualTo(expenseForecastDTO2);
    }
}
