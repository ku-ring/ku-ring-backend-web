package com.kustacks.kuring.support;

import com.kustacks.kuring.common.env.MockKuringPropertyRestClient;
import com.kustacks.kuring.common.env.RemotePropertyResolver;
import com.kustacks.kuring.common.featureflag.RemoteFeatureFlags;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class FeatureFlagsSupport {

    private final RemotePropertyResolver remotePropertyResolver;
    private final RemoteFeatureFlags remoteFeatureFlags;

    public FeatureFlagsSupport(RemotePropertyResolver mockRemotePropertyResolver,
                               RemoteFeatureFlags remoteFeatureFlags) {
        this.remotePropertyResolver = mockRemotePropertyResolver;
        this.remoteFeatureFlags = remoteFeatureFlags;
    }

    //refresh를 꼭 해주어야 한다...
    public void setMapProperty(String key, boolean value) {
        setProperty(key, Map.of("enabled", value));
    }

    public void setProperty(String key, Object value) {
        if (remotePropertyResolver instanceof MockKuringPropertyRestClient mockClient) {
            mockClient.setProperty(key, value);
            remoteFeatureFlags.refresh();

        }
    }

    public void resetProperties() {
        if (remotePropertyResolver instanceof MockKuringPropertyRestClient mockClient) {
            mockClient.resetProperties();
            remoteFeatureFlags.refresh();
        }
    }
}
