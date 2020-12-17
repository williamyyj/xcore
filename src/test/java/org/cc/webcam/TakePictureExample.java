package org.cc.webcam;

import com.github.sarxos.webcam.Webcam;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class TakePictureExample {

	public static void main(String[] args) throws IOException {

		List<Webcam> items = Webcam.getWebcams();
		if(items!=null){
			for(Webcam item:items){
				System.out.println(item.getName());
			}
		}
		Webcam webcam = Webcam.getWebcamByName("Logitech HD Webcam C615 1");
		if(webcam!=null){
			webcam.open();

			// get image
			BufferedImage image = webcam.getImage();

			// save image to PNG file
			ImageIO.write(image, "PNG", new File("d:\\test.png"));
		}

	}
}