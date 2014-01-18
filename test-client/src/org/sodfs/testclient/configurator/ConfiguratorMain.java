package org.sodfs.testclient.configurator;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.SAXException;

/**
 *
 * @author Roman Kierzkowski
 */

public class ConfiguratorMain {
    public static final String SORPA_CONFIG_ELEMENT = "sorpa-config";
    public static final String SORPA_CONFIG_PC_ATTRIBUTE = "pc";
    public static final String SORPA_CONFIG_K_ATTRIBUTE = "k";
    public static final String SORPA_CONFIG_TTL_ATTRIBUTE = "ttl";
    public static final String SORPA_CONFIG_MIN_NOR_ATTRIBUTE = "min-nor";    
    public static final String SORPA_CONFIG_PIN_ATTRIBUTE = "pin";
    public static final String SORPA_CONFIG_RF_ATTRIBUTE = "rf";
    public static final String SORPA_CONFIG_DRF_ATTRIBUTE = "drf";
    public static final String SORPA_CONFIG_AF_ATTRIBUTE = "af";
    public static final String SORPA_CONFIG_MF_ATTRIBUTE = "mf";
    
        /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length == 10) {
            try {
                String path = args[0];
                /* TYPE CHECK */
                Double.parseDouble(args[1]);
                Double.parseDouble(args[2]);                
                Long.parseLong(args[3]);
                Long.parseLong(args[4]);
                Integer.parseInt(args[5]);
                Float.parseFloat(args[6]);
                Float.parseFloat(args[7]);
                Float.parseFloat(args[8]);
                Float.parseFloat(args[9]);
                File file = new File(path);
                Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
                Element sorpaConfig = (Element) doc.getElementsByTagName(SORPA_CONFIG_ELEMENT).item(0);
                sorpaConfig.setAttribute(SORPA_CONFIG_PC_ATTRIBUTE, args[1]);
                sorpaConfig.setAttribute(SORPA_CONFIG_K_ATTRIBUTE, args[2]);
                sorpaConfig.setAttribute(SORPA_CONFIG_TTL_ATTRIBUTE, args[3]);
                sorpaConfig.setAttribute(SORPA_CONFIG_MIN_NOR_ATTRIBUTE, args[4]);
                sorpaConfig.setAttribute(SORPA_CONFIG_PIN_ATTRIBUTE, args[5]);
                sorpaConfig.setAttribute(SORPA_CONFIG_RF_ATTRIBUTE, args[6]);
                sorpaConfig.setAttribute(SORPA_CONFIG_DRF_ATTRIBUTE, args[7]);
                sorpaConfig.setAttribute(SORPA_CONFIG_AF_ATTRIBUTE, args[8]);
                sorpaConfig.setAttribute(SORPA_CONFIG_MF_ATTRIBUTE, args[9]);
                DOMImplementationLS di = (DOMImplementationLS) doc.getImplementation();
                LSSerializer serializer = di.createLSSerializer();
                serializer.writeToURI(doc, "file:" + path);               
            } catch (ParserConfigurationException ex) {
                ex.printStackTrace();
            } catch (SAXException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            System.err.println("usage: sodfsconfigurator <path> <pc> <k> <TTL> <pinTime> <minNOR> <RF> <DRF> <AF> <MF>");
        }        
    }
}