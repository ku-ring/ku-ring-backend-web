package com.kustacks.kuring.storage.adapter.out;

import com.kustacks.kuring.common.properties.AwsS3Properties;
import com.kustacks.kuring.storage.exception.CloudStorageException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AwsS3StorageAdapterTest {

    @Mock
    private S3Client mockS3Client;

    @Mock
    private S3Presigner mockS3Presigner;

    @Mock
    private AwsS3Properties properties;

    @InjectMocks
    private AwsS3StorageAdapter awsS3StorageAdapter;

    private final String bucketName = "test-bucket";
    private final String fileKey = "test-file-key";
    private final byte[] testFile = "test-file".getBytes();

    @BeforeEach
    void setUp() {
        when(properties.bucket()).thenReturn(bucketName);
    }

    @DisplayName("S3에 파일을 업로드한다")
    @Test
    void uploadFile() {
        // given
        InputStream inputStream = new ByteArrayInputStream(testFile);

        // when
        awsS3StorageAdapter.upload(inputStream, fileKey, "image/jpeg", testFile.length);

        // then
        ArgumentCaptor<PutObjectRequest> requestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(mockS3Client).putObject(requestCaptor.capture(), any(RequestBody.class));
        PutObjectRequest actualRequest = requestCaptor.getValue();
        assertAll(
                () -> assertThat(actualRequest.bucket()).isEqualTo(bucketName),
                () -> assertThat(actualRequest.key()).isEqualTo(fileKey),
                () -> assertThat(actualRequest.contentType()).isEqualTo("image/jpeg"),
                () -> assertThat(actualRequest.contentLength()).isEqualTo(testFile.length)
        );
    }

    @DisplayName("S3에 파일 업로드 중 SDK 예외가 발생하면 스토리지 예외를 던진다")
    @Test
    void uploadFileWithSdkClientException() {
        // given
        doThrow(SdkClientException.create("S3 error"))
                .when(mockS3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
        InputStream inputStream = new ByteArrayInputStream(testFile);

        // when, then
        assertThatThrownBy(() -> awsS3StorageAdapter.upload(inputStream, fileKey, "image/jpeg", testFile.length))
                .isInstanceOf(CloudStorageException.class);
    }

    @DisplayName("S3 임시 읽기 URL을 생성한다")
    @Test
    void getTemporaryReadUrl() throws MalformedURLException {
        // given
        URL expectedUrl = new URL("https://test.com");

        PresignedGetObjectRequest presignedRequest = mock(PresignedGetObjectRequest.class);
        when(presignedRequest.url()).thenReturn(expectedUrl);

        when(mockS3Presigner.presignGetObject(any(GetObjectPresignRequest.class)))
                .thenReturn(presignedRequest);

        // when
        String actualUrl = awsS3StorageAdapter.getTemporaryReadUrl(fileKey);

        // then
        ArgumentCaptor<GetObjectPresignRequest> requestCaptor = ArgumentCaptor.forClass(GetObjectPresignRequest.class);
        verify(mockS3Presigner).presignGetObject(requestCaptor.capture());
        GetObjectPresignRequest actualPresignRequest = requestCaptor.getValue();
        GetObjectRequest actualObjectRequest = actualPresignRequest.getObjectRequest();
        assertAll(
                () -> assertThat(actualUrl).isEqualTo(expectedUrl.toString()),
                () -> assertThat(actualPresignRequest.signatureDuration()).isEqualTo(Duration.ofHours(1)),
                () -> assertThat(actualObjectRequest.bucket()).isEqualTo(bucketName),
                () -> assertThat(actualObjectRequest.key()).isEqualTo(fileKey)
        );
    }

    @DisplayName("S3 임시 읽기 URL 생성 중 SDK 예외가 발생하면 스토리지 예외를 던진다")
    @Test
    void getTemporaryReadUrlWithSdkClientException() {
        // given
        when(mockS3Presigner.presignGetObject(any(GetObjectPresignRequest.class)))
                .thenThrow(SdkClientException.create("S3 error"));

        // when, then
        assertThatThrownBy(() -> awsS3StorageAdapter.getTemporaryReadUrl(fileKey))
                .isInstanceOf(CloudStorageException.class);
    }

    @DisplayName("S3 파일을 삭제한다")
    @Test
    void deleteFile() {
        // given
        // when
        awsS3StorageAdapter.delete(fileKey);

        // then
        ArgumentCaptor<DeleteObjectRequest> requestCaptor = ArgumentCaptor.forClass(DeleteObjectRequest.class);
        verify(mockS3Client).deleteObject(requestCaptor.capture());
        DeleteObjectRequest actualRequest = requestCaptor.getValue();
        assertAll(
                () -> assertThat(actualRequest.bucket()).isEqualTo(bucketName),
                () -> assertThat(actualRequest.key()).isEqualTo(fileKey)
        );
    }

    @DisplayName("S3 파일 삭제 중 SDK 예외가 발생하면 스토리지 예외를 던진다")
    @Test
    void deleteFileWithSdkClientException() {
        // given
        doThrow(SdkClientException.create("S3 error"))
                .when(mockS3Client).deleteObject(any(DeleteObjectRequest.class));

        // when, then
        assertThatThrownBy(() -> awsS3StorageAdapter.delete(fileKey))
                .isInstanceOf(CloudStorageException.class);
    }
}
