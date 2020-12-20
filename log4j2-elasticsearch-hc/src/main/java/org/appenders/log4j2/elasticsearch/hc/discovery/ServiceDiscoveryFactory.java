package org.appenders.log4j2.elasticsearch.hc.discovery;

/*-
 * #%L
 * log4j2-elasticsearch
 * %%
 * Copyright (C) 2020 Rafal Foltynski
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.appenders.log4j2.elasticsearch.ClientProvider;
import org.appenders.log4j2.elasticsearch.hc.ClientProviderPolicy;

public class ServiceDiscoveryFactory<T> {

    protected final ClientProviderPolicy<T> clientProviderPolicy;
    protected final ServiceDiscoveryRequest<T> serviceDiscoveryRequest;
    protected final long refreshInterval;

    public ServiceDiscoveryFactory(
            ClientProviderPolicy<T> clientProviderPolicy,
            ServiceDiscoveryRequest<T> serviceDiscoveryRequest,
            long refreshInterval) {
        this.clientProviderPolicy = clientProviderPolicy;
        this.serviceDiscoveryRequest = serviceDiscoveryRequest;
        this.refreshInterval = refreshInterval;
    }

    public ServiceDiscovery create(final ClientProvider<T> parentClientProvider) {

        return new HCServiceDiscovery<>(
                clientProviderPolicy.apply(parentClientProvider),
                serviceDiscoveryRequest,
                refreshInterval
        );

    }

}
