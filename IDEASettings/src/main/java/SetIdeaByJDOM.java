import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * @author XUAN
 * @date 2020/10/6 - 16:06
 * @references
 *   [Java JDOM生成和解析XML](https://blog.csdn.net/p812438109/article/details/81813411)
 * @purpose
 * @errors
 */
public class SetIdeaByJDOM {
    String projectPath = "";
    private String compilerXML = projectPath + File.separator + ".idea"+ File.separator +"compiler.xml";
    private String miscXML = projectPath + File.separator + ".idea" + File.separator + "misc.xml";
    private String workspaceXML = projectPath + File.separator + ".idea" + File.separator + "workspace.xml";

    public interface EditElement {
        void edit(Element element);
    }

    private void confCompilerXML() throws JDOMException, ParserConfigurationException, SAXException, IOException {
        //解析xml文件
        Document document = parseDocument(compilerXML);
        //获取 bytecodeTargetLevel 节点列表
        for (Content temp : document.getContent()) {
            if (temp instanceof Element){
                //节点转换为元素
                Element element = (Element) temp;
                //找到 bytecodeTargetLevel 标签
                if (element.getName().equals("bytecodeTargetLevel")){
                    // target 设为 jdk 大版本
                    element.setAttribute("target",getJdkBigVersion());
                    //找到 bytecodeTargetLevel 下的 module 标签
                    for (Element e:element.getChildren("module")){
                        // target 设为 jdk 大版本
                        e.setAttribute("target",getJdkBigVersion());
                    }
                }
            }
        }
    }

    private void confMiscXML() throws JDOMException, ParserConfigurationException, SAXException, IOException {
        //解析xml文件
        Document document = parseDocument(miscXML);
        //获取 bytecodeTargetLevel 节点列表
        for (Content temp : document.getContent()) {
            if (temp instanceof Element){
                //节点转换为元素
                Element element = (Element) temp;
                //找到 component 标签
                if (element.getName().equals("component")){
                    // target 设为 jdk 大版本
                    element.setAttribute("target",getJdkBigVersion());
                    //找到 bytecodeTargetLevel 下的 module 标签
                    for (Element e:element.getChildren("module")){
                        // target 设为 jdk 大版本
                        e.setAttribute("target",getJdkBigVersion());
                    }
                }
            }
        }
    }

    private void confWorkspaceXML(){

    }

    class SelectedElement{
        String tag;
        String selectedAttribute = null;
        String selectedAttributeValue = null;
        SelectedElement(String tag){
            this.tag=tag;
        }

        SelectedElement(String tag,String selectedAttribute,String selectedAttributeValue){
            this.tag=tag;
            this.selectedAttribute=selectedAttribute;
            this.selectedAttributeValue=selectedAttributeValue;
        }
    }

    private void common(String XMLPath,EditElement editElement) throws JDOMException, IOException {
        // 创建一个sax解析器
        SAXBuilder builder = new SAXBuilder();
        // 根据xml结构转换成一个Document对象
        Document document =  builder.build(new File(XMLPath));
        for (Content temp : document.getContent()) {
            if (temp instanceof Element){
                //节点转换为元素
                Element element = (Element) temp;
                //编辑节点
                editElement.edit(element);
            }
        }
        // 创建xml输出流操作类
        XMLOutputter xmlOutput = new XMLOutputter();
        // 设置xml格式化的属性
        Format f = Format.getRawFormat();
        // 文本缩进
        f.setIndent("  ");
        f.setTextMode(Format.TextMode.TRIM_FULL_WHITE);
        xmlOutput.setFormat(f);
        // 把xml文件输出到指定的位置
        xmlOutput.output(document, new FileOutputStream(document.getBaseURI()));
    }

    private String getJdkBigVersion(){
        Properties properties = System.getProperties();
        //根据环境变量获取 java.version 后分割，取第一个分割出来的（也就是 jdk 的大版本）
        return properties.getProperty("java.version").split("\\.")[0];
    }

    private Document parseDocument(String xml) throws ParserConfigurationException, IOException, SAXException, JDOMException {
        // 创建一个sax解析器
        SAXBuilder builder = new SAXBuilder();
        // 根据xml结构转换成一个Document对象
        return builder.build(new File("xml"));
    }

    private void saveDocument(Document document) throws IOException {
        /// 创建xml输出流操作类
        XMLOutputter xmlOutput = new XMLOutputter();
        // 设置xml格式化的属性
        Format f = Format.getRawFormat();
        // 文本缩进
        f.setIndent("  ");
        f.setTextMode(Format.TextMode.TRIM_FULL_WHITE);
        xmlOutput.setFormat(f);
        // 把xml文件输出到指定的位置
        xmlOutput.output(document, new FileOutputStream(document.getBaseURI()));
    }
}
