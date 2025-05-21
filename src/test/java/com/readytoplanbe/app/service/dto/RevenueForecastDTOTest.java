package com.readytoplanbe.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.readytoplanbe.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RevenueForecastDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(RevenueForecastDTO.class);
        RevenueForecastDTO revenueForecastDTO1 = new RevenueForecastDTO();
        revenueForecastDTO1.setId("id1");
        RevenueForecastDTO revenueForecastDTO2 = new RevenueForecastDTO();
        assertThat(revenueForecastDTO1).isNotEqualTo(revenueForecastDTO2);
        revenueForecastDTO2.setId(revenueForecastDTO1.getId());
        assertThat(revenueForecastDTO1).isEqualTo(revenueForecastDTO2);
        revenueForecastDTO2.setId("id2");
        assertThat(revenueForecastDTO1).isNotEqualTo(revenueForecastDTO2);
        revenueForecastDTO1.setId(null);
        assertThat(revenueForecastDTO1).isNotEqualTo(revenueForecastDTO2);
    }
}
