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

package evopaint.pixel.rulebased.interfaces;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JComponent;

/**
 * Classes that implement this interfaces have parameters that have to be added
 * to string/html or GUI representations
 *
 * @author Markus Echterhoff <evopaint@markusechterhoff.com>
 */
public interface IParameterized {
    public Map<String, String>addParametersString(Map<String, String> parametersMap);
    public Map<String, String>addParametersHTML(Map<String, String> parametersMap);
    public LinkedHashMap<String, JComponent> addParametersGUI(LinkedHashMap<String, JComponent> parametersMap);
}
