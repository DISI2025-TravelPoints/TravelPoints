package org.example.userservice.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import java.io.UnsupportedEncodingException;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendPasswordResetEmail(String toEmail, String token) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            //String resetLink = "http://travelpoints.run.place/reset-pass?token=" + token;
            String resetLink = "http://localhost:3000/reset-pass?token=" + token;
            String htmlMsg = "<p>Hi there!</p>" +
                    "<p>We received a request to reset your password for your TravelPoints account.</p>" +
                    "<p>If you made this request, please click the link below to reset your password:</p>" +
                    "<p><a href=\"" + resetLink + "\">Reset your password</a></p>" +
                    "<p>If you didn't request a password reset, you can safely ignore this email.</p>" +
                    "<p>Best regards,<br>TravelPoints Team</p>";

            helper.setText(htmlMsg, true);
            helper.setTo(toEmail);
            helper.setSubject("Password Reset Request");
            helper.setFrom("antonia.rahela@gmail.com", "TravelPoints");

            mailSender.send(mimeMessage);

        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }


}
