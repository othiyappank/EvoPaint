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

package evopaint.util;

import evopaint.pixel.rulebased.RuleSetCollection;
import evopaint.pixel.rulebased.interfaces.INamed;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Used by the JTree that holds the data of the rule set browser, this class
 * represents a collection node.
 *
 * @author Markus Echterhoff <evopaint@markusechterhoff.com>
 */
public class CollectionNode extends DefaultMutableTreeNode implements INamed {

    public CollectionNode(RuleSetCollection collection) {
        super(collection, true);
    }

    public String getName() {
        return ((INamed)getUserObject()).getName();
    }

    @Override
    public boolean isLeaf() {
        return false;
    }
}
