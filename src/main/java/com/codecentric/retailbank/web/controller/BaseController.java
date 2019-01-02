package com.codecentric.retailbank.web.controller;

import com.codecentric.retailbank.constants.Constant;
import com.codecentric.retailbank.model.dto.UserDto;
import com.codecentric.retailbank.model.security.User;
import com.codecentric.retailbank.model.security.VerificationToken;
import com.codecentric.retailbank.service.UserService;
import com.codecentric.retailbank.web.error.UserAlreadyExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;

import java.util.Locale;

public class BaseController {
    private Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    protected UserService userService;

    @Autowired
    protected ApplicationEventPublisher eventPublisher;

    @Qualifier("messageSource")
    @Autowired
    protected MessageSource messages;

    @Autowired
    protected JavaMailSender mailSender;

    @Autowired
    protected Environment env;


    public BaseController() {
        super();
    }

    public BaseController(Environment env, MessageSource messages, UserService userService) {
        super();
        this.env = env;
        this.messages = messages;
        this.userService = userService;
    }


    /**
     * Creates a new account in the DB.
     *
     * @param accountDto Account information.
     * @return the newly created account if successful. Otherwise returns <code>null</code>.
     */
    protected User createUserAccount(UserDto accountDto) {
        User registered = null;

        try {
            registered = userService.registerNewUserAccount(accountDto);
        } catch (UserAlreadyExistsException ex) {
            return null;
        }

        return registered;
    }

    /**
     * Used to construct a <code>SimpleMailMessage</code> object required to send an email. This one is used specifically for generating a email verification token resend.
     *
     * @param contextPath Application domain link.
     * @param locale      Language to use.
     * @param newToken    Verification token object.
     * @param user        User to send the email to.
     * @return a new instance of a <code>SimpleMailMessage</code> object.
     */
    protected SimpleMailMessage constructResendVerificationTokenEmail(String contextPath,
                                                                      Locale locale,
                                                                      VerificationToken newToken,
                                                                      User user) {
        String confirmationUrl = contextPath + "/account/registrationConfirm.html?token=" + newToken.getToken();

        String message = messages.getMessage("message.resendToken", null, locale);

        SimpleMailMessage email = new SimpleMailMessage();
        email.setSubject("Resend Registration Token");
        email.setText(message + " \r\n" + confirmationUrl);
        email.setFrom(env.getProperty("support.email"));
        email.setTo(user.getEmail());
        return email;
    }

    /**
     * Used to construct a <code>SimpleMailMessage</code> object required to send an email. This one is used specifically for generating a password reset email.
     *
     * @param contextPath Application domain link.
     * @param locale      Language to use.
     * @param token       Password reset token value.
     * @param user        User to send the email to.
     * @return a new instance of a <code>SimpleMailMessage</code> object.
     */
    protected SimpleMailMessage constructResetTokenEmail(String contextPath,
                                                         Locale locale,
                                                         String token,
                                                         User user) {
        String url = contextPath + "/account/changePassword?id=" +
                user.getId() + "&token=" + token;
        String message = messages.getMessage("message.resetPassword",
                null, locale);
        return constructEmail("Reset Password", message + " \r\n" + url, user);
    }

    /**
     * Used to construct a <code>SimpleMailMessage</code> object required to send an email.
     *
     * @param subject Mail title.
     * @param body    Mail contents.
     * @param user    Mail receiver(takes the <code>email</code> property from the object).
     * @return a new instance of a <code>SimpleMailMessage</code> object.
     */
    protected SimpleMailMessage constructEmail(String subject,
                                               String body,
                                               User user) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setSubject(subject);
        email.setText(body);
        email.setTo(user.getEmail());
        email.setFrom(env.getProperty("support.email"));
        return email;
    }

    /**
     * Sends an email message.
     * <br/>
     * <br/>
     * CONFIGURE YOUR "application.properties" USING YOUR CREDENTIALS:
     * <pre>
     * support.email=jonhdoe@gmail.com
     * spring.mail.host=smtp.gmail.com
     * spring.mail.port=465
     * spring.mail.protocol=smtps
     * spring.mail.username=jonhdoe@gmail.com
     * spring.mail.password=YOUR_GOOGLE_APP_PASSWORD - "https://myaccount.google.com/apppasswords" Note: you must have 2FA activated to generate app passwords
     * spring.mail.properties.mail.transport.protocol=smtps
     * spring.mail.properties.mail.smtps.auth=true
     * spring.mail.properties.mail.smtps.starttls.enable=true
     * spring.mail.properties.mail.smtps.timeout=8000
     * </pre>
     *
     * @param mail       Represents an object containing mail information.
     * @param mailSender Represents the handler object that will be used to send the mail.
     */
    protected void sendEmailMessage(SimpleMailMessage mail, JavaMailSender mailSender) {
        try {
            mailSender.send(mail);
        } catch (MailAuthenticationException ex) {
            LOGGER.debug("MailAuthenticationException", ex);
        } catch (Exception ex) {
            LOGGER.debug("Exception", ex);
        }
    }

    /**
     * Retrieves the class name of a principle object.
     *
     * @return name of the principle object class as a <code>String</code>
     */
    protected String getPrincipalClassName() {
        return getPrincipal().getClass().getName() == null ?
                null
                :
                getPrincipal().getClass().getName();
    }

    /**
     * Adds a <code>username</code> attribute and value to the model.
     *
     * @param model              Model to which we add the <code>username</code> attribute and value.
     * @param principalClassName Type of principal to expect. Required to properly retrieve the <code>username</code> value.
     * @param principal          Principal from which we get the <code>username</code> value.
     */
    protected void setModelUsernameAttribute(Model model, String principalClassName, Object principal) {
        if (principalClassName == Constant.CLASS_NAME_USER_DETAILS_USER) {
            model.addAttribute("username", ((org.springframework.security.core.userdetails.User) principal).getUsername());
        } else if (principalClassName == Constant.CLASS_NAME_OAUTH2_DEFAULT_OIDC_USER) {
            model.addAttribute("username", ((org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser) principal).getFullName());
        }
    }

    /**
     * Retrieves the principal object of the current session.
     *
     * @return <code>Object</code>
     */
    protected Object getPrincipal() {
        return SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
