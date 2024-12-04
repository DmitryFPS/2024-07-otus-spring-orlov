package ru.otus.hw.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannelSpec;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.dsl.PollerSpec;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.scheduling.PollerMetadata;
import org.springframework.messaging.Message;
import ru.otus.hw.model.AcceptanceProduct;
import ru.otus.hw.service.OrderShippingService;
import ru.otus.hw.service.ProductProcessingService;

@Configuration
public class IntegrationConfig {

    @Bean
    public MessageChannelSpec<?, ?> acceptanceProductChannel() {
        return MessageChannels.queue(10);
    }

    @Bean
    public MessageChannelSpec<?, ?> orderProductChannel() {
        return MessageChannels.publishSubscribe();
    }

    @Bean(name = PollerMetadata.DEFAULT_POLLER)
    public PollerSpec poller() {
        return Pollers.fixedRate(100).maxMessagesPerPoll(2);
    }

    @Bean
    public IntegrationFlow deliveryFlow(final ProductProcessingService productProcessingService,
                                        final OrderShippingService orderShippingService) {
        return IntegrationFlow.from(acceptanceProductChannel())
                .split()
                .<AcceptanceProduct>log(LoggingHandler.Level.INFO, "AcceptanceProduct", Message::getPayload)
                .handle(productProcessingService, "stepProcessingProduct")
                .handle(orderShippingService, "stepOrderShipping")
                .aggregate()
                .channel(orderProductChannel())
                .get();
    }
}
