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


import evopaint.Configuration;
import evopaint.Selection;
import evopaint.gui.ISelectionManager;
import evopaint.interfaces.IChangeListener;


import java.awt.Point;
import java.awt.Rectangle;

/**
 * Command used by the Paint Tool to paint onto the canvas
 * 
 * @author Markus Echterhoff <evopaint@markusechterhoff.com>
 * @author Daniel Hoelbling (http://www.tigraine.at)
 */
public class PaintCommand extends AbstractCommand {
    private Configuration configuration;
    private Point location;
    private final ISelectionManager selectionManager;

    public Point getLocation() {
        return location;
    }

    public void setLocation(Point location) {
        this.location = location;
    }

    public PaintCommand(Configuration configuration, ISelectionManager selectionManager) {
        this.configuration = configuration;
        this.selectionManager = selectionManager;
    }

    public void execute() {
        new Thread() {
            @Override
            public void run() {
                configuration.world.addChangeListener(new IChangeListener() {
                    public void changed() {

                        Selection activeSelection = selectionManager.getActiveSelection();
                        if (activeSelection != null) {
                            Rectangle rectangle = activeSelection.getImageSpaceBounds();
                            int brushSize = configuration.brush.size / 2;

                            if (location.x - brushSize < rectangle.x)
                                    location.x = rectangle.x + brushSize;
                            if (location.x + brushSize > rectangle.x + rectangle.width)
                                    location.x = rectangle.x + rectangle.width - brushSize;
                            if (location.y - brushSize < rectangle.y)
                                    location.y = rectangle.y + brushSize;
                            if (location.y + brushSize > rectangle.y + rectangle.height)
                                    location.y = rectangle.y + rectangle.height - brushSize;
                        }
                        
                        configuration.brush.paint(location.x, location.y);
                    }
                });
            }
        }.start();
    }

}

