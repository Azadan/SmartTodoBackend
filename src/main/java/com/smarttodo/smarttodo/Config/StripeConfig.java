package com.smarttodo.smarttodo.Config;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/*
 * En config klass för Stripe
 */
@Configuration
@Getter
public class StripeConfig {

    /*
     * Values är inlagda i min IDE
     */

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @Value("${stripe.price.id}")
    private String stripePriceId;

    @Value("${stripe.webhook.secret}")
    private String stripeWebhookSecret;

    @PostConstruct
    public void init() {
      Stripe.apiKey = stripeApiKey;
    }



}
