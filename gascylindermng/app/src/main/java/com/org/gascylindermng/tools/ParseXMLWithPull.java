package com.org.gascylindermng.tools;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;

public class ParseXMLWithPull {

    public String getJsonStr() {
        return jsonStr;
    }

    public void setJsonStr(String jsonStr) {
        this.jsonStr = jsonStr;
    }

    private String jsonStr;

    //用Pull方式解析XML
    public void parseXMLWithPull(String xmlData){
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = factory.newPullParser();
            //设置输入的内容
            xmlPullParser.setInput(new StringReader(xmlData));
            //获取当前解析事件，返回的是数字
            int eventType = xmlPullParser.getEventType();
            //保存内容

            while (eventType != (XmlPullParser.END_DOCUMENT)){
                String nodeName = xmlPullParser.getName();
                switch (eventType){
                    //开始解析XML
                    case XmlPullParser.START_TAG:{
                        //nextText()用于获取结点内的具体内容
                        if(nodeName.equals("string"))
                            jsonStr = xmlPullParser.nextText();

                    } break;
                    //结束解析
                    case XmlPullParser.END_TAG:{
                        if("}".equals(nodeName)){

                        }
                    } break;
                    default: break;
                }
                //下一个
                eventType = xmlPullParser.next();
            }
            Log.d("TAG", "jsonStr: "+ jsonStr);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}