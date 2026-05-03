package com.yas.product.service;

import com.yas.commonlibrary.config.ServiceUrlConfig;
import com.yas.product.viewmodel.NoFileMediaVm;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MediaServiceTest {

    @Mock
    private RestClient restClient;

    @Mock
    private ServiceUrlConfig serviceUrlConfig;

    @InjectMocks
    private MediaService mediaService;

    @Mock private RestClient.RequestBodyUriSpec requestBodyUriSpec;
    @Mock private RestClient.RequestBodySpec requestBodySpec;
    @Mock private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock private RestClient.RequestHeadersSpec requestHeadersSpec;
    @Mock private RestClient.ResponseSpec responseSpec;

    private MockedStatic<SecurityContextHolder> securityContextHolderMockedStatic;

    @BeforeEach
    void setUp() {
        lenient().when(serviceUrlConfig.media()).thenReturn("http://api.yas.com/media");
        securityContextHolderMockedStatic = mockStatic(SecurityContextHolder.class);
    }

    @AfterEach
    void tearDown() {
        securityContextHolderMockedStatic.close();
    }

    private void mockSecurityContext(String tokenValue) {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        Jwt jwt = mock(Jwt.class);
        when(jwt.getTokenValue()).thenReturn(tokenValue);
        when(authentication.getPrincipal()).thenReturn(jwt);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        securityContextHolderMockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);
    }

    @Test
    void saveFile_ShouldReturnNoFileMediaVm_WhenSuccess() {
        MultipartFile multipartFile = mock(MultipartFile.class);
        Resource resource = mock(Resource.class);
        when(multipartFile.getResource()).thenReturn(resource);
        mockSecurityContext("test-token");

        NoFileMediaVm expectedResponse = new NoFileMediaVm(1L, "caption", "url", "fileName", "type");

        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(any(URI.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(MediaType.MULTIPART_FORM_DATA)).thenReturn(requestBodySpec);
        when(requestBodySpec.headers(any())).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(Object.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(NoFileMediaVm.class)).thenReturn(expectedResponse);

        NoFileMediaVm result = mediaService.saveFile(multipartFile, "caption", "fileName");

        assertNotNull(result);
        assertEquals(1L, result.id());
    }

    @Test
    void getMedia_ShouldReturnEmptyVm_WhenIdIsNull() {
        NoFileMediaVm result = mediaService.getMedia(null);

        assertNotNull(result);
        assertNull(result.id());
    }

    @Test
    void getMedia_ShouldReturnVm_WhenIdIsValid() {
        Long mediaId = 123L;
        NoFileMediaVm expectedResponse = new NoFileMediaVm(mediaId, "caption", "url", "fileName", "type");

        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(URI.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(NoFileMediaVm.class)).thenReturn(expectedResponse);

        NoFileMediaVm result = mediaService.getMedia(mediaId);

        assertEquals(mediaId, result.id());
    }

    @Test
    void removeMedia_ShouldCallDelete_WhenSuccess() {
        Long mediaId = 123L;
        mockSecurityContext("test-token");

        when(restClient.delete()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(URI.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(Void.class)).thenReturn(null);

        assertDoesNotThrow(() -> mediaService.removeMedia(mediaId));
    }
}