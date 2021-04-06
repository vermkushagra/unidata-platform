package com.unidata.mdm.backend.service.model.ie;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang3.StringUtils;

/**
 * The Class ZipUtils.
 */
public class ZipUtils {

	/**
	 * Instantiates a new zip utils.
	 */
	private ZipUtils() {

	}

	/**
	 * Zip dir.
	 *
	 * @param path
	 *            the path
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static Path zipDir(final Path path) throws IOException {
		if (!Files.isDirectory(path)) {
			throw new IllegalArgumentException("Path must be a directory.");
		}

		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(path.toString() + ".zip"));

		try (ZipOutputStream out = new ZipOutputStream(bos)) {
			addZipDir(out, path.getFileName(), path);
		}
		return Paths.get(path.toString() + ".zip");
	}

	/**
	 * Unzip dir.
	 *
	 * @param zipFile
	 *            the zip file
	 * @param toDir
	 *            the to dir
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static Path unzipDir(Path zipFile, Path toDir) throws IOException {
		if (!Files.isDirectory(toDir)) {
			Files.createDirectories(toDir);
		}
		Path result =null;
		try (InputStream is = new FileInputStream(zipFile.toFile())) {
			ZipInputStream zipInputStream = new ZipInputStream(is);
			ZipEntry zipEntry = zipInputStream.getNextEntry();
			result = Paths.get(toDir.toString() + File.separator + StringUtils.substringBefore(StringUtils.substringBefore(zipEntry.getName(),"/"),"\\"));			
			while (zipEntry != null) {
				String filePath = toDir.toString() + File.separator + StringUtils.replace(zipEntry.getName(), "\\", File.separator);
				Path parent = Paths.get(filePath).getParent();
				if (!Files.exists(parent)) {
					Files.createDirectories(parent);
				}
				if (!zipEntry.isDirectory()) {
					extractFile(zipInputStream, Paths.get(filePath));
				} else {
					Files.createDirectory(Paths.get(filePath));
				}
				zipInputStream.closeEntry();
				zipEntry = zipInputStream.getNextEntry();
			}
			zipInputStream.close();
		}
		return result;
	}

	/**
	 * Extract file.
	 *
	 * @param zipInputStream
	 *            the zip input stream
	 * @param filePath
	 *            the file path
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private static void extractFile(ZipInputStream zipInputStream, Path filePath) throws IOException {
		try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath.toString()));) {
			byte[] bi = new byte[4096];
			int read = 0;
			while ((read = zipInputStream.read(bi)) != -1) {
				bos.write(bi, 0, read);
			}
		}
	}

	/**
	 * Builds the path.
	 *
	 * @param root
	 *            the root
	 * @param child
	 *            the child
	 * @return the path
	 */
	private static Path buildPath(final Path root, final Path child) {
		if (root == null) {
			return child;
		} else {
			return Paths.get(root.toString(), child.toString());
		}
	}

	/**
	 * Adds folder to zip archive.
	 *
	 * @param out
	 *            the out
	 * @param root
	 *            the root
	 * @param dir
	 *            the dir
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private static void addZipDir(final ZipOutputStream out, final Path root, final Path dir) throws IOException {
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
			for (Path child : stream) {
				Path entry = buildPath(root, child.getFileName());
				if (Files.isDirectory(child)) {
					addZipDir(out, entry, child);
				} else {
					out.putNextEntry(new ZipEntry(entry.toString()));
					Files.copy(child, out);
					out.closeEntry();
				}
			}
		}
	}

}
