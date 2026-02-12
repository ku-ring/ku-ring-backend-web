package com.kustacks.kuring.storage.adapter.out;

import com.kustacks.kuring.common.properties.CloudStorageProperties;
import com.kustacks.kuring.storage.exception.CloudStorageException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.*;

import java.io.*;
import java.net.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class S3CompatibleStorageAdapterTest {

    @Mock
    private S3Client mockS3Client;

    @Mock
    private S3Presigner mockS3Presigner;

    @Mock
    private CloudStorageProperties properties;

    @InjectMocks
    private S3CompatibleStorageAdapter s3CompatibleStorageAdapter;

    private String bucketName = "test-bucket";
    private String fileKey = "test-file-key";
    private byte[] testFile = "test-file".getBytes();

    @DisplayName("S3에 파일을 업로드한다")
    @Test
    void uploadFile() {
        // given
        when(properties.bucket()).thenReturn(bucketName);
        InputStream inputStream = new ByteArrayInputStream(testFile);

        // when
        s3CompatibleStorageAdapter.upload(inputStream, fileKey, "image/jpeg");

        // then
        verify(mockS3Client, times(1))
                .putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @DisplayName("S3에 파일을 업로드 중 IOException이 발생하면 예외를 던진다")
    @Test
    void uploadFileWithIOException() throws IOException {
        // given
        InputStream mockInputStream = mock(InputStream.class);
        when(mockInputStream.readAllBytes()).thenThrow(new IOException());

        // when, then
        assertThatThrownBy(() -> s3CompatibleStorageAdapter.upload(mockInputStream, fileKey, "image/jpeg"))
                .isInstanceOf(CloudStorageException.class);
    }

    @DisplayName("S3에 파일을 업로드 중 S3Exception이 발생하면 예외를 던진다")
    @Test
    void uploadFileWithS3Exception() {
        // given
        when(properties.bucket()).thenReturn(bucketName);
        doThrow(SdkClientException.create("S3 error"))
                .when(mockS3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));

        InputStream inputStream = new ByteArrayInputStream(testFile);

        // when, then
        assertThatThrownBy(() -> s3CompatibleStorageAdapter.upload(inputStream, fileKey, "image/jpeg"))
                .isInstanceOf(CloudStorageException.class);
    }


    @DisplayName("S3 presigned url을 생성한다")
    @Test
    void getPresignedUrl() throws MalformedURLException {
        // given
        URL expectedUrl = new URL("https://test.com");
        when(properties.bucket()).thenReturn(bucketName);

        PresignedGetObjectRequest presignedRequest = mock(PresignedGetObjectRequest.class);
        when(presignedRequest.url()).thenReturn(expectedUrl);

        when(mockS3Presigner.presignGetObject(any(GetObjectPresignRequest.class)))
                .thenReturn(presignedRequest);

        // when
        String actualUrl = s3CompatibleStorageAdapter.getPresignedUrl(fileKey);

        // then
        assertThat(actualUrl).isEqualTo(expectedUrl.toString());
        verify(mockS3Presigner, times(1)).presignGetObject(any(GetObjectPresignRequest.class));
    }

    @DisplayName("S3 presigned url 생성 중 S3Exception이 발생하면 예외를 던진다")
    @Test
    void getPresignedUrlWithS3Exception() {
        // given
        when(properties.bucket()).thenReturn(bucketName);
        when(mockS3Presigner.presignGetObject(any(GetObjectPresignRequest.class)))
                .thenThrow(SdkClientException.create("S3 error"));

        // when, then
        assertThatThrownBy(() -> s3CompatibleStorageAdapter.getPresignedUrl(fileKey))
                .isInstanceOf(CloudStorageException.class);
    }

    @DisplayName("S3 파일을 삭제한다")
    @Test
    void deleteFile() {
        // given
        when(properties.bucket()).thenReturn(bucketName);

        // when
        s3CompatibleStorageAdapter.delete(fileKey);

        // then
        verify(mockS3Client, times(1)).deleteObject(any(DeleteObjectRequest.class));
    }

    @DisplayName("S3 파일 삭제 중 S3Exception이 발생하면 예외를 던진다")
    @Test
    void deleteFileWithS3Exception() {
        // given
        when(properties.bucket()).thenReturn(bucketName);
        doThrow(SdkClientException.create("S3 error"))
                .when(mockS3Client).deleteObject(any(DeleteObjectRequest.class));

        // when, then
        assertThatThrownBy(() -> s3CompatibleStorageAdapter.delete(fileKey))
                .isInstanceOf(CloudStorageException.class);
    }
}