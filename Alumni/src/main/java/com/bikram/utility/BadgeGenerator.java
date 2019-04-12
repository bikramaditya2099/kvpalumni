package com.bikram.utility;



import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.util.IOUtils;
import com.bikram.beans.AppConfigManager;
import com.bikram.dao.beans.UserBean;
import com.bikram.exception.ApplicationEnum;
import com.bikram.exception.KvpalException;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
@Component
public class BadgeGenerator {
	@Autowired
	private AppConfigManager manager;
	public  MultipartFile createImage(UserBean bean,HttpServletRequest request) throws KvpalException
    {
          MultipartFile multipartFile=null;
		try {
			int width = 500;
			  int height = 300;
			  int imageType = BufferedImage.TYPE_4BYTE_ABGR;
			  
			  BufferedImage img=ImageIO.read(new URL(bean.getImageLocation()));
			  BufferedImage img1=ImageIO.read(new URL(manager.getLogo()));
			 
			  BufferedImage imageBuffer = new BufferedImage(width,height,imageType);
			  Graphics2D graphics = imageBuffer.createGraphics();
			 
			  graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
			              RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
			 
			  // true means to repeat pattern
			 // GradientPaint gradientBG = new GradientPaint(0, 0, Color.red, 175,
			       //       175, Color.yellow,true);     
			 
			 // graphics.setPaint();
			  graphics.fillRect(0, 0, imageBuffer.getWidth(), imageBuffer.getHeight());
			  graphics.setFont(new Font(Font.SANS_SERIF,Font.BOLD,17));
			 
			 
			  GradientPaint gradientText = new GradientPaint(0, 0, Color.BLACK,
			              100, 20, Color.BLACK,true);   
			  graphics.setPaint(gradientText);
			 
			  // graphics.rotate(0.25);
			 // graphics.drawString("KENDRIYA VIDYALAYA PURI ALUMNI ASSOCIATION",10,40);
			  graphics.drawImage(img1, 10, 4,350,70, new ImageObserver() {
				
				@Override
				public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
					// TODO Auto-generated method stub
					return false;
				}
			});
			  graphics.drawImage(img, 360, 60,120,120, new ImageObserver() {
				
				@Override
				public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
					// TODO Auto-generated method stub
					return false;
				}
			});
			  
			  graphics.drawString("NAME : "+bean.getFirstName().toUpperCase()+" "+bean.getLastName().toUpperCase(),10,120);
			  graphics.drawString("CLASS : "+bean.getBatch(),10,145);
			  graphics.drawString("PASSOUT : "+bean.getPassout_year(),10,170);
			  graphics.drawString("GENDER : "+bean.getGender().toUpperCase(),10,195);
			  graphics.drawString("MOBILE : "+bean.getMobile(),10,220);
			  String url=request.getServerName();
			  int port=request.getServerPort();
			  String entryUrl=url+":"+port+"/entry/"+bean.getUid();
			  System.out.println(url+":"+port+"/");
			  Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<>();
			hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
			QRCodeWriter qrCodeWriter = new QRCodeWriter();
			BitMatrix byteMatrix = qrCodeWriter.encode(entryUrl, BarcodeFormat.QR_CODE, 70, 70, hintMap);
			// Make the BufferedImage that are to hold the QRCode
			int matrixWidth = byteMatrix.getWidth();
			BufferedImage image = new BufferedImage(matrixWidth, matrixWidth, BufferedImage.TYPE_INT_RGB);
			image.createGraphics();

			Graphics2D graphics1 = (Graphics2D) image.getGraphics();
			graphics1.setColor(Color.WHITE);
			graphics1.fillRect(0, 0, matrixWidth, matrixWidth);
			// Paint and save the image using the ByteMatrix
			graphics1.setColor(Color.BLACK);

			for (int i = 0; i < matrixWidth; i++) {
				for (int j = 0; j < matrixWidth; j++) {
					if (byteMatrix.get(i, j)) {
						graphics1.fillRect(i, j, 1, 1);
					}
				}
			}
			
			 graphics.drawImage(image, 220, 220,70,70, new ImageObserver() {
				
				@Override
				public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
					// TODO Auto-generated method stub
					return false;
				}
			});
			String fileLoc=manager.getBadgeTempLoc()+"\\"+System.currentTimeMillis()+".png";
			   File file = new File(fileLoc);
			   
			   ImageIO.write(imageBuffer, "png", file);
			   
			   FileInputStream input = new FileInputStream(file);
			   
			   multipartFile = new MockMultipartFile ("badge",
			           file.getName(), "image/png", IOUtils.toByteArray(input));
		} catch (IOException | WriterException e ) {
			throw new KvpalException(ApplicationEnum.ERROR_UPLOADING_IMAGE);
		} 
         
          return multipartFile;
}
	
	public static void main(String[] args) throws IOException, WriterException {
		}
}
