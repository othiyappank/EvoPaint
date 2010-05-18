/*
 *  Copyright (C) 2010 Markus Echterhoff <evopaint@markusechterhoff.com>,
 *                      Daniel Hoelbling (http://www.tigraine.at)
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

package evopaint.commands;

import evopaint.gui.util.WrappingScalableCanvas;
import java.awt.Point;

/**
 * Command to translate the canvas
 * 
 * @author Markus Echterhoff <evopaint@markusechterhoff.com>
 * @author Daniel Hoelbling (http://www.tigraine.at)
 */
public class MoveCommand extends AbstractCommand {

    private WrappingScalableCanvas canvas;
    private Point source;
    private Point destination;

    public WrappingScalableCanvas getCanvas() {
        return canvas;
    }

    public void setCanvas(WrappingScalableCanvas canvas) {
        this.canvas = canvas;
    }

    public Point getSource() {
        return source;
    }

    public void setSource(Point source) {
        this.source = source;
    }

    public Point getDestination() {
        return destination;
    }

    public void setDestination(Point destionation) {
        this.destination = destionation;
    }

    public MoveCommand() {
    }

    public void execute() {
        assert(canvas != null);
        assert(source != null);
        assert(destination != null);

        canvas.translateInUserSpace(source, destination);

        source = destination;
        destination = null;
    }
}

