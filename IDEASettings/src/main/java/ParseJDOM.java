import java.io.File;
import java.io.IOException;
import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.CDATA;
import org.jdom2.Comment;
import org.jdom2.Content;
import org.jdom2.DocType;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.EntityRef;
import org.jdom2.JDOMException;
import org.jdom2.ProcessingInstruction;
import org.jdom2.Text;
import org.jdom2.input.SAXBuilder;

/**
 * JDOM解析XML
 * @author ouyangjun
 */
public class ParseJDOM {

    public static void main(String[] args) {
        // 执行JDOM解析XML方法
        parseJDOM(new File("C:\\Users\\86188\\Desktop\\itheima_mybatis_config中东欧\\.idea\\workspace在.xml"));
    }

    public static void parseJDOM(File file) {
        try {
            // 创建一个sax解析器
            SAXBuilder builder = new SAXBuilder();

            // 根据xml结构转换成一个Document对象
            Document doc = builder.build(file);

            // 打印xml信息
            printXNL(doc.getContent());
        } catch(JDOMException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private static void printXNL(List<Content> list) {
        for (Content temp : list) {
            if (temp instanceof Comment) { //获取的内容是注释
                Comment com = (Comment)temp;
                System.out.println("<--"+com.getText()+"-->");
            } else if (temp instanceof Element) { //获取的内容是元素
                Element elt = (Element) temp;
                List<Attribute> attrs = elt.getAttributes();
                System.out.print("<"+elt.getName()+"");
                for (Attribute t : attrs) {
                    System.out.print(" " + t.getName()+"=\""+t.getValue()+"\"");
                }
                System.out.print(">");
                printXNL(elt.getContent());
                System.out.print("</"+elt.getName()+">");
            } else if (temp instanceof ProcessingInstruction) { // 获取的内容是处理指令
                ProcessingInstruction pi = (ProcessingInstruction)temp;
                System.out.println("<?"+pi.getTarget()+""+pi.getData()+"?>");
            } else  if (temp instanceof EntityRef) {
                EntityRef ref = (EntityRef)temp;
                System.out.println("<--"+ref.getName()+"-->");
            } else if (temp instanceof Text) { //获取的内容是文本
                Text text = (Text)temp;
                if (!text.getText().trim().equals("")) {
                    System.out.print(text.getText());
                } else {
                    System.out.println();
                }
            } else if (temp instanceof CDATA) { // 获取的内容是CDATA
                CDATA cdata = (CDATA)temp;
                System.out.println("<![CDATA["+cdata.getText()+"]]>");
            } else  if (temp instanceof DocType) {
                DocType docType = (DocType)temp;
                System.out.println("<--"+docType.getCType()+"-->");
            }
        }
    }
}