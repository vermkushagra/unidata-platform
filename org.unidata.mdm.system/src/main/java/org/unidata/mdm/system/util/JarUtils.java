/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 * 
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package org.unidata.mdm.system.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.apache.commons.lang3.StringUtils;

/**
 * Utility class for JAR files.
 * 
 * @author ilya.bykov
 */
public class JarUtils {
	public static final String UNIDATA_INTEGRATION = "unidata-integration";

	/**
	 * Default constructor.
	 */
	private JarUtils() {
		super();
	}

	/**
	 * Find classes in jar.
	 *
	 * @param <T>
	 *            the generic type
	 * @param baseInterface
	 *            the base interface
	 * @param filePath
	 *            the file path
	 * @return the list
	 * @throws FileNotFoundException
	 *             the file not found exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws InstantiationException
	 *             the instantiation exception
	 * @throws IllegalAccessException
	 *             the illegal access exception
	 * @throws ClassNotFoundException
	 *             the class not found exception
	 */
	@SuppressWarnings("unchecked")
	public static synchronized <T> List<Class<T>> findClassesInJar(final Class<T> baseInterface, final String filePath)
			throws FileNotFoundException, IOException, InstantiationException, IllegalAccessException,
			ClassNotFoundException {
		final List<Class<T>> classesTobeReturned = new ArrayList<Class<T>>();
		if (!StringUtils.isBlank(filePath)) {
			final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			final URL url = new URL("jar:file:" + filePath + "!/");
			final URLClassLoader ucl = new URLClassLoader(new URL[] { url }, classLoader);
			final JarInputStream jarFile = new JarInputStream(new FileInputStream(filePath));
			try {
				JarEntry jarEntry;
				while (true) {
					jarEntry = jarFile.getNextJarEntry();
					if (jarEntry == null)
						break;
					// check only compiled classes
					if (jarEntry.getName().endsWith(".class")) {
						String classname = jarEntry.getName().replaceAll("/", "\\.");
						classname = classname.substring(0, classname.length() - 6);
						// skip sub classes
						final Class<?> myLoadedClass = Class.forName(classname, true, ucl);
						if (!classname.contains("$")) {							
							if (baseInterface.isAssignableFrom(myLoadedClass)) {
								classesTobeReturned.add((Class<T>) myLoadedClass);
							}

						}
					}

				}
			} finally {
				jarFile.close();
				ucl.close();
			}
		}
		return classesTobeReturned;
	}

	/**
	 * Save file to lib folder.
	 *
	 * @param attachment
	 *            the attachment
	 * @return the java.nio.file. path
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static final java.nio.file.Path saveFileToLibFolder(String fileName, InputStream in) throws IOException{
		//String fileName = attachment.getContentDisposition().getParameter("filename");
		Files.createDirectories(Paths.get(System.getProperty("catalina.base") + File.separator + UNIDATA_INTEGRATION+ File.separator + "custom_cf"));
		java.nio.file.Path path = Paths
				.get(System.getProperty("catalina.base") + File.separator + UNIDATA_INTEGRATION+ File.separator + "custom_cf" + File.separator + fileName);
		Files.deleteIfExists(path);
		//InputStream in = attachment.getObject(InputStream.class);
		Files.copy(in, path);
		return path;
	}

	/**
	 * Validate file name.
	 *
	 * @param attachment
	 *            the attachment
	 * @return true, if successful
	 */
	public static final boolean validateFileName(String fileName) {
		//String fileName = attachment.getContentDisposition().getParameter("filename");
		if (!fileName.endsWith(".jar")) {
			return false;
		} else {
			return true;
		}
	}
}