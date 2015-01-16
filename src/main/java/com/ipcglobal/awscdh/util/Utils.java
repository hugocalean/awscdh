package com.ipcglobal.awscdh.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.auth.PropertiesFileCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;

public class Utils {
	private static final Log log = LogFactory.getLog(Utils.class);

	public static AWSCredentialsProvider initCredentials() {
		// Get credentials from IMDS. If unsuccessful, get them from the
		// credential profiles file.
		AWSCredentialsProvider credentialsProvider = null;
		try {
			credentialsProvider = new DefaultAWSCredentialsProviderChain();
			// Verify we can fetch credentials from the provider
			credentialsProvider.getCredentials();
			//log.info("Obtained credentials from DefaultAWSCredentialsProviderChain.");
		} catch (AmazonClientException e) {
			log.error("Unable to obtain credentials from DefaultAWSCredentialsProviderChain", e);
		}
		return credentialsProvider;
	}

	/**
	 * Inits the credentials.
	 *
	 * @param credentialsFileName
	 *            the credentials file name
	 * @return the AWS credentials provider
	 */
	public static AWSCredentialsProvider initCredentials(String credentialsFileName) {
		// Get credentials from IMDS. If unsuccessful, get them from the
		// credential profiles file.
		AWSCredentialsProvider credentialsProvider = null;
		try {
			credentialsProvider = new InstanceProfileCredentialsProvider();
			// Verify we can fetch credentials from the provider
			credentialsProvider.getCredentials();
			log.info("Obtained credentials from the IMDS.");
		} catch (AmazonClientException e) {
			log.warn("Unable to obtain credentials from the IMDS, trying credentialsFileName");
			// If the credentialsFileName contains a path, assume it contains
			// the entire drive:/path/name.ext
			// this is necessary when running as a service on Windows
			String credentialsPathNameExt = null;
			// safest to check for both
			if (credentialsFileName.indexOf("/") > -1 || credentialsFileName.indexOf("\\") > -1) credentialsPathNameExt = credentialsFileName;
			else
				credentialsPathNameExt = System.getProperty("user.home") + System.getProperty("file.separator")
						+ ".aws" + System.getProperty("file.separator") + credentialsFileName;
			credentialsProvider = new PropertiesFileCredentialsProvider(credentialsPathNameExt);
			log.info("Obtained credentials from the credentialsFileName file.");
		}
		return credentialsProvider;
	}
	
	
	/**
	 * Inits the profile credentials provider.
	 *
	 * @param profileName the profile name
	 * @return the AWS credentials provider
	 * @throws Exception the exception
	 */
	public static AWSCredentialsProvider initProfileCredentialsProvider(String profileName) throws Exception {
		// Get credentials from the ~/.aws/credentials file
		AWSCredentialsProvider credentialsProvider = null;
		try {
			credentialsProvider = new ProfileCredentialsProvider( profileName );
			// Verify we can fetch credentials from the provider
			credentialsProvider.getCredentials();
		} catch (AmazonClientException e) {
			log.error("Unable to obtain credentials from ProfileCredentialsProvider, profileName=" + profileName );
			throw e;
		}
		return credentialsProvider;
	}	

	
	public static String convertMSecsToHMmSs(long msecs) {
		return convertSecsToHMmSs(msecs/1000L);
	}
	
	
	public static String convertSecsToHMmSs(long seconds) {
	    long s = seconds % 60;
	    long m = (seconds / 60) % 60;
	    long h = (seconds / (60 * 60)) % 24;
	    return String.format("%02d:%02d:%02d", h,m,s);
	}
	
}
