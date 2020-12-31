package com.mes.springsecurityapi.security.services.email;


import com.mes.springsecurityapi.domain.security.User;
import io.vertx.ext.mail.MailClient;
import io.vertx.ext.mail.MailMessage;
import io.vertx.ext.mail.MailResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * Created by mesar on 12/31/2020
 */
@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    private final MailClient mailClient;

    public EmailServiceImpl(MailClient mailClient){
        this.mailClient = mailClient;
    }

    @Override
    public Mono<ResponseEntity<?>> sendEmail(User user, String origin) {

        MailMessage message = new MailMessage();
        log.debug("Verification email implementation active.");
        String from = "dcd1a29f84-0ab8c8@inbox.mailtrap.io";
        String subject = "Please Verify Your Registration";
        String to = user.getUsername();
        String content = "Dear " + user.getUsername() + "," + "<br>"+ "<br>" +
                "We are delighted to see you joining our growing member base."+ "<br>" +
                "To be able to finalize the sign up process, please verify your account by clicking the link: " + "<br>" +
                origin + "/"  + "verify/validate" +
                "/" + user.getUsername()+ "/" + user.getVerificationToken() + "." +"<br>"+ "<br>" +
                "Thanks for choosing us." + "<br>" +
                "Kind Regards," + "<br>" + "On behalf of Team";
        message.setSubject(subject);
        message.setTo(to);
        message.setFrom(from);
        message.setHtml(content);

        mailClient
            .sendMail(message)
            .onSuccess(System.out::println)
            .onFailure(Throwable::printStackTrace);

        return Mono.just(ResponseEntity.ok().build());
    }
}
