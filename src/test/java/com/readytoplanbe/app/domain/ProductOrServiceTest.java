package com.readytoplanbe.app.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.readytoplanbe.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProductOrServiceTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProductOrService.class);
        ProductOrService productOrService1 = new ProductOrService();
        productOrService1.setId("id1");
        ProductOrService productOrService2 = new ProductOrService();
        productOrService2.setId(productOrService1.getId());
        assertThat(productOrService1).isEqualTo(productOrService2);
        productOrService2.setId("id2");
        assertThat(productOrService1).isNotEqualTo(productOrService2);
        productOrService1.setId(null);
        assertThat(productOrService1).isNotEqualTo(productOrService2);
    }
}
