package com.tomclaw.utils.bb;

import com.tomclaw.utils.FontUtil;
import javax.microedition.lcdui.Font;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2012
 * http://www.tomclaw.com/
 * @author Solkin
 */
public class BBStyle {
    
    public Font font;
    public int color;
    
    public BBStyle(Font font) {
        this.font = font;
        this.color = 0x000000;
    }
    
    public BBStyle(Font font, int color) {
        this.font = font;
        this.color = color;
    }
    
    public BBStyle clone() {
        Font t_font = FontUtil.getFont(font.getFace(), font.getStyle(), font.getSize());
        int t_color = color;
        BBStyle style = new BBStyle(t_font, t_color);
        return style;
    }
    
    public void setStyle(int style) throws Throwable {
        font = FontUtil.getFont(font.getFace(), font.getStyle() | style, font.getSize());
    }
    
    public void setSize(int size) throws Throwable {
        font = FontUtil.getFont(font.getFace(), font.getStyle(), size);
    }
}
