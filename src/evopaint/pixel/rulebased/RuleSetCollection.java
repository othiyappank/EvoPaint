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

package evopaint.pixel.rulebased;

import evopaint.pixel.rulebased.interfaces.IDescribable;
import evopaint.pixel.rulebased.interfaces.INameable;
import java.io.Serializable;

/**
 * Collections are named and described containers for rule sets. Note that the
 * contained rule sets are not reflected in this class as they are part of the
 * tree structure, both on disk and in the rule set browser.
 *
 * @author Markus Echterhoff <evopaint@markusechterhoff.com>
 */
public class RuleSetCollection implements Serializable, INameable, IDescribable {
    private String name;
    private String description;

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public RuleSetCollection(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public RuleSetCollection(RuleSetCollection collection) {
        this.name = collection.name;
        this.description = collection.description;
    }

}
