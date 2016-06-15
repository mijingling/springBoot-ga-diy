package com.qz.controller;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Hashtable;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.qz.util.TOTPUtils;

@RestController
public class FirstController {
	private int qrcodeWidth = 400;
	private int qrcodeHeight = 400;
	private String hostLabel = "qian360.com";
	private static String secret = "";

	/**
	 * 生成QRBarcode(Quick Response Barcode)
	 */
	@RequestMapping(value = "/")
	public String home(HttpServletResponse response) throws IOException {

		response.setHeader("Cache-Control", "no-store");
		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", 0);
		response.setContentType("image/png");
		String newSecret = TOTPUtils.generateSecret();
		String data = getQRBarcodeURL("test", hostLabel, newSecret);
		secret = newSecret;
		System.out.println("newSecret:" + newSecret);
		System.out.println(data);

		BitMatrix matrix = null;
		com.google.zxing.Writer writer = new MultiFormatWriter();
		try {
			Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>(2);
			hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
			matrix = writer.encode(data, com.google.zxing.BarcodeFormat.QR_CODE, qrcodeWidth, qrcodeHeight, hints);
		} catch (com.google.zxing.WriterException e) {
			e.printStackTrace();
		}

		try {
			MatrixToImageWriter.writeToStream(matrix, "PNG", response.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 校验code
	 */
	@RequestMapping(value = "/{code}")
	public String verifyCode(@PathVariable long code) {
		System.out.println("ck-secret:" + secret);
		String ckinfo = "";
		try {
			boolean ck = TOTPUtils.checkCode(secret, code);
			if (ck) {
				ckinfo = "校验码正确^.^";
			} else {
				ckinfo = "校验码错误-_-";
			}
		} catch (InvalidKeyException | NoSuchAlgorithmException e) {
			ckinfo = "校验error-_-||";
			e.printStackTrace();
		}
		return ckinfo;
	}

	// 生成QRBarcodeURL
	private String getQRBarcodeURL(String user, String host, String secret) {
		String format = "otpauth://totp/%s@%s?secret=%s";
		return String.format(format, user, host, secret);
	}

}
