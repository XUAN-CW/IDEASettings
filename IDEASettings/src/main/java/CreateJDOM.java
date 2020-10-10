import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.Format.TextMode;
import org.jdom2.output.XMLOutputter;

/**
 * JDOM生成XML
 * @author ouyangjun
 */
public class CreateJDOM {

    public static void main(String[] args) {
        // 执行JDOM生成XML方法
        createJDOM(new File("C:\\Users\\86188\\Desktop\\itheima_mybatis_config中东欧\\.idea\\workspace在.xml"));
    }

    public static void createJDOM(File file) {
        try {
            // 创建一个根节点
            Element rootElement = new Element("root");
            Document doc = new Document(rootElement);

            // 在根节点下创建第一个子节点
            Element rootOneElement = new Element("person");
            rootOneElement.setAttribute(new Attribute("attr","root one"));

            // 在第一个子节点下创建第一个子节点
            Element childOneElement = new Element("people");
            childOneElement.setAttribute(new Attribute("attr","child one"));
            childOneElement.setText("person child one");

            // 在第一个子节点下创建第二个子节点
            Element childTwoElement = new Element("people");
            childTwoElement.setAttribute(new Attribute("attr","child two"));
            childTwoElement.setText("person child two");

            // 在根节点下创建第二个子节点
            Element rootTwoElement = new Element("person");
            rootTwoElement.setAttribute(new Attribute("attr","root two"));

            // 在第一个子节点下创建第一个子节点
            Element oneChildOneElement = new Element("people");
            oneChildOneElement.setAttribute(new Attribute("attr","child one"));
            oneChildOneElement.setText("person child one");

            // 在第一个子节点下创建第二个子节点
            Element twoChildTwoElement = new Element("people");
            twoChildTwoElement.setAttribute(new Attribute("attr","child two"));
            twoChildTwoElement.setText("person child two");

            rootOneElement.addContent(childOneElement);
            rootOneElement.addContent(childTwoElement);

            rootTwoElement.addContent(oneChildOneElement);
            rootTwoElement.addContent(twoChildTwoElement);

            doc.getRootElement().addContent(rootOneElement);
            doc.getRootElement().addContent(rootTwoElement);

            // 创建xml输出流操作类
            XMLOutputter xmlOutput = new XMLOutputter();

            // 设置xml格式化的属性
            Format f = Format.getRawFormat();
            f.setIndent("  "); // 文本缩进
            f.setTextMode(TextMode.TRIM_FULL_WHITE);
            xmlOutput.setFormat(f);

            // 把xml文件输出到指定的位置
            xmlOutput.output(doc, new FileOutputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}