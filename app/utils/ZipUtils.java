package utils;

/**
 * Created by octavian.salcianu on 8/22/2016.
 */
import play.Logger;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.*;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class ZipUtils {
    private static final int BUFFER_SIZE = 4096;

    public static void unzip(String zipPath, String destinationPath) throws IOException {
        File destination = new File(destinationPath);
        if (!destination.exists()) {
            destination.mkdir();
        }

        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipPath));
        ZipEntry entry = zipIn.getNextEntry();

        while (entry != null) {
            String filePath = destinationPath + File.separator + entry.getName();
            if (!entry.isDirectory()) {
                File file = new File(filePath);
                file.createNewFile();
                BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file));
                byte[] bytesIn = new byte[BUFFER_SIZE];
                int read = 0;
                while ((read = zipIn.read(bytesIn)) != -1) {
                    outputStream.write(bytesIn, 0, read);
                }
                outputStream.close();
            } else {
                // if the entry is a directory, make the directory

                File dir = new File(filePath);
                dir.mkdir();

            }
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        zipIn.close();
    }

    public static String[] getProjectDetailsFromZip(File zip) throws IOException, XMLStreamException {
        final ZipFile file = new ZipFile(zip);
        try
        {
            final Enumeration<? extends ZipEntry> entries = file.entries();
            while ( entries.hasMoreElements() )
            {
                final ZipEntry entry = entries.nextElement();
                try {
                    if (entry.getName().contains("pom.xml")) {
                        String groupId = null, artifactId = null, projectName = null;
                        InputStream inputStream = file.getInputStream(entry);
                        XMLStreamReader reader = XMLInputFactory.newFactory().createXMLStreamReader(inputStream);
                        while(reader.hasNext()) {
                            reader.next();
                            if (reader.getEventType() == XMLStreamReader.START_ELEMENT) {
                                if(reader.getLocalName().equals("groupId")) {
                                    groupId = reader.getElementText();
                                }
                                if(reader.getLocalName().equals("artifactId")) {
                                    artifactId = reader.getElementText();
                                }
                                if(reader.getLocalName().equals("name")) {
                                    projectName = reader.getElementText();
                                }
                            }
                        }
                        return new String[] { groupId + ":" + artifactId, projectName };
                    }
                } catch (Exception e) {
                    Logger.info("ERROR: " + e.getMessage());
                    return null;
                }
            }
        }
        finally
        {
            file.close();
        }
        return null;
    }

    public static boolean existsInZip(File zip, String fileName) throws IOException {
        final ZipFile file = new ZipFile(zip);
        try
        {
            final Enumeration<? extends ZipEntry> entries = file.entries();
            while ( entries.hasMoreElements() )
            {
                final ZipEntry entry = entries.nextElement();
                if(entry.getName().contains(fileName)) {
                    return true;
                }
                readInputStream( file.getInputStream( entry ) );
            }
        }
        finally
        {
            file.close();
        }
        return false;
    }

    private static int readInputStream( final InputStream is ) throws IOException {
        final byte[] buf = new byte[ 8192 ];
        int read = 0;
        int cntRead;
        while ( ( cntRead = is.read( buf, 0, buf.length ) ) >=0  )
        {
            read += cntRead;
        }
        return read;
    }
}
