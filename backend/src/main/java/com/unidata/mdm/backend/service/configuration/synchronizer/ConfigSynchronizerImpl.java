package com.unidata.mdm.backend.service.configuration.synchronizer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.SystemRuntimeException;
import com.unidata.mdm.backend.service.configuration.ConfigurationHolder;
import com.unidata.mdm.backend.util.JarUtils;

/**
 * The Class ConfigSynchronizerImpl.
 * @author ilya.bykov
 */
@Component
public class ConfigSynchronizerImpl implements ConfigSynchronizer {

	/** The hazelcast instance. */
	@Autowired
	private HazelcastInstance hazelcastInstance;

	/** The fingerprint. */
	private IMap<String, ConfigFingerprintDTO> configFingerprint;

	/** The exclusions. */
	private List<String> EXCLUSIONS = new ArrayList<>();

	/** The to check. */
	private List<String> TO_CHECK = new ArrayList<>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.unidata.mdm.backend.service.configuration.AfterContextRefresh#
	 * afterContextRefresh()
	 */
	@Override
	public void afterContextRefresh() {
		configFingerprint = hazelcastInstance.getMap("configFingerprint");
		String integrationPath = System.getProperty(ConfigurationHolder.CATALINA_PATH_PROPERTY) + File.separator
				+ JarUtils.UNIDATA_INTEGRATION;
		String confPath = System.getProperty(ConfigurationHolder.CONFIGURATION_PATH_PROPERTY);
		EXCLUSIONS.add(File.separator + "custom_cf");
		EXCLUSIONS.add(File.separator + ConfigurationHolder.PROPERTIES_FILENAME);
		TO_CHECK.add(integrationPath);
		TO_CHECK.add(confPath);
		validate();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.unidata.mdm.backend.service.configuration.synchronizer.
	 * ConfigSynchronizer#validate()
	 */
	@Override
	public void validate() {
		try {
			Set<ConfigFingerprintDTO> localValues = collectValuesToCheck();
			Set<ConfigFingerprintDTO> distributedValues = new HashSet<>(configFingerprint.values());
			if (distributedValues == null || distributedValues.size() == 0) {
				for (ConfigFingerprintDTO localValue : localValues) {
					configFingerprint.put(localValue.getPath(), localValue);
				}
				return;
			}
			Set<ConfigFingerprintDTO> union = new HashSet<>(localValues);
			union.addAll(distributedValues);

			Set<ConfigFingerprintDTO> intersection = new HashSet<>(localValues);
			intersection.retainAll(distributedValues);

			Set<ConfigFingerprintDTO> symmetricDifference = new HashSet<>(union);
			symmetricDifference.removeAll(intersection);
			if (symmetricDifference.size() != 0) {
				throw new SystemRuntimeException(
						"Unable to start node. Some config files not in sync between nodes in cluster. Problem files are: {}",
						ExceptionId.EX_SYSTEM_CONFIGURATION_NOT_IN_SYNC, symmetricDifference.toString());
			}
		} catch (NoSuchAlgorithmException | IOException e) {
			throw new SystemRuntimeException("Unable to validate configuration files!",
					ExceptionId.EX_SYSTEM_CONFIGURATION_UNABLE_TO_VERIFY, e);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.unidata.mdm.backend.service.configuration.synchronizer.
	 * ConfigSynchronizer#synchronize()
	 */
	@Override
	public void synchronize() {
	}



	/**
	 * Collect values to check.
	 *
	 * @return the sets the
	 * @throws FileNotFoundException
	 *             the file not found exception
	 * @throws NoSuchAlgorithmException
	 *             the no such algorithm exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private Set<ConfigFingerprintDTO> collectValuesToCheck()
			throws FileNotFoundException, NoSuchAlgorithmException, IOException {
		Set<ConfigFingerprintDTO> result = new HashSet<ConfigFingerprintDTO>();
		List<String> pathsToCheck = new ArrayList<>();
		for (String folder : TO_CHECK) {
			pathsToCheck
					.addAll(fileList(folder).stream().filter(s -> !checkExclusions(s)).collect(Collectors.toList()));
		}
		for (String path : pathsToCheck) {
			ConfigFingerprintDTO fingerprint = new ConfigFingerprintDTO();
			fingerprint.setHash(hashFile(new File(path), "MD5"));
			fingerprint.setPath(path);
			result.add(fingerprint);
		}
		return result;

	}

	/**
	 * Check exclusions.
	 *
	 * @param toCheck
	 *            the to check
	 * @return true, if successful
	 */
	private boolean checkExclusions(String toCheck) {
		for (String excl : EXCLUSIONS) {
			if (toCheck.contains(excl)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * File list.
	 *
	 * @param directory
	 *            the directory
	 * @return the list
	 */
	private static List<String> fileList(String directory) {
		List<String> fileNames = new ArrayList<>();
		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(directory))) {
			for (Path path : directoryStream) {
				fileNames.add(path.toString());
			}
		} catch (IOException ex) {
		}
		return fileNames;
	}

	/**
	 * Hash file.
	 *
	 * @param file
	 *            the file
	 * @param algorithm
	 *            the algorithm
	 * @return the string
	 * @throws FileNotFoundException
	 *             the file not found exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws NoSuchAlgorithmException
	 *             the no such algorithm exception
	 */
	private static String hashFile(File file, String algorithm)
			throws FileNotFoundException, IOException, NoSuchAlgorithmException {
		try (FileInputStream inputStream = new FileInputStream(file)) {
			MessageDigest digest = MessageDigest.getInstance(algorithm);

			byte[] bytesBuffer = new byte[1024];
			int bytesRead = -1;

			while ((bytesRead = inputStream.read(bytesBuffer)) != -1) {
				digest.update(bytesBuffer, 0, bytesRead);
			}

			byte[] hashedBytes = digest.digest();

			return convertByteArrayToHexString(hashedBytes);
		}
	}

	/**
	 * Convert byte array to hex string.
	 *
	 * @param arrayBytes
	 *            the array bytes
	 * @return the string
	 */
	private static String convertByteArrayToHexString(byte[] arrayBytes) {
		StringBuffer stringBuffer = new StringBuffer();
		for (int i = 0; i < arrayBytes.length; i++) {
			stringBuffer.append(Integer.toString((arrayBytes[i] & 0xff) + 0x100, 16).substring(1));
		}
		return stringBuffer.toString();
	}

}