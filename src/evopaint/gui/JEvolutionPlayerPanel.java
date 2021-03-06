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
import evopaint.commands.LoadCommand;
import evopaint.commands.NewWorldCommand;
import evopaint.commands.ResetWorldCommand;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;

/**
 * Class of the "player"-style panel to control the evolution
 * 
 * @author Markus Echterhoff <evopaint@markusechterhoff.com>
 */
public class JEvolutionPlayerPanel extends JPanel {
    private Configuration configuration;

    public JEvolutionPlayerPanel(final Configuration configuration) {
        this.configuration = configuration;
        
        setLayout(new FlowLayout(FlowLayout.LEFT, 3, 0));

        JToggleButton recordButton = new JToggleButton();
        recordButton.setRolloverEnabled(true);
        recordButton.setPreferredSize(new Dimension(24, 24));
        recordButton.setContentAreaFilled(false);
        recordButton.setIcon(new ImageIcon(getClass().getResource("icons/evolution-record.png")));
        recordButton.setRolloverIcon(new ImageIcon(getClass().getResource("icons/evolution-record-rollover.png")));
        recordButton.setPressedIcon(new ImageIcon(getClass().getResource("icons/evolution-record-pressed.png")));
        recordButton.setSelectedIcon(new ImageIcon(getClass().getResource("icons/evolution-record-selected.png")));
        recordButton.setRolloverSelectedIcon(new ImageIcon(getClass().getResource("icons/evolution-record-rollover-selected.png")));
        recordButton.setToolTipText("Records a video of your evolution");
        add(recordButton);

        JToggleButton playButton = new JToggleButton();
        playButton.setRolloverEnabled(true);
        playButton.setPreferredSize(new Dimension(24, 24));
        playButton.setContentAreaFilled(false);
        playButton.setIcon(new ImageIcon(getClass().getResource("icons/evolution-play.png")));
        playButton.setRolloverIcon(new ImageIcon(getClass().getResource("icons/evolution-play-rollover.png")));
        playButton.setPressedIcon(new ImageIcon(getClass().getResource("icons/evolution-play-pressed.png")));
        playButton.setSelectedIcon(new ImageIcon(getClass().getResource("icons/evolution-play-selected.png")));
        playButton.setRolloverSelectedIcon(new ImageIcon(getClass().getResource("icons/evolution-play-rollover-selected.png")));
        playButton.setToolTipText("Resumes the evolution");
        add(playButton);

        JToggleButton pauseButton = new JToggleButton(new ImageIcon(getClass().getResource("icons/evolution-pause.png")));
        pauseButton.setRolloverEnabled(true);
        pauseButton.setPreferredSize(new Dimension(24, 24));
        pauseButton.setContentAreaFilled(false);
        pauseButton.setIcon(new ImageIcon(getClass().getResource("icons/evolution-pause.png")));
        pauseButton.setRolloverIcon(new ImageIcon(getClass().getResource("icons/evolution-pause-rollover.png")));
        pauseButton.setPressedIcon(new ImageIcon(getClass().getResource("icons/evolution-pause-pressed.png")));
        pauseButton.setSelectedIcon(new ImageIcon(getClass().getResource("icons/evolution-pause-selected.png")));
        pauseButton.setRolloverSelectedIcon(new ImageIcon(getClass().getResource("icons/evolution-pause-rollover-selected.png")));
        pauseButton.setToolTipText("Pauses evolution, but keeps painting and recording");
        add(pauseButton);

        JToggleButton stopButton = new JToggleButton(new ImageIcon(getClass().getResource("icons/evolution-stop.png")));
        stopButton.setRolloverEnabled(true);
        stopButton.setPreferredSize(new Dimension(24, 24));
        stopButton.setContentAreaFilled(false);
        stopButton.setIcon(new ImageIcon(getClass().getResource("icons/evolution-stop.png")));
        stopButton.setRolloverIcon(new ImageIcon(getClass().getResource("icons/evolution-stop-rollover.png")));
        stopButton.setPressedIcon(new ImageIcon(getClass().getResource("icons/evolution-stop-pressed.png")));
        stopButton.setSelectedIcon(new ImageIcon(getClass().getResource("icons/evolution-stop-selected.png")));
        stopButton.setRolloverSelectedIcon(new ImageIcon(getClass().getResource("icons/evolution-stop-rollover-selected.png")));
        stopButton.setToolTipText("Pauses evolution, painting and recording alltogether in case you need your CPU");
        add(stopButton);

        ButtonGroup group = new ButtonGroup();
        group.add(playButton);
        group.add(pauseButton);
        group.add(stopButton);

        final JButton ejectButton = new JButton(new ImageIcon(getClass().getResource("icons/evolution-eject.png")));
        ejectButton.setRolloverEnabled(true);
        ejectButton.setPreferredSize(new Dimension(24, 24));
        ejectButton.setContentAreaFilled(false);
        ejectButton.setIcon(new ImageIcon(getClass().getResource("icons/evolution-eject.png")));
        ejectButton.setRolloverIcon(new ImageIcon(getClass().getResource("icons/evolution-eject-rollover.png")));
        ejectButton.setPressedIcon(new ImageIcon(getClass().getResource("icons/evolution-eject-pressed.png")));
        ejectButton.setToolTipText("Lets you choose to open/create an evolution or reset your current one");
        add(ejectButton);

        playButton.setSelected(true);

        recordButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                JToggleButton source = ((JToggleButton)e.getSource());
                if (source.isSelected()) {
                    if (false == configuration.perception.startRecording()) {
                        source.setSelected(false);
                    }
                } else {
                    configuration.perception.stopRecording();
                }

            }
        });

        playButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                configuration.runLevel = Configuration.RUNLEVEL_RUNNING;
            }
        });

        pauseButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                configuration.runLevel = Configuration.RUNLEVEL_PAINTING_ONLY;
            }
        });

        stopButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                configuration.runLevel = Configuration.RUNLEVEL_STOP;
            }
        });

        ejectButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                JPopupMenu ejectMenu = new JPopupMenu();
                ejectMenu.add(new JMenuItem("Reset") {{
                    addActionListener(new ActionListener() {

                            public void actionPerformed(ActionEvent e) {
                                ResetWorldCommand rwc = new ResetWorldCommand(configuration);
                                rwc.execute();
                            }
                        });
                }});
                ejectMenu.add(new JMenuItem("New") {{
                    addActionListener(new NewWorldCommand());
                }});
                ejectMenu.add(new JMenuItem("Open") {{
                    addActionListener(new LoadCommand(configuration));
                }});
                ejectMenu.show(ejectButton, 0, 25);
            }
        });
    }

}
