package com.example.loyalty;

import com.example.loyalty.adapters.in.web.CatalogController;
import com.example.loyalty.application.service.CatalogQueryService;
import com.example.loyalty.infrastructure.security.CustomerUserDetailsService;
import com.example.loyalty.infrastructure.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CatalogController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {
    @Autowired MockMvc mockMvc;
    @MockBean CatalogQueryService catalogQueryService;
    @MockBean JwtService jwtService;
    @MockBean CustomerUserDetailsService customerUserDetailsService;

    @Test
    void catalogIsPublic() throws Exception {
        when(catalogQueryService.products(isNull(), any())).thenReturn(Page.empty());
        mockMvc.perform(get("/api/v1/catalog/products")).andExpect(status().isOk());
    }
}
