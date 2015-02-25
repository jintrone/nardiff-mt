package nardiff.mt;/*
 *
 * Created on March 16, 2007, 4:34 PM
 *
 * Copyright 2006-2007 Nigel Hughes
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at http://www.apache.org/
 * licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 *
 * Modified by kkoning to use anti-aliasing.
 *
 */

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.*;

/**

 * @author nigel
 */
public class Text2PNG {

    static String testText = "Judy is going to have a birthday party.  " +
            "She is ten years old.  She wants a hammer and a saw for presents.  Then " +
            "she could make a coat rack and fix her doll house.  She asked her " +
            "father to get them for her.  \n\n\n" +
            "Her father did not want to get them for her.  He did not think that " +
            "girls should play with a hammer and a saw.  But he wanted to get her " +
            "something.  So he bought her a beautiful new dress.  Judy liked the " +
            "dress, but she still wanted the hammer and the saw.\n" +
            "Later, she told her grandmother about her wish.  Her grandmother " +
            "knew that Judy really wanted a hammer and a saw.  She decided to get " +
            "them for her, because when Judy grows up and becomes a woman she will " +
            "have to fix things when they break.\n\n\n" +
            "Then her grandmother went out that very day and bought the tools for " +
            "Judy.  She gave them to Judy that night.  Judy was very happy.  Now " +
            "she could build things with her hammer and saw.";


    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "true");
        System.out.println("Headless? " + java.awt.GraphicsEnvironment.isHeadless());

        String[] splitText = cleanInput(testText);
        BufferedImage img = renderTextToImage(splitText,400);

        try {
            ImageIO.write(img, "png", new File("Text.png"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void writeImageFile(String story, Long id) {
        System.setProperty("java.awt.headless", "true");
        String[] text = cleanInput(story);
        BufferedImage img = renderTextToImage(text,500);
        try {
            ImageIO.write(img,"png",new File("web-app/images/narratives/" + id + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * Create a sanitized input for generating the next image of text.  The image
     * generation code does not accept blank lines, or do any paragraph layout.  We
     * need to fix those things, at least a little.
     *
     * @param input
     * @return
     */
    public static String[] cleanInput(String input) {
        java.util.List<String> toReturn = new ArrayList<String>();

        String[] lines = input.split("\n");
        for ( String line : lines ) {
            line = line.trim();
            if (line.length() > 3) // Line must be at least 1 word
                toReturn.add("     " + line);
        }

        return toReturn.toArray(new String[0]);
    }

    public static BufferedImage renderTextToImage(String text, int width) {
        return renderTextToImage(cleanInput(text),width);

    }


    /**
     * Renders multiple paragraphs of text in an array to an image (created and returned).
     *
     * @param text The message in an array of strings (one paragraph in each
     * @param width The width the text should be limited to
     * @return An image with the text rendered into it
     */
    public static BufferedImage renderTextToImage(String text[], int width){
        Font font = new Font("Arial", Font.PLAIN, 20);
        Color textColor = Color.BLACK;

        LinkedList<BufferedImage> images = new LinkedList<BufferedImage>();

        int totalHeight = 0;

        for (String paragraph : text){
            BufferedImage paraImage = renderTextToImage(font,textColor,paragraph,width);
            totalHeight+=paraImage.getHeight();
            images.add(paraImage);
        }

        BufferedImage image = createCompatibleImage(width,totalHeight);
        Graphics2D graphics = (Graphics2D) image.createGraphics();

        int y=0;

        for (BufferedImage paraImage : images){
            graphics.drawImage(paraImage,0,y,null);
            y+=paraImage.getHeight();
        }

        graphics.dispose();
        return image;
    }
    /**
     * Renders a paragraph of text (line breaks ignored) to an image (created and returned).
     *
     * @param font The font to use
     * @param textColor The color of the text
     * @param text The message
     * @param width The width the text should be limited to
     * @return An image with the text rendered into it
     */
    public static BufferedImage renderTextToImage(Font font, Color textColor, String text, int width){
        Hashtable   map = new Hashtable();
        map.put(TextAttribute.FONT, font);
        map.put(TextAttribute.FOREGROUND, Color.BLACK);
        AttributedString attributedString = new AttributedString(text,map);
        AttributedCharacterIterator paragraph = attributedString.getIterator();

        FontRenderContext frc = new FontRenderContext(null, true, true);
        int paragraphStart = paragraph.getBeginIndex();
        int paragraphEnd = paragraph.getEndIndex();
        LineBreakMeasurer lineMeasurer = new LineBreakMeasurer(paragraph, frc);

        float   drawPosY=0;

        //First time around, just determine the height
        while (lineMeasurer.getPosition() < paragraphEnd) {
            TextLayout layout = lineMeasurer.nextLayout(width);

            // Move it down
            drawPosY += layout.getAscent() + layout.getDescent() + layout.getLeading();
        }

        BufferedImage image = createCompatibleImage(width,(int) drawPosY);

        Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        graphics.setFont(font);


        drawPosY=0;
        lineMeasurer.setPosition(paragraphStart);
        while (lineMeasurer.getPosition() < paragraphEnd) {
            TextLayout layout = lineMeasurer.nextLayout(width);

            // Move y-coordinate by the ascent of the layout.
            drawPosY += layout.getAscent();
          
         /* Compute pen x position.  If the paragraph is
            right-to-left, we want to align the TextLayouts
            to the right edge of the panel.
          */
            float drawPosX;
            if (layout.isLeftToRight()) {
                drawPosX = 0;
            } else {
                drawPosX = width - layout.getAdvance();
            }

            // Draw the TextLayout at (drawPosX, drawPosY).
//            layout.
            layout.draw(graphics, drawPosX, drawPosY);

            // Move y-coordinate in preparation for next layout.
            drawPosY += layout.getDescent() + layout.getLeading();
        }

        graphics.dispose();
        return image;
    }
    /**
     * Creates an image compatible with the current display
     *
     * @return A BufferedImage with the appropriate color model
     */
    public static BufferedImage createCompatibleImage(int width, int height) {
        return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

    }
}
