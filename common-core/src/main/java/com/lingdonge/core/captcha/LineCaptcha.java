package com.lingdonge.core.captcha;

import com.lingdonge.core.exceptions.IORuntimeException;
import com.lingdonge.core.file.FileUtil;
import com.lingdonge.core.util.RandomUtil;
import com.lingdonge.core.util.StringUtils;
import com.lingdonge.core.image.ImageOperateUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.OutputStream;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 使用干扰线方式生成的图形验证码
 *
 */
public class LineCaptcha implements ICaptcha {

	private static final long serialVersionUID = 8691294460763091089L;
	
	// 图片的宽度。
	private int width = 100;

	// 图片的高度。
	private int height = 37;

	// 验证码字符个数
	private int codeCount = 4;

	// 验证码干扰线数
	private int lineCount = 150;

	// 字体
	private Font font;

	// 每个字符宽度
	private int charWidth;

	// 字符纵坐标，相对底线
	private int charY;

	// 验证码
	private String code;

	// 验证码图片Buffer
	private BufferedImage image;

	// -------------------------------------------------------------------- Constructor start
	/**
	 * 构造，默认4位验证码，150条干扰线
	 * 
	 * @param width 图片宽
	 * @param height 图片高
	 */
	public LineCaptcha(int width, int height) {
		this(width, height, 4, 150);
	}

	/**
	 * 构造
	 * 
	 * @param width 图片宽
	 * @param height 图片高
	 * @param codeCount 字符个数
	 * @param lineCount 干扰线条数
	 */
	public LineCaptcha(int width, int height, int codeCount, int lineCount) {
		this.width = width;
		this.height = height;
		this.codeCount = codeCount;
		this.lineCount = lineCount;
		// 每个字符的宽度
		this.charWidth = width / (codeCount + 2);
		// 字符相对图形验证码框底线的纵坐标位置
		this.charY = height - 4;
		// 字体高度设为验证码高度-2，留边距
		this.font = new Font("Arial", Font.PLAIN, this.height - 2);
	}
	// -------------------------------------------------------------------- Constructor end

	@Override
	public void createCode() {
		// 图像buffer
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();
		// 生成随机数
		final ThreadLocalRandom random = RandomUtil.getRandom();
		// 将图像填充为白色
		g.setColor(ImageOperateUtil.randomColor(random));
		g.fillRect(0, 0, width, height);
		// 创建字体
		g.setFont(this.font);

		// 干扰线
		drawRandomLines(g, random);

		// randomCode记录随机产生的验证码
		final StringBuffer randomCode = new StringBuffer();
		// 随机产生codeCount个字符的验证码。
		String randomCharStr;
		for (int i = 0; i < codeCount; i++) {
			randomCharStr = RandomUtil.randomString(1);
			// 产生随机的颜色值，让输出的每个字符的颜色值都将不同。
			g.setColor(ImageOperateUtil.randomColor(random));
			g.drawString(randomCharStr, (i + 1) * charWidth, charY);
			// 将产生的四个随机数组合在一起。
			randomCode.append(randomCharStr);
		}
		code = randomCode.toString();
	}

	/**
	 * 验证码写出到文件
	 * 
	 * @param path 文件路径
	 * @throws IORuntimeException IO异常
	 */
	public void write(String path) throws IORuntimeException {
		this.write(FileUtil.getOutputStream(path));
	}

	/**
	 * 验证码写出到文件
	 * 
	 * @param file 文件
	 * @throws IORuntimeException IO异常
	 */
	public void write(File file) throws IORuntimeException {
		this.write(FileUtil.getOutputStream(file));
	}

	@Override
	public void write(OutputStream out) throws IORuntimeException {
		ImageOperateUtil.write(this.getImage(), ImageOperateUtil.IMAGE_TYPE_PNG, out);
	}

	/**
	 * 获取验证码图
	 * 
	 * @return 验证码图
	 */
	public BufferedImage getImage() {
		if (null == this.image) {
			createCode();
		}
		return image;
	}

	@Override
	public String getCode() {
		if (null == this.code) {
			createCode();
		}
		return code;
	}

	@Override
	public boolean verify(String userInputCode) {
		if (StringUtils.isNotBlank(userInputCode)) {
			return StringUtils.equalsIgnoreCase(getCode(), userInputCode);
		}
		return false;
	}

	/**
	 * 自定义字体
	 * 
	 * @param font 字体
	 */
	public void setFont(Font font) {
		this.font = font;
	}

	/**
	 * 绘制干扰线
	 * 
	 * @param g {@link Graphics2D}画笔
	 * @param random 随机对象
	 */
	private void drawRandomLines(Graphics2D g, ThreadLocalRandom random) {
		// 干扰线
		for (int i = 0; i < lineCount; i++) {
			int xs = random.nextInt(width);
			int ys = random.nextInt(height);
			int xe = xs + random.nextInt(width / 8);
			int ye = ys + random.nextInt(height / 8);
			g.setColor(ImageOperateUtil.randomColor(random));
			g.drawLine(xs, ys, xe, ye);
		}
	}
}