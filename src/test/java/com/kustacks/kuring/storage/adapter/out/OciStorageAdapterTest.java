package com.kustacks.kuring.storage.adapter.out;

import com.kustacks.kuring.common.properties.OciStorageProperties;
import com.kustacks.kuring.storage.exception.CloudStorageException;
import com.oracle.bmc.model.BmcException;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.model.CreatePreauthenticatedRequestDetails;
import com.oracle.bmc.objectstorage.model.PreauthenticatedRequest;
import com.oracle.bmc.objectstorage.requests.CreatePreauthenticatedRequestRequest;
import com.oracle.bmc.objectstorage.requests.DeleteObjectRequest;
import com.oracle.bmc.objectstorage.requests.PutObjectRequest;
import com.oracle.bmc.objectstorage.responses.CreatePreauthenticatedRequestResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OciStorageAdapterTest {

    @Mock
    private ObjectStorage objectStorage;

    @Mock
    private OciStorageProperties properties;

    @InjectMocks
    private OciStorageAdapter ociStorageAdapter;

    private final String namespace = "test-namespace";
    private final String bucketName = "test-bucket";
    private final String region = "test-region";
    private final String fileKey = "test-object-key";
    private final byte[] testFile = "test-file".getBytes();

    @DisplayName("OCI에 파일을 업로드한다")
    @Test
    void uploadFile() {
        // given
        InputStream inputStream = new ByteArrayInputStream(testFile);
        mockBucketProperties();

        // when
        ociStorageAdapter.upload(inputStream, fileKey, "image/jpeg", testFile.length);

        // then
        ArgumentCaptor<PutObjectRequest> requestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(objectStorage).putObject(requestCaptor.capture());
        PutObjectRequest actualRequest = requestCaptor.getValue();
        assertAll(
                () -> assertThat(actualRequest.getNamespaceName()).isEqualTo(namespace),
                () -> assertThat(actualRequest.getBucketName()).isEqualTo(bucketName),
                () -> assertThat(actualRequest.getObjectName()).isEqualTo(fileKey),
                () -> assertThat(actualRequest.getContentType()).isEqualTo("image/jpeg"),
                () -> assertThat(actualRequest.getContentLength()).isEqualTo(testFile.length)
        );
    }

    @DisplayName("OCI 파일 업로드 중 SDK 예외가 발생하면 스토리지 예외를 던진다")
    @Test
    void uploadFileWithBmcException() {
        // given
        InputStream inputStream = new ByteArrayInputStream(testFile);
        mockBucketProperties();
        doThrow(BmcException.createClientSide("OCI error", null, null, null))
                .when(objectStorage).putObject(any(PutObjectRequest.class));

        // when, then
        assertThatThrownBy(() -> ociStorageAdapter.upload(inputStream, fileKey, "image/jpeg", testFile.length))
                .isInstanceOf(CloudStorageException.class);
    }

    @DisplayName("OCI 임시 읽기 URL을 생성한다")
    @Test
    void getTemporaryReadUrl() {
        // given
        mockBucketProperties();
        when(properties.region()).thenReturn(region);

        PreauthenticatedRequest preauthenticatedRequest = PreauthenticatedRequest.builder()
                .accessUri("/p/test-par-token/n/test-namespace/b/test-bucket/o/test-object-key")
                .build();

        CreatePreauthenticatedRequestResponse response = mock(CreatePreauthenticatedRequestResponse.class);
        when(response.getPreauthenticatedRequest()).thenReturn(preauthenticatedRequest);
        when(objectStorage.createPreauthenticatedRequest(any(CreatePreauthenticatedRequestRequest.class)))
                .thenReturn(response);

        Instant beforeExpirationLowerBound = Instant.now().plusSeconds(3590);

        // when
        String actual = ociStorageAdapter.getTemporaryReadUrl(fileKey);

        // then
        Instant afterExpirationUpperBound = Instant.now().plusSeconds(3610);
        ArgumentCaptor<CreatePreauthenticatedRequestRequest> requestCaptor =
                ArgumentCaptor.forClass(CreatePreauthenticatedRequestRequest.class);
        verify(objectStorage).createPreauthenticatedRequest(requestCaptor.capture());
        CreatePreauthenticatedRequestRequest actualRequest = requestCaptor.getValue();
        CreatePreauthenticatedRequestDetails actualDetails = actualRequest.getCreatePreauthenticatedRequestDetails();
        Instant actualExpiration = actualDetails.getTimeExpires().toInstant();

        assertAll(
                () -> assertThat(actual)
                        .isEqualTo("https://objectstorage.test-region.oraclecloud.com/p/test-par-token/n/test-namespace/b/test-bucket/o/test-object-key"),
                () -> assertThat(actualRequest.getNamespaceName()).isEqualTo(namespace),
                () -> assertThat(actualRequest.getBucketName()).isEqualTo(bucketName),
                () -> assertThat(actualDetails.getName()).isEqualTo("temporary-read-" + fileKey),
                () -> assertThat(actualDetails.getAccessType()).isEqualTo(CreatePreauthenticatedRequestDetails.AccessType.ObjectRead),
                () -> assertThat(actualDetails.getObjectName()).isEqualTo(fileKey),
                () -> assertThat(actualExpiration).isBetween(beforeExpirationLowerBound, afterExpirationUpperBound)
        );
    }

    @DisplayName("OCI 임시 읽기 URL 생성 중 SDK 예외가 발생하면 스토리지 예외를 던진다")
    @Test
    void getTemporaryReadUrlWithBmcException() {
        // given
        mockBucketProperties();
        when(objectStorage.createPreauthenticatedRequest(any(CreatePreauthenticatedRequestRequest.class)))
                .thenThrow(BmcException.createClientSide("OCI error", null, null, null));

        // when, then
        assertThatThrownBy(() -> ociStorageAdapter.getTemporaryReadUrl(fileKey))
                .isInstanceOf(CloudStorageException.class);
    }

    @DisplayName("OCI 파일을 삭제한다")
    @Test
    void deleteFile() {
        // given
        mockBucketProperties();

        // when
        ociStorageAdapter.delete(fileKey);

        // then
        ArgumentCaptor<DeleteObjectRequest> requestCaptor = ArgumentCaptor.forClass(DeleteObjectRequest.class);
        verify(objectStorage).deleteObject(requestCaptor.capture());
        DeleteObjectRequest actualRequest = requestCaptor.getValue();
        assertAll(
                () -> assertThat(actualRequest.getNamespaceName()).isEqualTo(namespace),
                () -> assertThat(actualRequest.getBucketName()).isEqualTo(bucketName),
                () -> assertThat(actualRequest.getObjectName()).isEqualTo(fileKey)
        );
    }

    @DisplayName("OCI 파일 삭제 중 SDK 예외가 발생하면 스토리지 예외를 던진다")
    @Test
    void deleteFileWithBmcException() {
        // given
        mockBucketProperties();
        doThrow(BmcException.createClientSide("OCI error", null, null, null))
                .when(objectStorage).deleteObject(any(DeleteObjectRequest.class));

        // when, then
        assertThatThrownBy(() -> ociStorageAdapter.delete(fileKey))
                .isInstanceOf(CloudStorageException.class);
    }

    private void mockBucketProperties() {
        when(properties.namespace()).thenReturn(namespace);
        when(properties.bucket()).thenReturn(bucketName);
    }
}
