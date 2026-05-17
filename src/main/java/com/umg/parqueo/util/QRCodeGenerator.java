package com.umg.parqueo.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Utilitario para generación y lectura de códigos QR con ZXing.
 * Implementa parte del requerimiento RF06 (RT-08).
 */
@Component
public class QRCodeGenerator {

    private static final int ANCHO_DEFAULT  = 300;
    private static final int ALTO_DEFAULT   = 300;

    /**
     * Genera un PNG del QR a partir del contenido y lo devuelve en Base64
     * listo para mostrar en frontend con: <img src="data:image/png;base64,..." />
     */
    public String generarQrBase64(String contenido) {
        return generarQrBase64(contenido, ANCHO_DEFAULT, ALTO_DEFAULT);
    }

    public String generarQrBase64(String contenido, int ancho, int alto) {
        try {
            byte[] pngBytes = generarQrPng(contenido, ancho, alto);
            return Base64.getEncoder().encodeToString(pngBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error generando QR: " + e.getMessage(), e);
        }
    }

    public byte[] generarQrPng(String contenido, int ancho, int alto) throws Exception {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        hints.put(EncodeHintType.MARGIN, 1);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix bitMatrix = writer.encode(contenido, BarcodeFormat.QR_CODE, ancho, alto, hints);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", baos);
            return baos.toByteArray();
        }
    }

    /**
     * Decodifica un QR (recibido como PNG en bytes) y devuelve su contenido.
     * Útil cuando en el futuro el módulo de seguridad escanee con cámara.
     */
    public String leerQr(byte[] imagenPng) throws IOException {
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imagenPng));
        BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(image);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        try {
            Result result = new MultiFormatReader().decode(bitmap);
            return result.getText();
        } catch (Exception e) {
            throw new IOException("No se pudo leer el código QR: " + e.getMessage(), e);
        }
    }
}
