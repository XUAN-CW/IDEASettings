import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author XUAN
 * @date 2020/10/5 - 23:35
 * @references
 *   [xml解析之----DOM解析](https://blog.csdn.net/u010590318/article/details/40474765?utm_medium=distribute.pc_relevant_t0.none-task-blog-BlogCommendFromMachineLearnPai2-1.channel_param&depth_1-utm_source=distribute.pc_relevant_t0.none-task-blog-BlogCommendFromMachineLearnPai2-1.channel_param)
 *   [java获取和设置系统变量(环境变量)](https://blog.csdn.net/u013514928/article/details/78147421)
 *
 * @purpose
 * @errors
 */
public class SetIDEAConf {

    private String getJdkBigVersion(){
        Properties properties = System.getProperties();
        //根据环境变量获取 java.version 后分割，取第一个分割出来的（也就是 jdk 的大版本）
        return properties.getProperty("java.version").split("\\.")[0];
    }

    private Document parseDocument(String xml) throws ParserConfigurationException, IOException, SAXException {
        //获得dom解析工厂类
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        //得到dom解析器
        DocumentBuilder builder = factory.newDocumentBuilder();
        //解析xml文件
        return  builder.parse(xml);
    }

    private void saveDocument(Document document) throws TransformerException, FileNotFoundException {
        //把内存中更新后对象树，重新定回到xml文档中
        TransformerFactory factory2 = TransformerFactory.newInstance();
        Transformer tf = factory2.newTransformer();
        //document.getDocumentURI() 需要去除前面的 file:\ 六个字符
        tf.transform(new DOMSource(document), new StreamResult(new FileOutputStream(document.getDocumentURI().substring(5))));
    }

    private void setCompiler(){
        String xml = ".idea"+ File.separator +"compiler.xml";
        try {
            //解析xml文件
            Document document = parseDocument(xml);
            //获取 bytecodeTargetLevel 节点列表
            NodeList nodeList = document.getElementsByTagName("bytecodeTargetLevel");
            //我估计 bytecodeTargetLevel 标签只有一个，所以在这里我直接取第一个
            Element element = (Element)nodeList.item(0);
            //设为 jdk 大版本
            element.setAttribute("target",getJdkBigVersion());
            saveDocument(document);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        SetIDEAConf setIDEAConf = new SetIDEAConf();
        setIDEAConf.setCompiler();
    }
}
