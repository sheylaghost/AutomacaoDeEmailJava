import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.util.List;
import java.util.Properties;

/**
 * Email Automation System in Java
 * Supports Gmail, Outlook, Yahoo and other SMTP providers
 */
public class EmailAutomation {

    private String senderEmail;
    private String appPassword;
    private String smtpHost;
    private int smtpPort;

    /**
     * Constructor - Initializes email automation
     *
     * @param senderEmail Your email address
     * @param appPassword Your app password (not regular password!)
     */
    public EmailAutomation(String senderEmail, String appPassword) {
        this.senderEmail = senderEmail;
        this.appPassword = appPassword;

        // Detect email provider and configure SMTP
        String domain = senderEmail.split("@")[1];

        switch (domain) {
            case "gmail.com":
                this.smtpHost = "smtp.gmail.com";
                this.smtpPort = 587;
                break;
            case "outlook.com":
            case "hotmail.com":
                this.smtpHost = "smtp-mail.outlook.com";
                this.smtpPort = 587;
                break;
            case "yahoo.com":
                this.smtpHost = "smtp.mail.yahoo.com";
                this.smtpPort = 587;
                break;
            default:
                throw new IllegalArgumentException(
                        "Provider " + domain + " not configured. Use Gmail, Outlook or Yahoo."
                );
        }
    }

    /**
     * Get SMTP session
     */
    private Session getSession() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.port", smtpPort);

        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, appPassword);
            }
        });
    }

    public boolean sendSimpleEmail(String recipient, String subject, String message) {
        try {
            Session session = getSession();

            Message emailMessage = new MimeMessage(session);
            emailMessage.setFrom(new InternetAddress(senderEmail));
            emailMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            emailMessage.setSubject(subject);
            emailMessage.setText(message);

            Transport.send(emailMessage);

            System.out.println("Email sent to " + recipient);
            return true;

        } catch (MessagingException e) {
            System.err.println("Error sending email: " + e.getMessage());
            return false;
        }
    }

    public boolean sendHtmlEmail(String recipient, String subject, String htmlContent) {
        try {
            Session session = getSession();

            Message emailMessage = new MimeMessage(session);
            emailMessage.setFrom(new InternetAddress(senderEmail));
            emailMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            emailMessage.setSubject(subject);
            emailMessage.setContent(htmlContent, "text/html; charset=utf-8");

            Transport.send(emailMessage);

            System.out.println("HTML email sent to " + recipient);
            return true;

        } catch (MessagingException e) {
            System.err.println("Error sending HTML email: " + e.getMessage());
            return false;
        }
    }

    public boolean sendEmailWithAttachment(String recipient, String subject,
                                           String message, String filePath) {
        try {
            Session session = getSession();

            Message emailMessage = new MimeMessage(session);
            emailMessage.setFrom(new InternetAddress(senderEmail));
            emailMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            emailMessage.setSubject(subject);

            Multipart multipart = new MimeMultipart();

            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(message);
            multipart.addBodyPart(textPart);

            File file = new File(filePath);
            if (!file.exists()) {
                System.err.println("File not found: " + filePath);
                return false;
            }

            MimeBodyPart attachmentPart = new MimeBodyPart();
            attachmentPart.attachFile(file);
            multipart.addBodyPart(attachmentPart);

            emailMessage.setContent(multipart);

            Transport.send(emailMessage);

            System.out.println("Email with attachment sent to " + recipient);
            return true;

        } catch (Exception e) {
            System.err.println("Error sending email with attachment: " + e.getMessage());
            return false;
        }
    }

    public int[] sendToMultiple(List<String> recipients, String subject, String message) {
        int sent = 0;
        int failed = 0;

        for (String recipient : recipients) {
            if (sendSimpleEmail(recipient, subject, message)) {
                sent++;
            } else {
                failed++;
            }
        }

        System.out.println("\nSummary: " + sent + " sent, " + failed + " failed");
        return new int[]{sent, failed};
    }

    // MAIN METHOD
    public static void main(String[] args) {
        System.out.println("Email Automation - Java");
        System.out.println("=".repeat(50));

        // CONFIGURAÇÃO ATUALIZADA
        String MY_EMAIL = "eyshilaivanha@gmail.com";
        String APP_PASSWORD = "pbnl mide hrzq hsyf";

        // Create email automation
        EmailAutomation emailBot = new EmailAutomation(MY_EMAIL, APP_PASSWORD);

        // Send test email
        emailBot.sendSimpleEmail(
                "eyshilaivanha@gmail.com",
                "Java Test - It Works!",
                "Hello! This email was sent automatically with Java!"
        );

        System.out.println("\nCheck your inbox!");
    }
}
