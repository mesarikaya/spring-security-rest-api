package com.mes.springsecurityapi.security.services.email;


import com.mes.springsecurityapi.domain.security.DTO.HttpResponse;
import com.mes.springsecurityapi.domain.security.User;
import io.vertx.ext.mail.MailClient;
import io.vertx.ext.mail.MailMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicReference;

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
    public HttpResponse sendEmail(User user, String origin) {

        MailMessage message = new MailMessage();
        log.debug("Verification email implementation active.");
        String from = "dcd1a29f84-0ab8c8@inbox.mailtrap.io";
        String subject = "Please Verify Your Registration";
        String to = user.getUsername();
        String content = "Dear " + user.getUsername() + "," + "<br>"+ "<br>" +
                "We are delighted to see you joining our growing member base." +
                "<br>" + "<br>" +
                "To be able to finalize the sign up process, please verify your account by clicking the below given link: " +
                "<a style=\"font-size: 16px; border-top-width: 10px; border-bottom-width: 10px; border-radius: 5px; background: rgb(212, 131, 68) none repeat scroll 0% 0%; border-color: rgb(212, 131, 68);\"" +
                "href=\"" + origin + "/"  + "verify/validate" + "/" + user.getVerificationToken() +
                "\"><button>Verify Account</button></a>\n"
                 + "<br>"+ "<br>" +
                "Or copy baste the below given url to your browser to go to verification page:" +
                "<br>" + "<br>" +
                origin + "/"  + "verify/validate" + "/" + user.getVerificationToken() +
                "<br>" + "<br>" +
                "Thanks for choosing us." +
                "<br>" + "<br>" +
                "Kind Regards," + "<br>" + "On behalf of Team";
        message.setSubject(subject);
        message.setTo(to);
        message.setFrom(from);
        message.setHtml(content);

        AtomicReference<HttpResponse> response = new AtomicReference<>(new HttpResponse(
                HttpStatus.CREATED,
                HttpResponse.ResponseType.SUCCESS,
                "Email is successfully sent!"));


        mailClient.sendMail(message, handler -> {
            if (handler.succeeded()){
                response.set(new HttpResponse(
                        HttpStatus.CREATED,
                        HttpResponse.ResponseType.SUCCESS,
                        "Email is successfully sent!"));
            }else{
                response.set(new HttpResponse(
                        HttpStatus.BAD_REQUEST,
                        HttpResponse.ResponseType.FAILURE,
                        handler.cause().getLocalizedMessage()));
            }
        });

        return response.get();
    }
}
