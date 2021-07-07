package main.service;

import com.github.cage.Cage;
import com.github.cage.GCage;
import main.dto.responses.CaptchaResponse;
import main.repositories.CaptchaRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import javax.imageio.ImageIO;
import javax.transaction.Transactional;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

@Transactional
@Service
public class CaptchaService {

    private final CaptchaRepository captchaRepository;

    @Autowired
    public CaptchaService(CaptchaRepository captchaRepository) {
        this.captchaRepository = captchaRepository;
    }

    public ResponseEntity<?> getCaptcha() throws IOException {
        final Cage cage = new GCage();

        String token = cage.getTokenGenerator().next();
        String secretKey = generateSecretKey();
        String base64String = ConvertImageToBase64(token, cage);
        captchaRepository.insertCaptcha(token, secretKey, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")));
        return new ResponseEntity<>(new CaptchaResponse(secretKey, base64String), HttpStatus.OK);
    }


    private String ConvertImageToBase64(String token, Cage cage) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(ImResizer(cage.drawImage(token)), "png", baos);
        baos.flush();
        byte[] imageInByte = baos.toByteArray();
        baos.close();
        return "data:image/png;base64," + Base64.getEncoder().encodeToString(imageInByte);
    }

    private BufferedImage ImResizer(BufferedImage image) {
        BufferedImage newImage = new BufferedImage(100, 35, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = newImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(image, 0, 0, 100, 35, null);
        g.dispose();
        return newImage;
    }

    private String generateSecretKey() {
        return RandomStringUtils.randomAlphanumeric(10);
    }
}
