package com.smarttodo.smarttodo.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.smarttodo.smarttodo.Config.StripeConfig;
import com.smarttodo.smarttodo.Entity.User;
import com.smarttodo.smarttodo.Exception.UserNotFoundException;
import com.smarttodo.smarttodo.Repository.UserRepository;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;

import lombok.RequiredArgsConstructor;

/*
 * Stripe Service
 */
@Service
@RequiredArgsConstructor
public class StripeService {

    private final StripeConfig stripeConfig;

    private final UserService userService;

    private final UserRepository userRepository;

    @Value("${app.frontend.url}")
    private String frontendUrl;


    /*
     * Skapar en Stripe checkout session för en användare och returnerar URL:en för betalningen
     * @param userId ID för användaren som skapar sessionen
     * @return URL för Stripe checkout session
     * @throws StripeException om det uppstår ett fel vid skapandet av sessionen
     */
    public String createCheckoutSession(Long userId) throws StripeException {
        //Börjar med att säkerställa att användaren finns innan vi skickar ett id till stripe
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User with this " + userId + " is not found"));

        // Skapar line items för checkout session
        List<SessionCreateParams.LineItem> lineItems = new ArrayList<>();

        // Lägg till en line item med pris-ID och kvantitet
        lineItems.add(
                SessionCreateParams.LineItem.builder()
                        .setPrice(stripeConfig.getStripePriceId())
                        .setQuantity(1L)
                        .build()
        );

        // Skapar checkout session med referens till användaren och nödvändiga URL:er
        SessionCreateParams params = SessionCreateParams.builder()
                // Payment mode
                .setMode(SessionCreateParams.Mode.PAYMENT)
                // Referens till användaren
                .setClientReferenceId(user.getUserId().toString())
                // Line items (alltså vad som ska köpas)
                .addAllLineItem(lineItems)
                // URL:er för framgång och misslyckad betalning
                .setSuccessUrl(frontendUrl + "/payment-success")
                .setCancelUrl(frontendUrl + "/payment-canceled")
                // Metadata för att lagra användar-ID
                .putMetadata("userId", user.getUserId().toString())
                .addExpand("line_items")
                .build();

        Session session = Session.create(params);
        return session.getUrl();
    }

    /*
     * Hanterar inkommande webhook-händelser från Stripe och triggar vidare hantering
     * @param payload JSON-payload från webhook-händelsen
     * @param sigHeader Signaturheader för att verifiera webhook-händelsen
     * @return true om händelsen hanterades framgångsrikt, annars false
     */
    public boolean webhookEventHandler(String payload, String sigHeader) {
        try {
            // Verifiera och konstruera Stripe-händelsen från payload och signatur
            Event event = Webhook.constructEvent(
                    payload, sigHeader, stripeConfig.getStripeWebhookSecret()
            );

            // Om eventet är en slutförd checkout-session, hantera den
            if ("checkout.session.completed".equals(event.getType())) {
                // Deserialisera objektet i eventet till en Session
                parseAndProcessSessionFromJson(payload);
            }

            return true;
        } catch (SignatureVerificationException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            return false;
        }
    }

    /*
     * Hämtar den inloggade användaren från säkerhetskontexten och returnerar användarobjektet
     * @return Användarobjektet för den inloggade användaren
     * @throws UserNotFoundException om användaren inte hittas i databasen
     */
    public User putUserInSession() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Optional<User> user = userRepository.findByEmail(email);

        if (!user.isPresent()) {
            throw new UserNotFoundException("User with email " + email + " not found");

        }
        return user.get();
    }

    /*
     * Plockar utt relevant information från JSON-payloaden och uppgraderar användaren till premium
     * @param payload JSON-payload från webhook-händelsen
     */
    private void parseAndProcessSessionFromJson(String payload) {
        try {
            // Omvandla Json-payload till JSONObject. Då blir det lättare att plocka ut data
            JSONObject eventJson = new JSONObject(payload);
            JSONObject dataObject = eventJson.getJSONObject("data");
            JSONObject sessionObject = dataObject.getJSONObject("object");


            // Försök hämta client_reference_id som sättes vid skapandet av sessionen
            String clientReferenceId = null;
            if (sessionObject.has("client_reference_id") && !sessionObject.isNull("client_reference_id")) {
                clientReferenceId = sessionObject.getString("client_reference_id");
            }

            // Försök hämta userId från metadata
            String userIdFromMetadata = null;
            if (sessionObject.has("metadata") && !sessionObject.isNull("metadata")) {
                JSONObject metadata = sessionObject.getJSONObject("metadata");
                if (metadata.has("userId") && !metadata.isNull("userId")) {
                    userIdFromMetadata = metadata.getString("userId");
                }
            }

            // Uppgradera användaren till premium baserat på tillgängligt ID
            if (clientReferenceId != null) {
                Long userId = Long.parseLong(clientReferenceId);
                upgradeToPremium(userId);
                // Logik för att uppgradera användaren till premium med clientReferenceId. Ifall ovan misslyckas
            } else if (userIdFromMetadata != null) {
                Long userId = Long.parseLong(userIdFromMetadata);
                upgradeToPremium(userId);
            } else {
                throw new RuntimeException("No userId found in session");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse session from JSON", e);
        }
    }

    public void upgradeToPremium (Long userId){
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User with this " + userId + " is not found"));

            if (!user.isPremiumUser()) {
                user.setPremiumUser(true);
                userRepository.save(user);

            } else {
                System.out.println("User " + userId + " is already premium");
            }
        }
}