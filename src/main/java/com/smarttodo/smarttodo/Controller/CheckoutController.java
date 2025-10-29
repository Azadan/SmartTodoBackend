package com.smarttodo.smarttodo.Controller;

import com.smarttodo.smarttodo.Entity.User;
import com.smarttodo.smarttodo.Response.ApiResponse;
import com.smarttodo.smarttodo.Service.StripeService;
import com.smarttodo.smarttodo.Service.UserService;
import com.smarttodo.smarttodo.Utility.JwtUtil;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/billing")
@RequiredArgsConstructor
public class CheckoutController {

    private final StripeService stripeService;
    private final UserService userService;
    private JwtUtil jwtUtil;

    @PostMapping("/create-checkout-session")
    public ResponseEntity<ApiResponse> createCheckoutSession() {
        try {
            User user = stripeService.putUserInSession();
            Long userId = user.getUserId();
            String sessionUrl = stripeService.createCheckoutSession(userId);
            Map<String, String> data = new HashMap<>();
            data.put("url", sessionUrl);
            return ResponseEntity.ok(new ApiResponse("Session created", true, data));
        } catch (StripeException e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse("Error", false, e.getMessage()));
        }
    }

    @PostMapping("/webhook")
    public ResponseEntity<?> handleStripeWebhook(@RequestBody String payload,
                                                           @RequestHeader("Stripe-Signature") String sigHeader) {
        boolean success = stripeService.webhookEventHandler(payload, sigHeader);
        if (success) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(BAD_REQUEST).body("failed");
        }
    }
}
