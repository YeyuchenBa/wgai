package org.jeecg.modules.demo.train.util;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.jeecg.modules.demo.easy.entity.TabEasyPic;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.List;

/**
 * @author wggg
 * @date 2024/12/20 17:13
 */
public class writeXml {

        public static void main(String[] args) {
            try {
                // 初始化 XML 文档构建器
                DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

                // 创建根元素 <annotation>
                Document doc = docBuilder.newDocument();
                Element annotation = doc.createElement("annotation");
                doc.appendChild(annotation);

                // 创建 <folder> 和 <filename>
                Element folder = doc.createElement("folder");
                folder.appendChild(doc.createTextNode("images"));
                annotation.appendChild(folder);

                Element filename = doc.createElement("filename");
                filename.appendChild(doc.createTextNode("image_001.jpg"));
                annotation.appendChild(filename);

                Element path = doc.createElement("path");
                path.appendChild(doc.createTextNode("/path/to/image/image_001.jpg"));
                annotation.appendChild(path);

                // <source> 元素
                Element source = doc.createElement("source");
                annotation.appendChild(source);
                Element database = doc.createElement("database");
                database.appendChild(doc.createTextNode("Unknown"));
                source.appendChild(database);

                // <size> 元素
                Element size = doc.createElement("size");
                annotation.appendChild(size);
                Element width = doc.createElement("width");
                width.appendChild(doc.createTextNode("800"));
                size.appendChild(width);
                Element height = doc.createElement("height");
                height.appendChild(doc.createTextNode("600"));
                size.appendChild(height);
                Element depth = doc.createElement("depth");
                depth.appendChild(doc.createTextNode("3"));
                size.appendChild(depth);

                // <segmented> 元素
                Element segmented = doc.createElement("segmented");
                segmented.appendChild(doc.createTextNode("0"));
                annotation.appendChild(segmented);

                // <object> 元素

                for (int i = 0; i <2 ; i++) {
                    Element object = doc.createElement("object");
                    annotation.appendChild(object);

                    Element objectName = doc.createElement("name");
                    objectName.appendChild(doc.createTextNode("dog"));
                    object.appendChild(objectName);

                    Element pose = doc.createElement("pose");
                    pose.appendChild(doc.createTextNode("Unspecified"));
                    object.appendChild(pose);

                    Element truncated = doc.createElement("truncated");
                    truncated.appendChild(doc.createTextNode("0"));
                    object.appendChild(truncated);

                    Element difficult = doc.createElement("difficult");
                    difficult.appendChild(doc.createTextNode("0"));
                    object.appendChild(difficult);

                    // <bndbox> 元素
                    Element bndbox = doc.createElement("bndbox");
                    object.appendChild(bndbox);
                    Element xmin = doc.createElement("xmin");
                    xmin.appendChild(doc.createTextNode("120"));
                    bndbox.appendChild(xmin);
                    Element ymin = doc.createElement("ymin");
                    ymin.appendChild(doc.createTextNode("100"));
                    bndbox.appendChild(ymin);
                    Element xmax = doc.createElement("xmax");
                    xmax.appendChild(doc.createTextNode("200"));
                    bndbox.appendChild(xmax);
                    Element ymax = doc.createElement("ymax");
                    ymax.appendChild(doc.createTextNode("250"));
                    bndbox.appendChild(ymax);
                }


                // 将文档内容写入文件，不携带 XML 声明
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");  // 不输出 XML 声明
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");  // 美化输出（缩进）
                DOMSource sourceXML = new DOMSource(doc);
                StreamResult result = new StreamResult(new File("F:\\JAVAAI\\windows_v1.5.1\\output.xml"));
                transformer.transform(sourceXML, result);

                System.out.println("XML file has been generated successfully!");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        public boolean writeXml(TabEasyPic tabEasyPic, List<picXml> picXmlList){
            try {


            // 初始化 XML 文档构建器
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // 创建根元素 <annotation>
            Document doc = docBuilder.newDocument();
            Element annotation = doc.createElement("annotation");
            doc.appendChild(annotation);

            // 创建 <folder> 和 <filename>
            Element folder = doc.createElement("folder");
            folder.appendChild(doc.createTextNode("images"));
            annotation.appendChild(folder);

            Element filename = doc.createElement("filename");
            filename.appendChild(doc.createTextNode("image_001.jpg"));
            annotation.appendChild(filename);

            Element path = doc.createElement("path");
            path.appendChild(doc.createTextNode("/path/to/image/image_001.jpg"));
            annotation.appendChild(path);

            // <source> 元素
            Element source = doc.createElement("source");
            annotation.appendChild(source);
            Element database = doc.createElement("database");
            database.appendChild(doc.createTextNode("Unknown"));
            source.appendChild(database);

            // <size> 元素
            Element size = doc.createElement("size");
            annotation.appendChild(size);
            Element width = doc.createElement("width");
            width.appendChild(doc.createTextNode("800"));
            size.appendChild(width);
            Element height = doc.createElement("height");
            height.appendChild(doc.createTextNode("600"));
            size.appendChild(height);
            Element depth = doc.createElement("depth");
            depth.appendChild(doc.createTextNode("3"));
            size.appendChild(depth);

            // <segmented> 元素
            Element segmented = doc.createElement("segmented");
            segmented.appendChild(doc.createTextNode("0"));
            annotation.appendChild(segmented);

            // <object> 元素
                for (picXml pic:picXmlList) {


                    Element object = doc.createElement("object");
                    annotation.appendChild(object);

                    Element objectName = doc.createElement("name");
                    objectName.appendChild(doc.createTextNode(pic.getName()));
                    object.appendChild(objectName);

                    Element pose = doc.createElement("pose");
                    pose.appendChild(doc.createTextNode("Unspecified"));
                    object.appendChild(pose);

                    Element truncated = doc.createElement("truncated");
                    truncated.appendChild(doc.createTextNode("0"));
                    object.appendChild(truncated);

                    Element difficult = doc.createElement("difficult");
                    difficult.appendChild(doc.createTextNode("0"));
                    object.appendChild(difficult);

                    // <bndbox> 元素
                    Element bndbox = doc.createElement("bndbox");
                    object.appendChild(bndbox);
                    Element xmin = doc.createElement("xmin");
                    xmin.appendChild(doc.createTextNode(pic.getXmin()));
                    bndbox.appendChild(xmin);
                    Element ymin = doc.createElement("ymin");
                    ymin.appendChild(doc.createTextNode(pic.getYmin()));
                    bndbox.appendChild(ymin);
                    Element xmax = doc.createElement("xmax");
                    xmax.appendChild(doc.createTextNode(pic.getXmax()));
                    bndbox.appendChild(xmax);
                    Element ymax = doc.createElement("ymax");
                    ymax.appendChild(doc.createTextNode(pic.getYmax()));
                    bndbox.appendChild(ymax);
                }
            // 将文档内容写入文件，不携带 XML 声明
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");  // 不输出 XML 声明
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");  // 美化输出（缩进）
            DOMSource sourceXML = new DOMSource(doc);
            StreamResult result = new StreamResult(new File("F:\\JAVAAI\\windows_v1.5.1\\output.xml"));
            transformer.transform(sourceXML, result);
            }catch (Exception ex){
                    ex.printStackTrace();
                    return  false;
            }
            return true;
        }


}
