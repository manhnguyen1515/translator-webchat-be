package com.translator.webchat.util;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class GenerateOTP {
    private static final Map<String, OTPDetails> otpStore = new HashMap<>();
    private final Environment env;

    public GenerateOTP(Environment env) {
        this.env = env;
    }

    public String generateOTP(String email) {
        int maxOtpValue = Integer.parseInt(env.getProperty("otp.max.value", "999999"));
        String otpFormat = env.getProperty("otp.format", "%06d");
        int randomPin = (int) (Math.random() * maxOtpValue);
        String otp = String.format(otpFormat, randomPin);
        otpStore.put(email, new OTPDetails(otp, LocalDateTime.now()));
        return otp;
    }

    public boolean isOtpValid(String email, String otp) {
        OTPDetails details = otpStore.get(email);
        if (details == null || !details.getOtp().equals(otp)) {
            return false;
        }
        int validityDuration = Integer.parseInt(env.getProperty("otp.validity.duration", "15"));
        return details.getGenerationTime().plusMinutes(validityDuration).isAfter(LocalDateTime.now());
    }

    private static class OTPDetails {
        private final String otp;
        private final LocalDateTime generationTime;

        public OTPDetails(String otp, LocalDateTime generationTime) {
            this.otp = otp;
            this.generationTime = generationTime;
        }

        public String getOtp() {
            return otp;
        }

        public LocalDateTime getGenerationTime() {
            return generationTime;
        }
    }
}