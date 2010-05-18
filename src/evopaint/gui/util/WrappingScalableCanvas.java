/*
 *  Copyright (C) 2010 Markus Echterhoff <evopaint@markusechterhoff.com>
 * 
 *  This file is part of EvoPaint.
 * 
 *  EvoPaint is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with EvoPaint.  If not, see <http://www.gnu.org/licenses/>.
 */

package evopaint.gui.util;

import evopaint.Perception;
import evopaint.util.ExceptionHandler;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

/**
 * The <code>WrappingScalableCanvas</code> class is used to create a parallax
 * surface which can be moved into two directions and be scaled indefinetly.
 * <p>
 * Keep in mind two spaces which need transformation from one to another. The
 * user space in which the user performs clicks and the image is scaled into
 * and the image space in which operations are performed on the image.<br>
 * As a rule of thumb remember to transform any points from mouse clicks
 * performed on the canvas to image space before processing them.
 * </p>
 * <p>
 * Implementing <code>IOverlayable</code>, this canvas supports alpha overlays,
 * which it will also wrap and scale to user space accordingly.
 * </p>
 *
 * @author Markus Echterhoff <evopaint@markusechterhoff.com>
 */
public class WrappingScalableCanvas extends JComponent implements IOverlayable {

    private Perception perception;
    private BufferedImage image;
    private Graphics2D imageG2;
    private int imageWidth;
    private int imageHeight;
    private int integerScale;
    private double scale;
    private Point translation;
    private AffineTransform scaleTransform;
    private AffineTransform translationTransform;
    private AffineTransform transform;
    private List<IOverlay> overlays;

    /**
     * Creates a new WrappingScalableCanvas using the image supplied.
     * @param image
     */
    public WrappingScalableCanvas(Perception perception) {
        this.perception = perception;
        this.image = perception.createImage();
        this.imageWidth = image.getWidth();
        this.imageHeight = image.getHeight();
        this.imageG2 = (Graphics2D)image.getGraphics();
        this.integerScale = 10;
        this.scale = 1;
        this.translation = new Point(0, 0);
        this.translationTransform = new AffineTransform();
        this.overlays = new ArrayList<IOverlay>();
        updateScale();
        updateComponentSize();
    }

    public double getScale() {
        return scale;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
        this.imageWidth = image.getWidth();
        this.imageHeight = image.getHeight();
        this.imageG2 = (Graphics2D)image.getGraphics();
        updateScale();
        repaint();
    }
    
    /**
     * Magnifies the display of the image
     */
    public void scaleUp() {
        integerScale++;
        scale = integerScale / 10d;
        updateScale();
    }

    /**
     * Shrinks the display of the image
     */
    public void scaleDown() {
        if (integerScale <= 1) {
            return;
        }
        integerScale--;
        updateScale();
    }

    /**
     * resets the display of the image to its original size
     */
    public void resetScale() {
        integerScale = 10;
        updateScale();
    }

    public void resetTranslation() {
        translation.move(0, 0);
        translationTransform = AffineTransform.getTranslateInstance(translation.x, translation.y);
        updateScale();
    }

    private void updateScale() {
        scale = integerScale / 10d;
        scaleTransform = AffineTransform.getScaleInstance(scale, scale);
        transform = new AffineTransform(scaleTransform);
        transform.concatenate(translationTransform);
        updateComponentSize();
        revalidate();
    }

    private void updateComponentSize() {
        setPreferredSize(new Dimension((int)Math.ceil(imageWidth * scale),
                (int)Math.ceil(imageHeight * scale)));
    }

    /**
     * Translates the display of the underlying image using user space
     * coordinates.
     * @param origin the origin eg. of a user space drag operation
     * @param destination the destination eg. of a user space drag operation
     */
    public void translateInUserSpace(Point origin, Point destination) {
        // transform points to image space before translation so they scale
        // correctly
        origin = transformToImageSpace(new Point(origin));
        destination = transformToImageSpace(new Point(destination));

        translation.x += destination.x - origin.x;
        if (translation.x < (-1) * imageWidth) {
            translation.x += imageWidth;
        } else if (translation.x > imageWidth) {
            translation.x -= imageWidth;
        }
        translation.y += destination.y - origin.y;
        if (translation.y < (-1) * imageWidth) {
            translation.y += imageWidth;
        } else if (translation.y > imageWidth) {
            translation.y -= imageWidth;
        }
        translationTransform = AffineTransform.getTranslateInstance(translation.x, translation.y);
        transform = new AffineTransform(scaleTransform);
        transform.concatenate(translationTransform);
    }

    /**
     * Transforms a <code>Point</code> from user space to image space. It will
     * translate the <code>Point</code> back to its origin in user space and
     * rescale it to the original image space scale.
     * @param point The <code>Point</code> used to create the
     * @return A new Point in image space corresponding to the passed
     * <code>Point</code>
     */
    public Point transformToImageSpace(Point point) {
       // AffineTransform invertedTransform = new AffineTransform(transform);
        try {
            Point2D floatPoint = transform.createInverse().transform(point, null);
            return new Point((int)floatPoint.getX(), (int)floatPoint.getY());
        } catch (NoninvertibleTransformException ex) {
            ExceptionHandler.handle(ex, false);
        }
       // Point2D.Float floatPoint = (Point2D.Float)invertedTransform.transform(point, null);
        //Point ret = new Point((int)floatPoint.x, (int)floatPoint.y);

        // the transformed point may have coordinates wich lie out of our
        // image, so we have to wrap them
        /*
        if (ret.x < 0) {
            ret.x += imageWidth;
        }
        else if (ret.x > imageWidth) {
            ret.x -= imageWidth;
        }
        if (ret.y < 0) {
            ret.y += imageHeight;
        }
        else if (ret.y > imageHeight) {
            ret.y -= imageHeight;
        }
*/
        //return ret;
        return null;
    }

    public Rectangle transformToImageSpace(Rectangle rectangle) {
        Point location = transformToImageSpace(rectangle.getLocation());
        try {
            Rectangle ret = transform.createInverse().createTransformedShape(rectangle).getBounds();
            ret.setLocation(location);
            return ret;
        } catch (NoninvertibleTransformException ex) {
            ExceptionHandler.handle(ex, true);
        }

        assert (false);
        return null;
    }

    /**
     * Scales a <code>Rectangle</code> from image space to user space
     * @param rectangle The <code>Rectangle</code> you wish to scale
     * @return A new <code>Rectangle</code> scaled to user space
     * @see Rectangle
     */
    public Rectangle scaleToUserSpace(Rectangle rectangle) {
        return scaleTransform.createTransformedShape(rectangle).getBounds();
    }

    /**
     * Scales a <code>Dimension</code> from image space to user space
     * @param dimension The <code>Dimension</code> you wish to scale
     * @return A new <code>Dimension</code> scaled to user space
     * @see Dimension
     */
    public Dimension scaleToUserSpace(Dimension dimension) {
        return scaleTransform.createTransformedShape(new Rectangle(dimension)).getBounds().getSize();
    }

    /**
     * Scales a <code>Rectangle</code> from user space to image space
     * @param rectangle The <code>Rectangle</code> you wish to scale
     * @return A new <code>Rectangle</code> scaled to image space
     * @see Rectangle
     */
    public Rectangle scaleToImageSpace(Rectangle rectangle) {
        try {
            return scaleTransform.createInverse().createTransformedShape(rectangle).getBounds();
        } catch (NoninvertibleTransformException ex) {
            ExceptionHandler.handle(ex, false);
        }
        assert (false);
        return null;
    }

    /**
     * Transforms a <code>Point</code> from image space to user space the
     * resulting <code>Point</code> will be translated and scaled to match
     * user space coordinates.
     * @param point The <code>Point</code> you wish to transform
     * @return a new <code>Point</code> in user space corresponding to the
     * argument point
     */
    public Point transformToUserSpace(Point point) {
        Point2D floatPoint = transform.transform(point, null);
        return new Point((int)floatPoint.getX(), (int)floatPoint.getY());
    }

    /**
     * Transforms a <code>Dimension</code> from image space to user space the
     * resulting <code>Dimension</code> will be translated and scaled to match
     * user space coordinates.
     * @param dimension The <code>Dimension</code> you wish to transform
     * @return a new <code>Dimension</code> in user space corresponding to the
     * argument point
     */
    public Dimension transformToUserSpace(Dimension dimension) {
        return transform.createTransformedShape(new Rectangle(dimension)).getBounds().getSize();
    }

    /**
     * Transforms an arbitrary <code>Rectangle</code> from image space to user space, translates
     * and scales.
     * @param rectangle The <code>Rectangle</code> you wish to transform
     * @return A new <code>Rectangle</code> corresponding the the argument
     * @see Rectangle
     */
    public Rectangle transformToUserSpace(Rectangle rectangle) {
        return transform.createTransformedShape(rectangle).getBounds();
    }

    /**
     * Adds an overlay to the canvas
     * @param overlay The Overlay you wish to subscribe
     * @see IOverlayable
     * @see IOverlay
     */
    public void subscribe(IOverlay overlay) {
        overlays.add(overlay);
    }

    /**
     * Removes an overlay from the subscribed overlays
     * @param overlay The Overlay you wish to unsubscribe
     * @see IOverlayable
     * @see IOverlay
     */
    public void unsubscribe(IOverlay overlay) {
        overlays.remove(overlay);
    }

    public Point snapToImageSpaceGrid(Point userSpaceLocation) {
        // TODO implement me
        return userSpaceLocation;
    }

    /**
     * Paints the outline of a <code>Rectangle</code> onto this canvas using the
     * underlying image graphics context. The shape will be wrapped around the
     * edges of the canvas. This method is designed to be used by overlays.
     * @param rectangle The <code>Rectangle</code> to draw, bounds expected in image
     * space coordinates
     * @see IOverlay
     */
    public void draw(Rectangle rectangle, Graphics2D g2) {
        //rectangle = transformToImageSpace(rectangle);
        g2.draw(rectangle);

        int width = getWidth();
        int height = getHeight();

        // wrapping horizontal
        // west
        if (rectangle.x < 0) {
            Rectangle wrappedRest = new Rectangle(rectangle.x + width, rectangle.y, rectangle.width, rectangle.height);
            g2.draw(wrappedRest);

            // corner NW
            if (rectangle.y < 0) {
                wrappedRest = new Rectangle(rectangle.x + width, rectangle.y + height, rectangle.width, rectangle.height);
                g2.draw(wrappedRest);
            }

            // corner SW
            else if (rectangle.y + rectangle.height > height) {
                wrappedRest = new Rectangle(rectangle.x + width, -height + rectangle.y, rectangle.width, rectangle.height);
                g2.draw(wrappedRest);
            }
        }

        // east
        else if (rectangle.x + rectangle.width > width) {
            Rectangle wrappedRest = new Rectangle(-width + rectangle.x, rectangle.y, rectangle.width, rectangle.height);
            g2.draw(wrappedRest);

            // corner NE
            if (rectangle.y < 0) {
                wrappedRest = new Rectangle(-width + rectangle.x, rectangle.y + height, rectangle.width, rectangle.height);
                g2.draw(wrappedRest);
            }

            // corner SE
            else if (rectangle.y + rectangle.height > height) {
                wrappedRest = new Rectangle(-width + rectangle.x, -height + rectangle.y, rectangle.width, rectangle.height);
                g2.draw(wrappedRest);
            }
        }

        // wrapping vertical (corners already painted)
        // north
        if (rectangle.y < 0) {
            Rectangle wrappedRest = new Rectangle(rectangle.x, rectangle.y + height, rectangle.width, rectangle.height);
            g2.draw(wrappedRest);
        }

        // south
        else if (rectangle.y + rectangle.height > height) {
            Rectangle wrappedRest = new Rectangle(rectangle.x, -height + rectangle.y, rectangle.width, rectangle.height);
            g2.draw(wrappedRest);
        }
    }

    /**
     * Paints a filled <code>Rectangle</code> onto this canvas using the underlying
     * image graphics context. The shape will be wrapped around the edges of
     * the canvas. This method is designed to be used by overlays.
     * @param rectangle The <code>Rectangle</code> to draw, bounds expected in image
     * space coordinates
     * @see IOverlay
     */
    public void fill(Rectangle rectangle, Graphics2D g2) {
        g2.fill(rectangle);

        int width = getWidth();
        int height = getHeight();

        // wrapping horizontal
        // west
        if (rectangle.x < 0) {
            Rectangle wrappedRest = new Rectangle(rectangle.x + width, rectangle.y, rectangle.width + rectangle.x, rectangle.height);
            g2.fill(wrappedRest);

            // corner NW
            if (rectangle.y < 0) {
                wrappedRest = new Rectangle(rectangle.x + width, rectangle.y + height, rectangle.width + rectangle.x, rectangle.height + rectangle.y);
                g2.fill(wrappedRest);
            }

            // corner SW
            else if (rectangle.y + rectangle.height > height) {
                wrappedRest = new Rectangle(rectangle.x + width, 0, rectangle.width + rectangle.x, rectangle.y + rectangle.height - height);
                g2.fill(wrappedRest);
            }
        }

        // east
        else if (rectangle.x + rectangle.width > width) {
            Rectangle wrappedRest = new Rectangle(0, rectangle.y, rectangle.x + rectangle.width - width, rectangle.height);
            g2.fill(wrappedRest);

            // corner NE
            if (rectangle.y < 0) {
                wrappedRest = new Rectangle(0, rectangle.y + height, rectangle.x + rectangle.width - width, rectangle.height + rectangle.y);
                g2.fill(wrappedRest);
            }

            // corner SE
            else if (rectangle.y + rectangle.height > height) {
                wrappedRest = new Rectangle(0, 0, rectangle.x + rectangle.width - width, rectangle.y + rectangle.height - height);
                g2.fill(wrappedRest);
            }
        }

        // wrapping vertical (corners already painted)
        // north
        if (rectangle.y < 0) {
            Rectangle wrappedRest = new Rectangle(rectangle.x, rectangle.y + height, rectangle.width, rectangle.height + rectangle.y);
            g2.fill(wrappedRest);
        }

        // south
        else if (rectangle.y + rectangle.height > height) {
            Rectangle wrappedRest = new Rectangle(rectangle.x, 0, rectangle.width, rectangle.y + rectangle.height - height);
            g2.fill(wrappedRest);
        }
    }
    
    /**
     * Paints the canvas
     * @param g The graphics context to paint on
     */
    @Override
    public void paintComponent(Graphics g) {

        image = perception.createImage();
        imageG2 = image.createGraphics();

        Graphics2D g2 = (Graphics2D)g;

        g2.clip(scaleToUserSpace(new Rectangle(imageWidth, imageHeight)));
        
        // paint NW
        transform.translate((-1) * imageWidth, (-1) * imageHeight);
        g2.drawRenderedImage(image, transform);
        // paint N
        transform.translate(imageWidth, 0);
        g2.drawRenderedImage(image, transform);
        // paint NE
        transform.translate(imageWidth, 0);
        g2.drawRenderedImage(image, transform);
        // paint E
        transform.translate(0, imageHeight);
        g2.drawRenderedImage(image, transform);
        // paint SE
        transform.translate(0, imageHeight);
        g2.drawRenderedImage(image, transform);
        // paint S
        transform.translate((-1) * imageWidth, 0);
        g2.drawRenderedImage(image, transform);
        // paint SW
        transform.translate((-1) * imageWidth, 0);
        g2.drawRenderedImage(image, transform);
        // paint W
        transform.translate(0, (-1) * imageHeight);
        g2.drawRenderedImage(image, transform);
        // back to normal
        transform.translate(imageWidth, 0);
        g2.drawRenderedImage(image, transform);

        for (IOverlay overlay : overlays) {
            overlay.paint(g2);
        }
    }

    public BufferedImage scaleAndTranslate(BufferedImage image) {
        BufferedImage ret = new BufferedImage((int)(imageWidth * scale), (int)(imageHeight * scale), BufferedImage.TYPE_INT_RGB);
        
        Graphics2D g2 = ret.createGraphics();
        
        g2.clip(scaleToUserSpace(new Rectangle(imageWidth, imageHeight)));

        // paint NW
        transform.translate((-1) * imageWidth, (-1) * imageHeight);
        g2.drawRenderedImage(image, transform);
        // paint N
        transform.translate(imageWidth, 0);
        g2.drawRenderedImage(image, transform);
        // paint NE
        transform.translate(imageWidth, 0);
        g2.drawRenderedImage(image, transform);
        // paint E
        transform.translate(0, imageHeight);
        g2.drawRenderedImage(image, transform);
        // paint SE
        transform.translate(0, imageHeight);
        g2.drawRenderedImage(image, transform);
        // paint S
        transform.translate((-1) * imageWidth, 0);
        g2.drawRenderedImage(image, transform);
        // paint SW
        transform.translate((-1) * imageWidth, 0);
        g2.drawRenderedImage(image, transform);
        // paint W
        transform.translate(0, (-1) * imageHeight);
        g2.drawRenderedImage(image, transform);
        // back to normal
        transform.translate(imageWidth, 0);
        g2.drawRenderedImage(image, transform);

        return ret;
    }

    public BufferedImage translate(BufferedImage image) {
        BufferedImage ret = new BufferedImage((int)(imageWidth), (int)(imageHeight), BufferedImage.TYPE_INT_RGB);

        Graphics2D g2 = ret.createGraphics();

        g2.clip(new Rectangle(imageWidth, imageHeight));

        // paint NW
        translationTransform.translate((-1) * imageWidth, (-1) * imageHeight);
        g2.drawRenderedImage(image, translationTransform);
        // paint N
        translationTransform.translate(imageWidth, 0);
        g2.drawRenderedImage(image, translationTransform);
        // paint NE
        translationTransform.translate(imageWidth, 0);
        g2.drawRenderedImage(image, translationTransform);
        // paint E
        translationTransform.translate(0, imageHeight);
        g2.drawRenderedImage(image, translationTransform);
        // paint SE
        translationTransform.translate(0, imageHeight);
        g2.drawRenderedImage(image, translationTransform);
        // paint S
        translationTransform.translate((-1) * imageWidth, 0);
        g2.drawRenderedImage(image, translationTransform);
        // paint SW
        translationTransform.translate((-1) * imageWidth, 0);
        g2.drawRenderedImage(image, translationTransform);
        // paint W
        translationTransform.translate(0, (-1) * imageHeight);
        g2.drawRenderedImage(image, translationTransform);
        // back to normal
        translationTransform.translate(imageWidth, 0);
        g2.drawRenderedImage(image, translationTransform);

        return ret;
    }
    
}
