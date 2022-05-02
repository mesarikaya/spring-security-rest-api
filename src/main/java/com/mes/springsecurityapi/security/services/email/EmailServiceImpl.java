package com.mes.springsecurityapi.security.services.email;


import com.mes.springsecurityapi.domain.security.DTO.HttpResponse;
import com.mes.springsecurityapi.domain.security.User;
import io.vertx.ext.mail.MailClient;
import io.vertx.ext.mail.MailMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
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
    public HttpResponse sendAccountVerificationEmail(@NotNull User user, String origin) {

        MailMessage message = setMailMessage(user, origin, "AccountVerification");

        AtomicReference<HttpResponse> response = new AtomicReference<>(
                new HttpResponse(HttpStatus.CREATED, HttpResponse.ResponseType.SUCCESS, "Email is successfully sent!"));

        return sendEmail(message, response);
    }

    @Override
    public HttpResponse sendPasswordVerificationEmail(@NotNull User user, String origin) {
        MailMessage message = setMailMessage(user, origin, "PasswordUpdateRequestVerification");

        AtomicReference<HttpResponse> response = new AtomicReference<>(
                new HttpResponse(HttpStatus.CREATED, HttpResponse.ResponseType.SUCCESS, "Email is successfully sent!"));

        return sendEmail(message, response);
    }

    private HttpResponse sendEmail(MailMessage message, AtomicReference<HttpResponse> response) {
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

    @NotNull
    private MailMessage setMailMessage(User user, String origin, String type) {
        MailMessage message = new MailMessage();
        log.debug("Verification email implementation active.");
        String from = "dcd1a29f84-0ab8c8@inbox.mailtrap.io";
        String subject = "Please Verify Your Registration";
        String to = user.getUsername();
        String content = (type=="AccountVerification") ? getAccountVerificationEmailContent(user,origin) : getPasswordUpdateEmailContent(user,origin);
        message.setSubject(subject);
        message.setTo(to);
        message.setFrom(from);
        message.setHtml(content);
        return message;
    }

    @NotNull
    private String getAccountVerificationEmailContent(User user, String origin){
        String content = "Dear " + user.getUsername() + "," + "<br>"+ "<br>" +
                "We are delighted to see you joining our growing client base." +
                "<br>" + "<br>" +
                "To be able to finalize the sign up process, please verify your account by clicking the below given link: " +
                "<a " + "href=\"" + origin + "/"  + "verify/account/validate" + "/" + user.getVerificationToken()
                + "\"><button style=\"background-color:rgb(248, 196, 113); border-color:rgb(248, 196, 113); font-weight: bold;\">Verify Account</button></a>\n"
                + "<br>"+ "<br>" +
                "Or copy and paste the below given url to your browser to go to verification page:" +
                "<br>" + "<br>" +
                origin + "/"  + "verify/account/validate" + "/" + user.getVerificationToken() +
                "<br>" + "<br>" +
                "Thanks for choosing us." +
                "<br>" + "<br>" +
                "Kind Regards," + "<br>" + "<br>" +
                "On behalf of Team";

        return content;
    }

    @NotNull
    private String getPasswordUpdateEmailContent(User user, String origin){
        String content = "Dear " + user.getUsername() + "," + "<br>"+ "<br>" +
                "Below you can find the security token. This token needs to be provided in order to update your account" +
                "password." +
                "<br>" + "<br>" +
                "To be able to finalize the password update process, please verify your credentials by clicking the below given link: " +
                "<a " + "href=\"" + origin + "/"  + "verify/password/validate" + "/" + user.getPasswordUpdateToken() +
                "\"><button style=\"background-color:rgb(248, 196, 113); border-color:rgb(248, 196, 113); font-weight: bold;\">Verify Password Request</button></a>\n"
                + "<br>"+ "<br>" +
                "Or copy and paste the below given url to your browser to go to the password update page:" +
                "<br>" + "<br>" +
                origin + "/"  + "verify/password/validate" + "/" + user.getPasswordUpdateToken() +
                "<br>" + "<br>" +
                "If you have not made such a request, we strongly recommend you to go to your account and update your credentials." +
                "<br>" + "<br>" +
                "Kind Regards," + "<br>" + "<br>" +
                "On behalf of Team";

        return content;
    }
}
