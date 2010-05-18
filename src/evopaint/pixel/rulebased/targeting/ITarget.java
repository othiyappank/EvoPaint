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

package evopaint.pixel.rulebased.targeting;

import evopaint.interfaces.IRandomNumberGenerator;
import evopaint.pixel.rulebased.interfaces.IHTML;
import java.io.Serializable;

/**
 * All targets should implement this interface
 *
 * @author Markus Echterhoff <evopaint@markusechterhoff.com>
 */
public interface ITarget extends IHTML, Serializable {
    public int getType();
    public int countGenes();
    public void mutate(int mutatedGene, IRandomNumberGenerator rng);
}
