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

import evopaint.*;
import evopaint.interfaces.IChangeListener;
import evopaint.pixel.rulebased.RuleBasedPixel;
import evopaint.util.mapping.AbsoluteCoordinate;

import java.awt.*;

/**
 * Open current selection in a new thread and window
 *
 * @author Markus Echterhoff <evopaint@markusechterhoff.com>
 * @author Daniel Hoelbling (http://www.tigraine.at)
 */
public class SelectionOpenAsNewCommand extends AbstractCommand {
    private Configuration config;

    public SelectionOpenAsNewCommand(Configuration config) {
        this.config = config;
    }

    @Override
    public void execute() {
        final Configuration oldConfig = config;
        int runlevel = oldConfig.runLevel;
        oldConfig.runLevel = Configuration.RUNLEVEL_STOP;
        Thread t = new Thread() {
            @Override
            public void run() {
                config.world.addChangeListener(new IChangeListener() {

                    public void changed() {
                        Selection activeSelection = oldConfig.mainFrame.getShowcase().getActiveSelection();
                        if (activeSelection == null) return;

                        EvoPaint evoPaint = new EvoPaint();
                        Configuration newConfig = evoPaint.getConfiguration();
                        newConfig.setDimension(oldConfig.getDimension());
                        newConfig.backgroundColor = oldConfig.backgroundColor;
                        newConfig.startingEnergy = oldConfig.startingEnergy;
                        newConfig.mutationRate = oldConfig.mutationRate;
                        newConfig.operationMode = oldConfig.operationMode;
                        newConfig.usedActions = oldConfig.usedActions;
                        newConfig.world.resetPendingOperations();
                        newConfig.world = new World(newConfig);

                        Rectangle rectangle = activeSelection.getImageSpaceBounds();
                        newConfig.setDimension(rectangle.getSize());
                        for(int x = 0; x < activeSelection.getImageSpaceBounds().width; x++) {
                            for(int y = 0; y < activeSelection.getImageSpaceBounds().height; y++) {
                                RuleBasedPixel ruleBasedPixel = oldConfig.world.get(x + activeSelection.getImageSpaceBounds().x, y + activeSelection.getImageSpaceBounds().y);
                                if (ruleBasedPixel == null) continue;
                                newConfig.world.set(new RuleBasedPixel(ruleBasedPixel.getPixelColor(), new AbsoluteCoordinate(x, y, newConfig.world), ruleBasedPixel.getEnergy(), ruleBasedPixel.getRules()));
                            }
                        }

                        newConfig.runLevel = Configuration.RUNLEVEL_RUNNING;
                        evoPaint.start();
                    }
                });
            }
        };
        t.start();

        oldConfig.runLevel = runlevel;
    }
}
