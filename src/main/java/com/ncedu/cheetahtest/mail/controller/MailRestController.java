package com.ncedu.cheetahtest.mail.controller;

import com.ncedu.cheetahtest.developer.entity.ResetToken;
import com.ncedu.cheetahtest.mail.entity.Email;
import com.ncedu.cheetahtest.mail.entity.GenericResponse;
import com.ncedu.cheetahtest.mail.entity.PasswordDTO;
import com.ncedu.cheetahtest.mail.service.EmailService;
import com.ncedu.cheetahtest.developer.entity.Developer;
import com.ncedu.cheetahtest.developer.service.DeveloperService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@Slf4j
public class MailRestController {

    public static final String FRONT_URL = "http://localhost:8080/api/change-password?token=";
    private final EmailService emailService;
    private final DeveloperService developerService;


    @Autowired
    public MailRestController(EmailService emailService, DeveloperService developerService) {
        this.emailService = emailService;
        this.developerService = developerService;
    }

    @PostMapping("/reset-password")
    public ResponseEntity<GenericResponse> resetPassword(@RequestBody Email email) {
        Developer developer = developerService.findDeveloperByEmail(email.getEmail());
        if (developer == null) {
            return new ResponseEntity<>(new GenericResponse("invalid.email"),HttpStatus.BAD_REQUEST);
        }

        String token = UUID.randomUUID().toString();

        developerService.createPasswordResetTokenForUser(developer, token);
        emailService.sendMessageWithAttachment(email.getEmail(), constructUrl(token));

        return new ResponseEntity<>(new GenericResponse("user.fetched"), HttpStatus.OK);
    }

    @PostMapping("/save-password")
    public ResponseEntity<GenericResponse> changePassword(@RequestBody PasswordDTO passwordDTO) {

        String token = passwordDTO.getToken();

        String result = validatePasswordResetToken(token);
        if (result != null) {
            return new ResponseEntity<>(new GenericResponse(result), HttpStatus.BAD_REQUEST);
        }

        boolean isPasswordSame = developerService.validatePassword(passwordDTO);
        if (isPasswordSame) {
            log.info("Same password as before");
            return new ResponseEntity<>(new GenericResponse("same.password"), HttpStatus.BAD_REQUEST);
        }

        ResetToken resetToken = developerService.findByToken(token);

        if (resetToken != null) {
            developerService.changeUserPassword(resetToken, passwordDTO.getPassword());

            log.info("Password has been successfully reset");
            return new ResponseEntity<>(new GenericResponse("message.resetPasswordSuc"), HttpStatus.OK);
        } else {
            log.info("Reset token doesn't exist");
            return new ResponseEntity<>(new GenericResponse("reset.token.null"), HttpStatus.BAD_REQUEST);
        }
    }

    private String constructUrl(String token) {
        return FRONT_URL + token;
    }

    public String validatePasswordResetToken(String token) {
        final ResetToken passToken = developerService.findByToken(token);

        return !isTokenFound(passToken) ? "invalidToken"
                : isTokenExpired(passToken) ? "expired"
                : null;

    }

    private boolean isTokenFound(ResetToken token) {
        return token != null;
    }

    private boolean isTokenExpired(ResetToken passToken) {
        Calendar calendarExpiry = Calendar.getInstance();
        calendarExpiry.setTime(passToken.getExpiryDate());

        Calendar calendarCurrent = Calendar.getInstance();
        calendarCurrent.setTime(new Date());

        return calendarExpiry.before(calendarCurrent);
    }

}
