package com.kustacks.kuring.storage.adapter.out;

import com.kustacks.kuring.common.properties.OciStorageProperties;
import com.kustacks.kuring.storage.application.port.out.StoragePort;
import com.kustacks.kuring.storage.exception.CloudStorageException;
import com.oracle.bmc.model.BmcException;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.requests.DeleteObjectRequest;
import com.oracle.bmc.objectstorage.requests.PutObjectRequest;
import com.oracle.bmc.objectstorage.model.CreatePreauthenticatedRequestDetails;
import com.oracle.bmc.objectstorage.requests.CreatePreauthenticatedRequestRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.OffsetDateTime;
import java.util.Date;

import static com.kustacks.kuring.common.exception.code.ErrorCode.STORAGE_S3_SDK_PROBLEM;

@Profile("dev")
@Service
@RequiredArgsConstructor
public class OciStorageAdapter implements StoragePort {

    private final ObjectStorage objectStorage;
    private final OciStorageProperties properties;

    @Override
    public void upload(InputStream inputStream, String key, String contentType, long contentLength) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .namespaceName(properties.namespace())
                    .bucketName(properties.bucket())
                    .objectName(key)
                    .contentType(contentType)
                    .contentLength(contentLength)
                    .putObjectBody(inputStream)
                    .build();

            objectStorage.putObject(putObjectRequest);
        } catch (BmcException e) {
            throw new CloudStorageException(STORAGE_S3_SDK_PROBLEM);
        }
    }

    @Override
    public String getTemporaryReadUrl(String key) {
        try {
            CreatePreauthenticatedRequestDetails details = CreatePreauthenticatedRequestDetails.builder()
                    .name("temporary-read-" + key)
                    .accessType(CreatePreauthenticatedRequestDetails.AccessType.ObjectRead)
                    .objectName(key)
                    .timeExpires(Date.from(OffsetDateTime.now().plusHours(1).toInstant()))
                    .build();

            CreatePreauthenticatedRequestRequest request = CreatePreauthenticatedRequestRequest.builder()
                    .namespaceName(properties.namespace())
                    .bucketName(properties.bucket())
                    .createPreauthenticatedRequestDetails(details)
                    .build();

            String accessUri = objectStorage.createPreauthenticatedRequest(request)
                    .getPreauthenticatedRequest()
                    .getAccessUri();

            return canonicalEndpoint() + accessUri;
        } catch (BmcException e) {
            throw new CloudStorageException(STORAGE_S3_SDK_PROBLEM);
        }
    }

    @Override
    public void delete(String key) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .namespaceName(properties.namespace())
                    .bucketName(properties.bucket())
                    .objectName(key)
                    .build();

            objectStorage.deleteObject(deleteObjectRequest);
        } catch (BmcException e) {
            throw new CloudStorageException(STORAGE_S3_SDK_PROBLEM);
        }
    }

    private String canonicalEndpoint() {
        return objectStorage.getEndpoint();
    }
}
