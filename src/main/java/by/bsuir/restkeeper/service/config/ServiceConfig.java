package by.bsuir.restkeeper.service.config;

import by.bsuir.restkeeper.domain.exception.StorageException;
import by.bsuir.restkeeper.service.property.MailProperty;
import by.bsuir.restkeeper.service.property.MinioProperty;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Properties;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class ServiceConfig {

    private final MinioProperty minioProperty;
    private final MailProperty mailProperty;

    /**
     * Create MinioClient.
     *
     * @return MinioClient
     */
    @Bean
    public MinioClient minioClient() {
        try {
            return MinioClient.builder()
                    .endpoint(this.minioProperty.getUrl())
                    .credentials(
                            this.minioProperty.getAccessKey(),
                            this.minioProperty.getSecretKey()
                    ).build();
        } catch (Exception e) {
            throw new StorageException("Unable to connect to minio!");
        }
    }

    /**
     * Create JavaMailSender.
     *
     * @return JavaMailSender
     */
    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(this.mailProperty.getHost());
        mailSender.setUsername(this.mailProperty.getUsername());
        mailSender.setPassword(this.mailProperty.getPassword());
        mailSender.setPort(this.mailProperty.getPort());
        Properties properties = mailSender.getJavaMailProperties();
        properties.setProperty("mail.smtp.starttls.enable", "true");
        return mailSender;
    }

}
