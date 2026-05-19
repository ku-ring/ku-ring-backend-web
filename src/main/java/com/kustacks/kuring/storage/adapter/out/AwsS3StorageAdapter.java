package com.kustacks.kuring.storage.adapter.out;

import com.kustacks.kuring.common.properties.AwsS3Properties;
import com.kustacks.kuring.storage.application.port.out.StoragePort;
import com.kustacks.kuring.storage.exception.CloudStorageException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.InputStream;
import java.time.Duration;

import static com.kustacks.kuring.common.exception.code.ErrorCode.STORAGE_S3_SDK_PROBLEM;

@Profile("prod")
@Service
@RequiredArgsConstructor
public class AwsS3StorageAdapter implements StoragePort {

    private static final Duration TEMPORARY_READ_URL_DURATION = Duration.ofHours(1);

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final AwsS3Properties properties;

    @Override
    public void upload(InputStream inputStream, String key, String contentType, long contentLength) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(properties.bucket())
                    .key(key)
                    .contentType(contentType)
                    .contentLength(contentLength)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, contentLength));
        } catch (S3Exception | SdkClientException e) {
            throw new CloudStorageException(STORAGE_S3_SDK_PROBLEM);
        }
    }

    @Override
    public String getTemporaryReadUrl(String key) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(properties.bucket())
                    .key(key)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(TEMPORARY_READ_URL_DURATION)
                    .getObjectRequest(getObjectRequest)
                    .build();

            PresignedGetObjectRequest presignedGetObjectRequest = s3Presigner.presignGetObject(presignRequest);
            return presignedGetObjectRequest.url().toString();
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
