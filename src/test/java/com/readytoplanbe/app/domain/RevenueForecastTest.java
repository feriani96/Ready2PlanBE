package com.readytoplanbe.app.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.readytoplanbe.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RevenueForecastTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(RevenueForecast.class);
        RevenueForecast revenueForecast1 = new RevenueForecast();
        revenueForecast1.setId("id1");
        RevenueForecast revenueForecast2 = new RevenueForecast();
        revenueForecast2.setId(revenueForecast1.getId());
        assertThat(revenueForecast1).isEqualTo(revenueForecast2);
        revenueForecast2.setId("id2");
        assertThat(revenueForecast1).isNotEqualTo(revenueForecast2);
        revenueForecast1.setId(null);
        assertThat(revenueForecast1).isNotEqualTo(revenueForecast2);
    }
}
