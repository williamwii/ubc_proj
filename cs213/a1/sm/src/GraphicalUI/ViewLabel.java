package GraphicalUI;

import java.util.List;
import java.util.Arrays;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Color;
import javax.swing.JComponent;
import javax.swing.JLabel;

public class ViewLabel extends ViewFormat {
  private JLabel prototype;
  private int    baseWidth;
  private int    baseCharWidth;
  
  ViewLabel (int width, int horizontalAlignment, Font font, Color color, HighlightControl highlightControl, List <Format> formats) {
    super (formats);
    prototype = new JLabel ();
    baseWidth     = width;
    baseCharWidth = prototype.getFontMetrics (font).charWidth ('W');
    Dimension sz = new Dimension (width, prototype.getPreferredSize ().height);
    prototype.setMinimumSize   (sz);
    prototype.setPreferredSize (sz);
    prototype.setMaximumSize   (sz);
    prototype.setHorizontalAlignment (horizontalAlignment);
    if (font!=null)
      prototype.setFont (font);
    if (color!=null)
      prototype.setForeground (color);
    setHighlightControl (highlightControl, prototype);
  }
  
  ViewLabel (int width, int horizontalAlignment, Font font, Color color, HighlightControl highlightControl, Format format) {
    this (width, horizontalAlignment, font, color, highlightControl, Arrays.asList (format));
  }
  
  JComponent getRendererPrototype () {
    return prototype;
  }
  
  void setRendererPrototypeValue (Object value, boolean isSelected) {
    prototype.setText (valueToText (value, isSelected));
  }
  
  @Override
  void adjustFontSize (int increment) {
    Font oldFont = prototype.getFont ();
    Font newFont = new Font (oldFont.getName (), oldFont.getStyle (), oldFont.getSize () + increment);
    prototype.setFont (newFont);
    int width = (baseWidth * prototype.getFontMetrics (newFont).charWidth ('W') + baseCharWidth/2 + 1) / baseCharWidth;
    Dimension sz = new Dimension (width, prototype.getPreferredSize ().height);
    prototype.setMinimumSize   (sz);
    prototype.setPreferredSize (sz);
    prototype.setMaximumSize   (sz);
  }
  
  @Override
  void setWidth (int width) {
    Dimension sz = new Dimension (width, prototype.getPreferredSize ().height);
    prototype.setMinimumSize   (sz);
    prototype.setPreferredSize (sz);
    prototype.setMaximumSize   (sz);
  }
}