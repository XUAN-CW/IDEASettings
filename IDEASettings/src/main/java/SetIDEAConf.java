import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
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

    String projectPath = "";

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
        String xml = projectPath + File.separator + ".idea"+ File.separator +"compiler.xml";
        try {
            //解析xml文件
            Document document = parseDocument(xml);
            //获取 bytecodeTargetLevel 节点列表
            NodeList nodeList = document.getElementsByTagName("bytecodeTargetLevel");
            //我估计 bytecodeTargetLevel 标签只有一个，所以在这里我直接取第一个
            Element element = (Element)nodeList.item(0);
            //设为 jdk 大版本
            element.setAttribute("target",getJdkBigVersion());
            //对 module 进行同样的设置
            NodeList modules =  element.getElementsByTagName("module");
            for (int i = 0; i < modules.getLength(); i++) {
                Element currentElement = (Element) modules.item(i);
                if (currentElement.hasAttribute("target")){
                    currentElement.setAttribute("target",getJdkBigVersion());
                }
            }
            //保存 xml 文件
            saveDocument(document);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setMisc() {
        String xml =  projectPath + File.separator + ".idea" + File.separator + "misc.xml";
        try {
            //解析xml文件
            Document document = parseDocument(xml);
            //获取 component 节点列表
            NodeList nodeList = document.getElementsByTagName("component");
            Element element = null;
            //找到 ProjectRootManager
            for (int i = 0; i < nodeList.getLength(); i++) {
                element = (Element) nodeList.item(i);
                if (element.getAttribute("name").equals("ProjectRootManager")) {
                    //设为 jdk 大版本
                    if (element.hasAttribute("languageLevel")) {
                        element.setAttribute("languageLevel", "JDK_" + getJdkBigVersion());
                    }
                    if (element.hasAttribute("languageLevel")) {
                        element.setAttribute("project-jdk-name",getJdkBigVersion());
                    }
                }
            }
            //保存 xml 文件
            saveDocument(document);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setWorkspace() {
        String xml = projectPath + File.separator + ".idea" + File.separator + "workspace.xml";
        String M2_HOME = System.getenv("M2_HOME");
        String mavenSettings = M2_HOME + File.separator+"conf"+File.separator+"settings.xml";
        try {
            //解析xml文件
            Document document = parseDocument(xml);
            //获取 MavenGeneralSettings 节点列表
            NodeList options = document.getElementsByTagName("option");
            for (int i = 0; i < options.getLength(); i++) {
                //获取当前结点
                Element currentElement = (Element) options.item(i);
                //如果 name=mavenHome ,设置 value=M2_HOME
                if (currentElement.getAttribute("name").equals("mavenHome")){
                    currentElement.setAttribute("value",M2_HOME);
                }
                //如果 name=userSettingsFile ,设置 value=mavenSettings
                if (currentElement.getAttribute("name").equals("userSettingsFile")){
                    currentElement.setAttribute("value",mavenSettings);
                }
                //如果 name=localRepository ,设置 value=localRepositoryAbsolutePath ,localRepository 在安装 maven 时已设置
                if (currentElement.getAttribute("name").equals("localRepository")){
                    //localRepositoryAbsolutePath 从 mavenSettings 的 localRepository 标签中获取
                    Document localRepositoryDocument = parseDocument(mavenSettings);
                    Element localRepositoryElement = (Element)localRepositoryDocument.getElementsByTagName("localRepository").item(0);
                    //获取 maven 中 localRepository 配置的 localRepositoryPath
                    String localRepositoryPath = localRepositoryElement.getTextContent();
                    //建立 localRepository 对应的文件,以便操作
                    File localRepositoryFile = new File(localRepositoryPath);
                    //如果 maven 中使用的不是绝对路径,需要进行处理,将其转化为绝对路径
                    if (!localRepositoryFile.getAbsolutePath().equals(localRepositoryPath)){
                        //使用相对路径,以 mavenSettings 为起点,进行变换
                        localRepositoryFile = new File(mavenSettings);
                        String[] temp = localRepositoryPath.split("/");
                        for (int j = 0; j < temp.length; j++) {
                            if (temp[j].equals("..")){
                                localRepositoryFile = localRepositoryFile.getParentFile();
                            }else {
                                localRepositoryFile = new File(localRepositoryFile+File.separator+temp[j]);
                            }
                        }
                    }
                    currentElement.setAttribute("value",localRepositoryFile.getAbsolutePath());
                }
            }
            //保存 xml 文件
            saveDocument(document);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.out.println(123);
        SetIDEAConf setIDEAConf = new SetIDEAConf();
        setIDEAConf.setCompiler();
        setIDEAConf.setMisc();
        setIDEAConf.setWorkspace();
    }
}
