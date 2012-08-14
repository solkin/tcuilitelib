package com.tomclaw.utils.bb;

import com.tomclaw.tcuilite.Settings;
import com.tomclaw.tcuilite.Theme;
import com.tomclaw.tcuilite.smiles.SmileLink;
import com.tomclaw.tcuilite.smiles.Smiles;
import java.util.Stack;
import java.util.Vector;
import javax.microedition.lcdui.Font;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2012
 * http://www.tomclaw.com/
 * @author Solkin
 */
public class BBUtil {

    private static Stack elements;
    private static BBStyle style;
    private static int length;
    private static char ch;
    private static boolean state_ReadingTag;
    private static boolean state_ReadingValue;
    private static boolean is_TagClosing;
    private static boolean is_TagSelfClosing;
    private static boolean is_PrevChSlash;
    private static String buffer;
    private static String tag;
    private static String value;
    private static int line_width;
    private static int line_height;
    private static int total_height;
    private static int local_x;
    private static int breakpoint;
    private static boolean isOverflow;
    private static boolean isTagStart;
    private static String printText;
    private static SmileLink smileLink;
    public static String[][] colors = new String[][]{
        {"red", "FF0000"},
        {"white", "FFFFFF"},
        {"cyan", "00FFFF"},
        {"silver", "C0C0C0"},
        {"blue", "0000FF"},
        {"grey", "808080"},
        {"dark_blue", "0000A0"},
        {"black", "000000"},
        {"light_purple", "FF0080"},
        {"orange", "FFA500"},
        {"purple", "800080"},
        {"brown", "A52A2A"},
        {"yellow", "FFFF00"},
        {"maroon", "800000"},
        {"lime", "00FF00"},
        {"green", "008000"},
        {"fuchsia", "FF00FF"},
        {"olive", "808000"}
    };
    private static Vector styledStrings = new Vector();

    public static BBResult processText(String text, int x, int y, int width) throws Throwable {
        BBResult bbResult = new BBResult();
        bbResult.originalString = new String();
        styledStrings.removeAllElements();
        style = new BBStyle(Theme.font);
        if (Settings.BB_FORMATTING_ENABLED) {
            if (elements == null) {
                elements = new Stack();
            }
            elements.removeAllElements();
            elements.push(style);
        } else {
            elements = null;
        }
        length = text.length();
        state_ReadingTag = false;
        state_ReadingValue = false;
        is_TagClosing = false;
        is_TagSelfClosing = false;
        is_PrevChSlash = false;
        buffer = "";
        tag = "";
        value = "";
        line_width = 0;
        line_height = 0;
        total_height = 0;
        local_x = 0;
        breakpoint = 0;
        isOverflow = false;
        isTagStart = false;
        printText = "";
        for (int c = 0; c < length; c++) {
            ch = text.charAt(c);
            if (is_PrevChSlash) {
            } else {
                if (ch == '\\') {
                    is_PrevChSlash = true;
                    continue;
                }
            }
            if (state_ReadingTag && !is_PrevChSlash) {
                if (ch == ']') {
                    if (is_TagClosing && Settings.BB_FORMATTING_ENABLED) {
                        elements.pop();
                        // System.out.println("was: " + tag);
                        if (!elements.empty()) {
                            style = (BBStyle) elements.lastElement();
                        }
                    } else if (is_TagSelfClosing) {
                        if (tag.equals("br") && Settings.BB_BR_TAG_ENABLED) {
                            local_x = 0;
                            total_height += line_height;
                            line_height = detectHeight(style, 0);
                            bbResult.originalString += "\n";
                        } else if (tag.equals("smile") && Settings.BB_SMILE_TAG_ENABLED) {
                            smileLink = new SmileLink(Integer.parseInt(value));
                            BBStyleString styleString = new BBStyleString();
                            styleString.style = style;
                            styleString.smileLink = smileLink;
                            if (x + local_x + smileLink.getWidth() > width) {
                                /** 
                                 * Смайлик не умещается на строке, 
                                 * нужно сперва осуществить перенос. 
                                 */
                                local_x = 0;
                                total_height += line_height;
                                line_height = detectHeight(style, 0);
                            }
                            styleString.local_x = x + local_x;
                            styleString.local_y = y + total_height;
                            styledStrings.addElement(styleString);
                            bbResult.originalString += Smiles.smiles[smileLink.smileIndex].getSmileDefinitions()[0];
                            if (smileLink.getHeight() > line_height) {
                                line_height = smileLink.getHeight();
                            }
                            local_x += smileLink.getWidth();
                        }
                    } else if (Settings.BB_FORMATTING_ENABLED) {
                        try {
                            style = processTag(tag, value, style);
                            elements.push(style);
                        } catch (UnsupportedTagException ex) {
                        }
                        // System.out.println("now: " + tag + " value = " + value);
                    }
                    line_height = detectHeight(style, line_height);
                    tag = "";
                    value = "";
                    state_ReadingTag = false;
                    state_ReadingValue = false;
                    is_TagClosing = false;
                    is_TagSelfClosing = false;
                } else {
                    if (state_ReadingTag && (tag.length() == 0 && ch == '/')) {
                        is_TagClosing = true;
                    } else if (state_ReadingTag && (ch == '/' && text.charAt(c + 1) == ']')) {
                        is_TagSelfClosing = true;
                    } else if (ch == '=') {
                        state_ReadingValue = true;
                    } else if (state_ReadingValue) {
                        value += ch;
                    } else if (state_ReadingTag) {
                        tag += ch;
                    }
                }
                continue;
            }
            line_width = style.font.stringWidth(buffer + ch);
            isOverflow = x + local_x + line_width > width;
            isTagStart = (ch == '[' && !is_PrevChSlash);
            if ((isOverflow || isTagStart) && !(buffer.length() == 0) || c == length) {
                // System.out.println(buffer + ": " + breakpoint);
                if (isOverflow && breakpoint > 0) {
                    printText = buffer.substring(0, breakpoint);
                    buffer = buffer.substring(breakpoint);
                } else if (isOverflow && breakpoint == 0 && local_x == 0) {
                    /** 
                     * Начало новой строки, а слово не умещается 
                     * целиком. Нужно вывести сколько можно
                     */
                    printText = buffer;
                    buffer = "";
                } else if (isOverflow && breakpoint == 0) {
                    /*
                     * Либо слово на всю строку и не было ни одного прерывания, 
                     * либо смена тега. Ничего не печатаем.
                     */
                    printText = "";
                } else {
                    printText = buffer;
                    buffer = "";
                }
                BBStyleString styleString = new BBStyleString();
                styleString.style = style;
                styleString.string = printText;
                styleString.local_x = x + local_x;
                styleString.local_y = y + total_height;
                styledStrings.addElement(styleString);
                bbResult.originalString += printText;

                local_x += style.font.stringWidth(printText);
                breakpoint = 0;
                if (isOverflow) {
                    total_height += line_height;
                    local_x = 0;
                    line_height = detectHeight(style, 0);
                    c--;
                    continue;
                }
            }
            if (isTagStart) {
                state_ReadingTag = true;
                continue;
            }
            buffer += ch;
            if (ch == ' ' || ch == ',' || ch == '.' || ch == ';' || ch == ':'
                    || ch == '-' || ch == '(' || ch == ')' || ch == '?'
                    || ch == '!' || ch == '"' || ch == '`') {
                breakpoint = buffer.length() - 1;
            }
            is_PrevChSlash = false;
        }
        total_height += line_height;
        bbResult.height = total_height;
        bbResult.bbStyleString = new BBStyleString[styledStrings.size()];
        styledStrings.copyInto(bbResult.bbStyleString);
        styledStrings.copyInto(bbResult.bbStyleString);
        return bbResult;
    }

    private static BBStyle processTag(String tag, String value, BBStyle t_style) throws Throwable {
        BBStyle bbStyle = t_style.clone();
        if (tag.equals("b")) {
            bbStyle.setStyle(Font.STYLE_BOLD);
        } else if (tag.equals("i")) {
            bbStyle.setStyle(Font.STYLE_ITALIC);
        } else if (tag.equals("u")) {
            bbStyle.setStyle(Font.STYLE_UNDERLINED);
        } else if (tag.equals("p")) {
            bbStyle.setStyle(Font.STYLE_PLAIN);
        } else if (tag.equals("l")) {
            bbStyle.setSize(Font.SIZE_LARGE);
        } else if (tag.equals("m")) {
            bbStyle.setSize(Font.SIZE_MEDIUM);
        } else if (tag.equals("s")) {
            bbStyle.setSize(Font.SIZE_SMALL);
        } else if (tag.equals("c")) {
            value = value.toLowerCase();
            for (int c = 0; c < colors.length; c++) {
                if (colors[c][0].equals(value)) {
                    value = colors[c][1];
                    break;
                }
            }
            if (value.length() == 0) {
                bbStyle.color = 0x000000;
            } else {
                try {
                    bbStyle.color = Integer.parseInt(value, 16);
                } catch (Throwable ex) {
                    bbStyle.color = 0x000000;
                }
            }
        } else {
            throw new UnsupportedTagException();
        }
        return bbStyle;
    }

    private static int detectHeight(BBStyle style, int line_height) {
        return (style.font.getHeight() > line_height)
                ? style.font.getHeight() + Theme.upSize : line_height;
    }
}
