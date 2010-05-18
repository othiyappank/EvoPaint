/*
 *  Copyright (C) 2010 Markus Echterhoff <evopaint@markusechterhoff.com>,
 *                      Daniel Hoelbling (http://www.tigraine.at)
 *
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

package evopaint;

import evopaint.pixel.rulebased.Action;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Wraps most important data about the EvoPaint World for serialization 
 *
 * @author Daniel Hoelbling (http://www.tigraine.at)
 */
public class SaveWrapper {
    public World world;
    private Dimension dimension = new Dimension(300, 300);
    public int backgroundColor = 0;
    public int startingEnergy = 100;
    public double mutationRate = 0.01; // NOTE: it seems the lowest double > 0 generated by the rng is 10^(-18): 0.000000000000000001
    public int operationMode;
    private List<Action> usedActions;

    public SaveWrapper(Configuration config) {
        world = config.world;
        dimension = config.getDimension();
        backgroundColor = config.backgroundColor;
        startingEnergy = config.startingEnergy;
        mutationRate = config.mutationRate;
        operationMode = config.operationMode;
        usedActions = config.usedActions;
    }

    public void Apply(Configuration config) {
        config.setDimension(dimension);
        config.backgroundColor = backgroundColor;
        config.startingEnergy = startingEnergy;
        config.mutationRate = mutationRate;
        config.operationMode = operationMode;
        world.resetPendingOperations();
        config.world = world;
        config.usedActions = usedActions;
        config.world.setConfiguration(config);
        config.runLevel = Configuration.RUNLEVEL_RUNNING;
    }
}
