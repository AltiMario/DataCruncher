/*
 * DataCruncher
 * Copyright (c) Mario Altimari. All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.datacruncher.spring;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class CaptchaController implements Controller {

	private static int ImageWidth = 280;

	private static int ImageHeight = 80;

	private static Color backgroundColor = Color.WHITE;

	private static Color textColor = Color.black;

	// text properties
	private static int genTextSize = 4;

	private static String textFont = "Courier";

	private static int fontType = Font.BOLD;

	private final Random generator = new Random();

	private final static String SIMPLE_CAPCHA_SESSION_KEY = "simple.capcha.session.key";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.web.servlet.mvc.Controller#handleRequest(javax.servlet
	 * .http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		getCaptchaImage(request,response);
		return null;
	}

	public static void setCaptchaParams(int height, Color bgColor,
			Color txtColor, Color lColor, int textSize, String font, int ftType) {
		ImageWidth = 5 * height * (textSize / 6);
		ImageHeight = height;
		backgroundColor = bgColor;
		textColor = txtColor;
		genTextSize = textSize;
		textFont = font;
		fontType = ftType;
	}

	private static char[] charset = { '1', '2', '3', '4', '6', '7', '8', '9',
			'0', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
			'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y',
			'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
			'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y',
			'Z' };

	public void getCaptchaImage(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String genText = "";
		int charsetSize = charset.length;
		for (int i = 0; i < genTextSize; i++) {
			genText += charset[generator.nextInt(charsetSize)];
		}

		// this key can be read from any controller to check whether user
		// is a computer or human..
		request.getSession().setAttribute(SIMPLE_CAPCHA_SESSION_KEY, genText);

		// Image Generation
		BufferedImage buffImage = new BufferedImage(ImageWidth + 10,
				ImageHeight, BufferedImage.TYPE_BYTE_INDEXED);

		Graphics2D graphics = buffImage.createGraphics();
		// setting the image background and color
		graphics.setBackground(backgroundColor);
		graphics.setColor(backgroundColor);
		// fills the rectangle with the given color
		graphics.fillRect(0, 0, buffImage.getWidth(), buffImage.getHeight());

		graphics.setColor(textColor);

		// Text formatting
		TextLayout textFormat = new TextLayout(genText, new Font(textFont,
				fontType, 70), new FontRenderContext(null, true, false));
		textFormat.draw(graphics, 4, 60);
		int w = buffImage.getWidth();
		int h = buffImage.getHeight();

		// disordering the text in the image
		shear(graphics, w, h, backgroundColor);
		/*
		 * this.drawThickLine(graphics, 0, generator.nextInt(ImageHeight) + 1,
		 * ImageWidth, generator.nextInt(ImageHeight) + 1, 4, lineColor);
		 */

		// setting the image to the output stream of response with the
		// appropriate content type
		response.setContentType("image/jpeg");
		ImageIO.write(buffImage,"jpeg",response.getOutputStream());
	}

	private void shear(Graphics grashic, int width, int height, Color color) {

		shearX(grashic, width, height, color);
		shearY(grashic, width, height, color);
	}

	private void shearX(Graphics grashic, int width, int height, Color color) {

		int period = generator.nextInt(10) + 5;

		int frames = 15;
		int phase = generator.nextInt(5) + 2;

		for (int i = 0; i < height; i++) {
			double d = (period >> 1)
					* Math.sin((double) i / (double) period
							+ (6.2831853071795862D * phase) / frames);
			grashic.copyArea(0, i, width, 1, (int) d, 0);
			grashic.setColor(color);
			grashic.drawLine((int) d, i, 0, i);
			grashic.drawLine((int) d + width, i, width, i);
		}
	}

	/**
	 * disorders the text in the image
	 * 
	 * @param grashic
	 * @param width
	 * @param height
	 * @param color
	 */
	private void shearY(Graphics grashic, int width, int height, Color color) {

		int period = generator.nextInt(30) + 10; // 50;

		int frames = 15;
		int phase = 7;
		for (int i = 0; i < width; i++) {
			double d = (period >> 1)
					* Math.sin((double) i / (double) period
							+ (6.2831853071795862D * phase) / frames);
			grashic.copyArea(i, 0, 1, height, 0, (int) d);
			grashic.setColor(color);
			grashic.drawLine(i, (int) d, i, 0);
			grashic.drawLine(i, (int) d + height, i, height);
		}
	}
}
