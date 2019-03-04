package com.bikram.utility;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.util.IOUtils;
import com.bikram.beans.AppConfigManager;

@Service
public class FileStorageService {
	@Autowired
	private AppConfigManager configManager;
	private final Path fileStorageLocation;
	private static final String BADGE = "badges";
	private static final String PROFILE_PIC = "profilepics";

	@Autowired
	public FileStorageService(FileStorageProperties fileStorageProperties) {
		this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();

		try {
			Files.createDirectories(this.fileStorageLocation);
		} catch (Exception ex) {
		}
	}

	public String storeFile(MultipartFile file) {

		AWSCredentials credentials = new BasicAWSCredentials(configManager.getAppAccess(),
				configManager.getAppSecret());
		AmazonS3 s3client = AmazonS3ClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion(Regions.US_EAST_2).build();
		String bucketName = configManager.getS3Bucket();

		if (!s3client.doesBucketExist(bucketName)) {
			s3client.createBucket(bucketName);

		}

		try {

			MessageDigest messageDigest = null;
			try {
				messageDigest = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			messageDigest.reset();
			messageDigest.update(IOUtils.toByteArray(file.getInputStream()));
			byte[] resultByte = DigestUtils.md5(file.getInputStream());

			ObjectMetadata meta = new ObjectMetadata();

			String streamMD5 = new String(Base64.encodeBase64(resultByte));
			meta.setContentMD5(streamMD5);
			String newfileName = "";
			if (file.getName().contains("."))
				newfileName = PROFILE_PIC + "/" + configManager.getOrgPrefix() + System.currentTimeMillis()
						+ file.getName().substring(file.getName().indexOf("."), file.getName().length());
			else
				newfileName = PROFILE_PIC + "/" + configManager.getOrgPrefix() + System.currentTimeMillis() + ".jpg";
			if (file.getName().contains("badge"))
				newfileName = BADGE + "/" + configManager.getOrgPrefix() + System.currentTimeMillis() + ".png";

			s3client.putObject(bucketName, newfileName, file.getInputStream(), meta);

			java.util.Date expiration = new java.util.Date();
			long expTimeMillis = expiration.getTime();
			expTimeMillis += 1000 * 60 * 60 * 24;
			expiration.setTime(expTimeMillis);

			// Generate the presigned URL.
			System.out.println("Generating pre-signed URL.");
			GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName,
					newfileName).withMethod(HttpMethod.GET).withExpiration(expiration);
			URL url = s3client.generatePresignedUrl(generatePresignedUrlRequest);

			System.out.println("Pre-Signed URL: " + url.toString());
			return url.toString();
		} catch (IOException ex) {
			return null;
		}
	}

	public Resource loadFileAsResource(String fileName) {
		try {
			Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
			Resource resource = new UrlResource(filePath.toUri());
			if (resource.exists()) {
				return resource;
			} else {
				return null;
			}
		} catch (MalformedURLException ex) {
			return null;
		}
	}
}