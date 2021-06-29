package eu.paliwoda.servicepoller.web;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class WebClientService {

    public WebClient.RequestHeadersUriSpec<?> get(String url) {
        return WebClient.create(url).get();
    }

}
