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
import javax.swing.JFrame;

/**
 *
 * @author Markus Echterhoff <evopaint@markusechterhoff.com>
 */
public class WorldFrame extends JFrame {

    private Showcase showcase;
    private Configuration configuration;

    public WorldFrame(Configuration configuration) {
        this.showcase = new Showcase(configuration);
        add(showcase);
    }

}
