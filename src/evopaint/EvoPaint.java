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

package evopaint;

import evopaint.util.ExceptionHandler;

/**
 * Main class of EvoPaint, 'nuff said.
 *
 * @author Markus Echterhoff <evopaint@markusechterhoff.com>
 */
public class EvoPaint extends Thread {

    public Configuration getConfiguration() {
        return configuration;
    }

    private Configuration configuration;

    public EvoPaint() {
        this.configuration = new Configuration();
    }

    @Override
    public void run() {
        configuration.perception.updateImageBuffer();
        configuration.mainFrame.getShowcase().repaint();
        long timeStamp = System.currentTimeMillis();

        while (true) {

            configuration.world.step();

            long currentTime = System.currentTimeMillis();
            if (currentTime - timeStamp > 1000 / configuration.fps) {
                timeStamp = currentTime;
                configuration.perception.updateImageBuffer();
                configuration.mainFrame.getShowcase().repaint();
            }
            else if (configuration.runLevel == Configuration.RUNLEVEL_PAINTING_ONLY) {
                try {
                    Thread.sleep(currentTime - timeStamp);
                } catch (InterruptedException ex) {
                }
            }
        }
    }

    public static void main(String args[]) {
        System.setProperty("sun.awt.exception.handler", ExceptionHandler.class.getName());
        try {
            new EvoPaint().start();
        } catch (Throwable t) {
            ExceptionHandler.handle(t, true);
        }
    }

}
