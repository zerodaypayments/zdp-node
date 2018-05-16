package io.zdp.node.web.api.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import net.glxn.qrgen.QRCode;
import net.glxn.qrgen.image.ImageType;

@Controller
public class QRCodeAction {

	private static final int MAX_IMAGE_SIZE = 2600;

	@RequestMapping(path = "/api/address/qr/{size}/{address}.jpg")
	public void generate(HttpServletResponse resp, @PathVariable String address, @PathVariable int size) throws IOException {

		if (address.startsWith("zdp") && size <= MAX_IMAGE_SIZE) {

			ByteArrayOutputStream stream = QRCode.from(address).withSize(size, size).to(ImageType.JPG).stream();

			byte[] data = stream.toByteArray();

			resp.setHeader("Pragma", "public");
			resp.setHeader("Cache-Control", "max-age=86400");
			resp.setDateHeader("Expires", DateUtils.addMonths(new Date(), 12).getTime());

			resp.setContentType("image/jpeg");
			resp.setContentLength(data.length);

			IOUtils.write(data, resp.getOutputStream());

		}

	}

}
