package com.aykutozkat.stripepaymentgateway.config;

import com.aykutozkat.stripepaymentgateway.dto.StripeContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeConfig {

	@Value("${stripe.key.public}")
	private String publicKey;
	@Value("${stripe.key.secret}")
	private String secretKey;

	@Bean
	public StripeContext stripeContext() {
		return new StripeContext(publicKey, secretKey);
	}
}