package com.kustacks.kuring.storage.adapter.out;

import com.kustacks.kuring.common.properties.CloudStorageProperties;
import com.kustacks.kuring.storage.application.port.out.StoragePort;
import com.kustacks.kuring.storage.exception.CloudStorageException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.*;

import java.io.*;
import java.net.URL;
import java.time.Duration;

import static com.kustacks.kuring.common.exception.code.ErrorCode.*;

@Profile("dev | prod")
@Service
@RequiredArgsConstructor
public class S3CompatibleStorageAdapter implements StoragePort {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final CloudStorageProperties properties;

    @Override
    public void upload(InputStream inputStream, String key, String contentType) {
        try {
            byte[] fileContent = inputStream.readAllBytes();

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(properties.bucket())
                    .key(key)
                    .contentType(contentType)
                    .contentLength((long) fileContent.length)
                    .build();

                s3Client.putObject(putObjectRequest, RequestBody.fromBytes(fileContent));
        } catch (IOException e) {
            throw new CloudStorageException(FILE_IO_EXCEPTION);
        } catch (S3Exception | SdkClientException e) {
            throw new CloudStorageException(STORAGE_S3_SDK_PROBLEM);
        }
    }

    @Override
    public URL getPresignedUrl(String key) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(properties.bucket())
                    .key(key)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofHours(1)) // 1 hour expiry
                    .getObjectRequest(getObjectRequest)
                    .build();

            PresignedGetObjectRequest presignedGetObjectRequest = s3Presigner.presignGetObject(presignRequest);

            return presignedGetObjectRequest.url();
        } catch (S3Exception | SdkClientException e) {
            throw new CloudStorageException(STORAGE_S3_SDK_PROBLEM);
        }
    }

    @Override
    public void delete(String key) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(properties.bucket())
                    .key(key)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
        } catch (S3Exception | SdkClientException e) {
            throw new CloudStorageException(STORAGE_S3_SDK_PROBLEM);
        }
    }
}
