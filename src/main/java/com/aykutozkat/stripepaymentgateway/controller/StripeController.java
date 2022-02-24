package com.aykutozkat.stripepaymentgateway.controller;

import com.aykutozkat.stripepaymentgateway.dto.Response;
import com.aykutozkat.stripepaymentgateway.dto.StripeContext;
import com.aykutozkat.stripepaymentgateway.service.StripeService;
import com.stripe.model.Coupon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class StripeController {

	@Autowired
	private StripeContext context;
	@Autowired
	private StripeService service;

	@GetMapping("/")
	public String home() {
		return "home";
	}

	@GetMapping("/subscription")
	public String subscription(Model model) {
		model.addAttribute("stripePublicKey", context.getPublicKey());
		return "subscription";
	}

	@GetMapping("/charge")
	public String charge(Model model) {
		model.addAttribute("stripePublicKey", context.getPublicKey());
		return "charge";
	}

	@PostMapping("/create-subscription")
	@ResponseBody
	public Response createSubscription(String email, String token, String plan, String coupon) {
		if (token == null || plan.isEmpty()) {
			return new Response(false, "Stripe payment token is missing. Please try again later.");
		}

		String customerId = service.createCustomer(email, token);

		if (customerId == null) {
			return new Response(false, "An error occurred while trying to create customer");
		}

		String subscriptionId = service.createSubscription(customerId, plan, coupon);

		if (subscriptionId == null) {
			return new Response(false, "An error occurred while trying to create subscription");
		}

		return new Response(true, "Success! Your subscription id is " + subscriptionId);
	}

	@PostMapping("/cancel-subscription")
	@ResponseBody
	public Response cancelSubscription(String subscriptionId) {
		boolean subscriptionStatus = service.cancelSubscription(subscriptionId);

		if (!subscriptionStatus) {
			return new Response(false, "Failed to cancel subscription. Please try again later");
		}

		return new Response(true, "Subscription cancelled successfully");
	}

	@PostMapping("/coupon-validator")
	@ResponseBody
	public Response couponValidator(String code) {
		Coupon coupon = service.retriveCoupon(code);

		if (coupon != null && coupon.getValid()) {
			String details = (coupon.getPercentOff() == null ? "$" + (coupon.getAmountOff() / 100)
					: coupon.getPercentOff() + "%") + "OFF" + coupon.getDuration();
			return new Response(true, details);
		}
		return new Response(false, "This coupon code is not available. This may be because it has expired or has "
				+ "already been applied to your account.");
	}

	@PostMapping("/create-charge")
	@ResponseBody
	public Response createCharge(String email, String token) {
		if (token == null) {
			return new Response(false, "Stripe payment token is missing. Please try again later.");
		}

		String chargeId = service.createCharge(email, token, 999);// 9.99 usd

		if (chargeId == null) {
			return new Response(false, "An error occurred while trying to charge.");
		}

		return new Response(true, "Success! Your charge id is " + chargeId);
	}

}