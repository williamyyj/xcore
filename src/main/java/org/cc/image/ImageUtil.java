package org.cc.image;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA. User: Ryan Date: 2011/1/31 Time: 上午 11:01:35
 * 用來取代hyweb util的 image util 20200923 解決CMYK 要先轉 RGB
 */

public class ImageUtil {

	/**
	 *
	 * @param src         檔案來源路徑
	 * @param widthratio  縮圖比例 width
	 * @param heightratio 縮圖比例 height
	 * @param dest        縮圖產出路徑
	 * @throws IOException
	 */
	public static void scale(String src, double widthratio, double heightratio, String dest) throws IOException {
		System.out.println("===== src : " + src);
		// BufferedImage bsrc = ImageIO.read(new File(src));
		BufferedImage bsrc = readImage(new File(src));
		int width = (int) (bsrc.getWidth() * widthratio);
		int height = (int) (bsrc.getHeight() * heightratio);
		BufferedImage bdest = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bdest.createGraphics();
		AffineTransform at = AffineTransform.getScaleInstance(widthratio, heightratio);
		g.drawRenderedImage(bsrc, at);
		System.out.println("縮圖路徑=====> " + dest);
		ImageIO.write(bdest, "JPG", new File(dest));
	}

	public static BufferedImage readImage(File file) throws IOException {

		return readImage(ImageIO.createImageInputStream(file));
	}

	public static BufferedImage readImage(InputStream stream) throws IOException {

		return readImage(ImageIO.createImageInputStream(stream));
	}

	public static BufferedImage readImage(ImageInputStream input) throws IOException {
		Iterator<?> readers = ImageIO.getImageReaders(input);
		if (readers == null || !readers.hasNext()) {
			return null;
		}

		ImageReader reader = (ImageReader) readers.next();
		reader.setInput(input);

		BufferedImage image;
		try {
			// 嘗試讀取圖片 (包括顏色的轉換).
			image = reader.read(0); // RGB

		} catch (IIOException e) {
			// 讀取Raster (沒有顏色的轉換).
			Raster raster = reader.readRaster(0, null);// CMYK
			image = createJPEG4(raster);
		}

		return image;
	}

	private static BufferedImage createJPEG4(Raster raster) {
		int w = raster.getWidth();
		int h = raster.getHeight();
		byte[] rgb = new byte[w * h * 3];

		// 彩色空間轉換
		float[] Y = raster.getSamples(0, 0, w, h, 0, (float[]) null);
		float[] Cb = raster.getSamples(0, 0, w, h, 1, (float[]) null);
		float[] Cr = raster.getSamples(0, 0, w, h, 2, (float[]) null);
		float[] K = raster.getSamples(0, 0, w, h, 3, (float[]) null);

		for (int i = 0, imax = Y.length, base = 0; i < imax; i++, base += 3) {
			float k = 220 - K[i], y = 255 - Y[i], cb = 255 - Cb[i], cr = 255 - Cr[i];

			double val = y + 1.402 * (cr - 128) - k;
			val = (val - 128) * .65f + 128;
			rgb[base] = val < 0.0 ? (byte) 0 : val > 255.0 ? (byte) 0xff : (byte) (val + 0.5);

			val = y - 0.34414 * (cb - 128) - 0.71414 * (cr - 128) - k;
			val = (val - 128) * .65f + 128;
			rgb[base + 1] = val < 0.0 ? (byte) 0 : val > 255.0 ? (byte) 0xff : (byte) (val + 0.5);

			val = y + 1.772 * (cb - 128) - k;
			val = (val - 128) * .65f + 128;
			rgb[base + 2] = val < 0.0 ? (byte) 0 : val > 255.0 ? (byte) 0xff : (byte) (val + 0.5);
		}

		raster = Raster.createInterleavedRaster(new DataBufferByte(rgb, rgb.length), w, h, w * 3, 3,
				new int[] { 0, 1, 2 }, null);

		ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
		ColorModel cm = new ComponentColorModel(cs, false, true, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
		return new BufferedImage(cm, (WritableRaster) raster, true, null);
	}

}

