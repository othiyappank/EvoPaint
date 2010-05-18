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

package evopaint.commands;

import evopaint.gui.ISelectionManager;
import javax.swing.JOptionPane;

/**
 * Command to set the name of the current selection
 *
 * @author Markus Echterhoff <evopaint@markusechterhoff.com>
 */
public class SetSelectionNameCommand extends AbstractCommand {
    private ISelectionManager selectionManager;

    public SetSelectionNameCommand(ISelectionManager selectionManager) {
        this.selectionManager = selectionManager;
    }

    public void execute() {
        String s = JOptionPane.showInputDialog("Please enter the new name for your selection");
        if (s != null && s.length() > 0) {
            selectionManager.getActiveSelection().setSelectionName(s);
        }
    }

}
