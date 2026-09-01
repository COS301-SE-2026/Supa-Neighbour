package com.app.api.unit.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.app.api.config.OpenApiConfig;

import io.swagger.v3.oas.models.OpenAPI;


@ExtendWith(MockitoExtension.class)
class OpenApiConfigTest {

    private OpenApiConfig openApiConfig;

    @BeforeEach
    void setUp() {
        openApiConfig = new OpenApiConfig();
    }

    @Test
    void appOpenAPI_returnsNonNullBean() {
        OpenAPI api = openApiConfig.appOpenAPI();

        assertThat(api).isNotNull();
    }

    @Test
    void appOpenAPI_hasTitleSocialAppAPI() {
        OpenAPI api = openApiConfig.appOpenAPI();

        assertThat(api.getInfo()).isNotNull();
        assertThat(api.getInfo().getTitle()).isEqualTo("Social App API");
    }

    @Test
    void appOpenAPI_hasExpectedDescription() {
        OpenAPI api = openApiConfig.appOpenAPI();

        assertThat(api.getInfo().getDescription())
                .isEqualTo("API documentation for the Social App backend");
    }


    @Test
    void appOpenAPI_hasVersionOnePointZero() {
        OpenAPI api = openApiConfig.appOpenAPI();

        assertThat(api.getInfo().getVersion()).isEqualTo("1.0.0");
    }


    @Test
    void appOpenAPI_infoFieldsAllCorrect() {
        OpenAPI api = openApiConfig.appOpenAPI();

        assertThat(api.getInfo())
                .isNotNull()
                .satisfies(info -> {
                    assertThat(info.getTitle()).isEqualTo("Social App API");
                    assertThat(info.getDescription())
                            .isEqualTo("API documentation for the Social App backend");
                    assertThat(info.getVersion()).isEqualTo("1.0.0");
                });
    }
}
