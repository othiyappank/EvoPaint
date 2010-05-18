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

package evopaint.gui;

import evopaint.Configuration;
import evopaint.gui.util.IOverlay;
import evopaint.gui.util.WrappingScalableCanvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

/**
 * The brush indicator overlay is the rectangle drawn around the position
 * of the cursor when hovering the canvas. Note that the origin of this
 * rectangle is the origin of the curser minus half the width and height
 * respectively. All calculations rounded down.
 *
 * @author Markus Echterhoff <evopaint@markusechterhoff.com>
 */
public class BrushIndicatorOverlay extends Rectangle implements IOverlay {
    private Configuration configuration;
    private WrappingScalableCanvas canvas;
    private Point unsnappedLocation;

    public BrushIndicatorOverlay(Configuration configuration, WrappingScalableCanvas canvas) {
        this.configuration = configuration;
        this.canvas = canvas;
        this.unsnappedLocation = new Point(0, 0);
    }

    @Override
    public void setLocation(Point location) {
        super.setLocation(location);
        this.unsnappedLocation = location;
    }

    public void paint(Graphics2D g2) {

        // prepare soft-xor painting
        g2.setXORMode(new Color(0xDDDDDD));

        // this would make the overlay look transparent white
        // imageG2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .5f));
        // imageG2.setColor(Color.white);

        // re-center
        x = (int)(unsnappedLocation.x - (configuration.brush.size) * canvas.getScale() / 2);
        y = (int)(unsnappedLocation.y - (configuration.brush.size) * canvas.getScale() / 2);

        // snap to unzoomed pixels, not that we cannot call setLocation()
        // because we have overridden it to set unsnappedLocation
        Point snappedLocation = canvas.snapToImageSpaceGrid(getLocation());
        x = snappedLocation.x;
        y = snappedLocation.y;

        // pull current size and use it for this overlay
        setSize(canvas.transformToUserSpace(new Dimension(configuration.brush.size, configuration.brush.size)));

        canvas.fill(this, g2);
    }
}
