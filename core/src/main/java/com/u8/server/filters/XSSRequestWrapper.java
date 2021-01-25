package com.u8.server.filters;

import com.u8.server.log.Log;
import org.apache.struts2.dispatcher.StrutsRequestWrapper;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.regex.Pattern;

public class XSSRequestWrapper extends StrutsRequestWrapper {

    public XSSRequestWrapper(HttpServletRequest req) {
        super(req);
    }

    @Override
    public String getParameter(String name) {
        String value = super.getParameter(name);
        if (value == null) {
            return null;
        }
        return cleanXSS(value);
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (values == null) {
            return null;
        }
        int count = values.length;
        String[] encodedValues = new String[count];
        for (int i = 0; i < count; i++) {
            encodedValues[i] = cleanXSS(values[i]);
        }
        return encodedValues;
    }

    @Override
    public Enumeration<String> getParameterNames() {
        Enumeration<String> names = super.getParameterNames();
        List<String> newNames = new ArrayList<String>();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            name = cleanXSS(name);
            newNames.add(name);
        }
        return Collections.enumeration(newNames);
    }

    @Override
    public Map getParameterMap() {

        Map paramMap = super.getParameterMap();
        if (paramMap == null) {
            return paramMap;
        }
        for (Object value : paramMap.values()) {
            String[] str = (String[]) value;
            if (str != null) {
                for (int i = 0; i < str.length; i++) {
                    str[i] = cleanXSS(str[i]);
                }
            }
        }

        return paramMap;
    }


    public static String cleanXSS(String value) {
        if (value != null) {
            // Avoid null characters
            value = value.replaceAll("", "");
            // Avoid anything between script tags
            Pattern scriptPattern = Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE);
            value = scriptPattern.matcher(value).replaceAll("");
            // Avoid anything in a
            // src="http://www.yihaomen.com/article/java/..." type of
            // e­xpression
            scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");
            scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");
            // Remove any lonesome </script> tag
            scriptPattern = Pattern.compile("</script>", Pattern.CASE_INSENSITIVE);
            value = scriptPattern.matcher(value).replaceAll("");
            // Remove any lonesome <script ...> tag
            scriptPattern = Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");
            // Avoid eval(...) e­xpressions
            scriptPattern = Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");
            // Avoid e­xpression(...) e­xpressions
            scriptPattern = Pattern.compile("e­xpression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");
            // Avoid javascript:... e­xpressions
            scriptPattern = Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE);
            value = scriptPattern.matcher(value).replaceAll("");
            // Avoid vbscript:... e­xpressions
            scriptPattern = Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE);
            value = scriptPattern.matcher(value).replaceAll("");
            // Avoid οnlοad= e­xpressions
            scriptPattern = Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");
        }
        return filter(value);
    }

    /**
     * 过滤特殊字符
     */
    public static String filter(String value) {
        if (value == null) {
            return null;
        }
        StringBuffer result = new StringBuffer(value.length());
        for (int i = 0; i < value.length(); ++i) {
            switch (value.charAt(i)) {
                case '<':
                    Log.e("xss filter pass a < char.");
                    result.append("＜");
                    break;
                case '>':
                    Log.e("xss filter pass a > char.");
                    result.append("＞");
                    break;
                case '"':
                    Log.e("xss filter pass a \" char.");
                    result.append("\"");
                    break;
                case '\'':
                    Log.e("xss filter pass a ' char.");
                    result.append("‘");
                    break;
                case '#':
                    Log.e("xss filter pass a # char.");
                    result.append("＃");
                    break;
                case '\\':
                    // 全角斜线
                    Log.e("xss filter pass a \\ char.");
                    result.append('＼');
                case '%':
                    Log.e("xss filter pass a % char.");
                    result.append("％");
                    break;
                case ';':
                    Log.e("xss filter pass a ; char.");
                    result.append("；");
                    break;
                case '(':
                    Log.e("xss filter pass a ( char.");
                    result.append("(");
                    break;
                case ')':
                    Log.e("xss filter pass a ) char.");
                    result.append(")");
                    break;
                case '&':
                    Log.e("xss filter pass a & char.");
                    result.append("＆");
                    break;
                case '+':
                    Log.e("xss filter pass a + char.");
                    result.append("+");
                    break;
                default:
                    result.append(value.charAt(i));
                    break;
            }
        }
        return result.toString();
    }

}

