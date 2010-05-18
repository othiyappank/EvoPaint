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
package evopaint.gui;

import evopaint.Configuration;
import evopaint.commands.*;
import evopaint.interfaces.IChangeListener;
import evopaint.util.ExceptionHandler;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * The MenuBar of EvoPaint
 * 
 * @author Markus Echterhoff <evopaint@markusechterhoff.com>
 * @author Daniel Hoelbling (http://www.tigraine.at)
 * @author Augustin Malle
 */
public class MenuBar extends JMenuBar { // implements Observer {

    private Configuration configuration;
    private Showcase showcase;

    public MenuBar(final Configuration configuration, Showcase showcase) {
        this.configuration = configuration;
        this.showcase = showcase;

        setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();

        //showcase.getCurrentSelections().addObserver(this);
        // World Menu
        JMenu worldMenu = new JMenu();
        worldMenu.setText("World");
        add(worldMenu, c);

        // File Menu Items        
        JMenuItem newItem = new JMenuItem();
        newItem.setText("New");
        newItem.addActionListener(new NewWorldCommand());
        worldMenu.add(newItem);

        JMenuItem load = new JMenuItem("Load...");
        load.addActionListener(new LoadCommand(configuration));
        worldMenu.add(load);
        JMenuItem save = new JMenuItem("Save");
        save.addActionListener(new SaveCommand(configuration));
        worldMenu.add(save);
        JMenuItem saveAs = new JMenuItem("Save as...");
        saveAs.addActionListener(new SaveAsCommand(configuration));
        worldMenu.add(saveAs);
        JMenuItem importMenu = new JMenuItem("Import...");
        importMenu.addActionListener(showcase.getImportCommand());
        worldMenu.add(importMenu);

        JMenuItem exportItem = new JMenuItem();
        exportItem.setText("Export");
        exportItem.addActionListener(new ExportDialog(configuration));

        worldMenu.add(exportItem);

        JMenuItem opt = new JMenuItem("Options...");
        opt.addActionListener(new ShowConfigurationDialogCommand(configuration));
        worldMenu.add(opt);

        JMenuItem endItem = new JMenuItem();
        endItem.setText("End");
        endItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (JOptionPane.showConfirmDialog(getRootPane(), "Are you sure you want the world to end?", "End the world", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });
        worldMenu.add(endItem);

        // selection menu
        JMenu selectionMenu = new JMenu("Selection");
        c.gridx = 1;
        add(selectionMenu, c);


        JMenuItem unselect = new JMenuItem("Unselect");
        unselect.addActionListener(new UnselectCommand(configuration));
        selectionMenu.add(unselect);

        JMenuItem selectionSetName = new JMenuItem("Set Name...");
        selectionMenu.add(selectionSetName);
        selectionSetName.addActionListener(new SetSelectionNameCommand(showcase));

        JMenuItem openAsNew = new JMenuItem("Open as new");
        openAsNew.addActionListener(new SelectionOpenAsNewCommand(configuration));
        selectionMenu.add(openAsNew);
        JMenuItem copySelection = new JMenuItem("Copy");
        copySelection.addActionListener(showcase.getCopySelectionCommand());
        selectionMenu.add(copySelection);

        JMenuItem pasteSelection = new JMenuItem("Paste");
        pasteSelection.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                configuration.mainFrame.setActiveTool(CopySelectionCommand.class);
            }
        });
        selectionMenu.add(pasteSelection);

        JMenuItem deleteCurrentSelection = new JMenuItem("Delete");
        selectionMenu.add(deleteCurrentSelection);
        deleteCurrentSelection.addActionListener(new DeleteCurrentSelectionCommand(showcase));

        JMenuItem clearSelections = new JMenuItem("Delete All");
        clearSelections.addActionListener(new DeleteAllSelectionsCommand(showcase));
        selectionMenu.add(clearSelections);

        // info menu
        JMenu infoMenu = new JMenu();
        infoMenu.setText("Info");
        c.gridx = 2;
        add(infoMenu, c);

        JMenuItem userGuide = new JMenuItem();
        userGuide.setText("User Guide");
        userGuide.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                //.getDesktop().browse(new URI("http://www.your.url"));
                try {
                    java.awt.Desktop.getDesktop().browse(new URI(Configuration.USER_GUIDE_URL));
                } catch (IOException e1) {
                    ExceptionHandler.handle(e1, false);
                } catch (URISyntaxException e1) {
                    ExceptionHandler.handle(e1, false);
                }
            }
        });
        infoMenu.add(userGuide);

        JMenuItem getCode = new JMenuItem();
        getCode.setText("Get the source code");
        getCode.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                try {
                    java.awt.Desktop.getDesktop().browse(new URI(Configuration.CODE_DOWNLOAD_URL));
                } catch (URISyntaxException e1) {
                    ExceptionHandler.handle(e1, false);
                } catch (IOException e1) {
                    ExceptionHandler.handle(e1, false);
                }
            }
        });
        infoMenu.add(getCode);

        JMenuItem about = new JMenuItem();
        about.setText("About");
        about.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                JAboutDialog aboutDialog = new JAboutDialog(configuration.mainFrame);
                aboutDialog.pack();
                aboutDialog.setVisible(true);
            }
        });
        infoMenu.add(about);



        final JMenu modeMenu = new JMenu("Mode: Agent Simulation");

        JRadioButtonMenuItem menuRadioAgentSimulation = new JRadioButtonMenuItem("Agent Simulation", true);
        menuRadioAgentSimulation.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                configuration.world.addChangeListener(new IChangeListener() {

                    public void changed() {
                        configuration.operationMode = Configuration.OPERATIONMODE_AGENT_SIMULATION;
                        modeMenu.setText("Mode: Agent Simulation");
                        configuration.world.reset();
                    }
                });
            }
        });
        menuRadioAgentSimulation.setToolTipText("<html>During each time frame each pixel will act once.<br />"
                + "Pixels act in a different, random order each time frame.<br />"
                + "The effects of an action will be seen by all pixels immediately.<br />"
                + "So during a single time frame the pixel who acts first can influence the<br />"
                + "descision of a neighbor acting after him or even remove the neighbor alltogether.</html>");
        modeMenu.add(menuRadioAgentSimulation);

        JRadioButtonMenuItem menuRadioCellularAutomaton = new JRadioButtonMenuItem("Cellular Automaton", false);
        menuRadioCellularAutomaton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                configuration.world.addChangeListener(new IChangeListener() {

                    public void changed() {
                        configuration.operationMode = Configuration.OPERATIONMODE_CELLULAR_AUTOMATON;
                        modeMenu.setText("Mode: Cellular Automaton");
                        configuration.world.reset();
                    }
                });
            }
        });
        menuRadioCellularAutomaton.setToolTipText("<html>Each time frame consists of a snapshot of the world.<br />"
                + "Each pixel can then change itself once according to its environment.<br />"
                + "The changed pixels are used to construct the subsequent snapshot.<br />"
                + "Please note that for increased performance there are no restrictions in place that would<br />"
                + "prevent you from modifying neighbors but doing so will not yield the desired results.<br />"
                + "(Unintentional SE-directed patterns would be created due to the lack of randomization)</html>");
        modeMenu.add(menuRadioCellularAutomaton);

        ButtonGroup modeGroup = new ButtonGroup();
        modeGroup.add(menuRadioAgentSimulation);
        modeGroup.add(menuRadioCellularAutomaton);

        c.gridx = 3;
        c.weightx = 1;
        c.anchor = GridBagConstraints.EAST;
        add(modeMenu, c);

    }
   
}
