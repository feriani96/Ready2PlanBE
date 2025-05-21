package com.readytoplanbe.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.readytoplanbe.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProductOrServiceDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProductOrServiceDTO.class);
        ProductOrServiceDTO productOrServiceDTO1 = new ProductOrServiceDTO();
        productOrServiceDTO1.setId("id1");
        ProductOrServiceDTO productOrServiceDTO2 = new ProductOrServiceDTO();
        assertThat(productOrServiceDTO1).isNotEqualTo(productOrServiceDTO2);
        productOrServiceDTO2.setId(productOrServiceDTO1.getId());
        assertThat(productOrServiceDTO1).isEqualTo(productOrServiceDTO2);
        productOrServiceDTO2.setId("id2");
        assertThat(productOrServiceDTO1).isNotEqualTo(productOrServiceDTO2);
        productOrServiceDTO1.setId(null);
        assertThat(productOrServiceDTO1).isNotEqualTo(productOrServiceDTO2);
    }
}
