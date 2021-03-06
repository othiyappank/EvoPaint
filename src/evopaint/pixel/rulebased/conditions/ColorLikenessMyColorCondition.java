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

package evopaint.pixel.rulebased.conditions;

import evopaint.gui.rulesetmanager.JConditionTargetButton;
import evopaint.gui.rulesetmanager.util.DimensionsListener;
import evopaint.gui.rulesetmanager.util.NamedObjectListCellRenderer;
import evopaint.gui.util.AutoSelectOnFocusSpinner;
import evopaint.interfaces.IRandomNumberGenerator;
import evopaint.pixel.rulebased.Condition;
import evopaint.pixel.ColorDimensions;
import evopaint.pixel.rulebased.RuleBasedPixel;
import evopaint.pixel.rulebased.targeting.IConditionTarget;
import evopaint.pixel.rulebased.util.NumberComparisonOperator;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Compares the color of a given pixel to that of the acting pixel
 *
 * @author Markus Echterhoff <evopaint@markusechterhoff.com>
 */
public class ColorLikenessMyColorCondition extends Condition {

    private ColorDimensions dimensions;
    private float compareToLikeness;
    private NumberComparisonOperator comparisonOperator;

    public ColorLikenessMyColorCondition(IConditionTarget target, ColorDimensions dimensions, float compareToLikeness, NumberComparisonOperator comparisonOperator) {
        super(target);
        this.dimensions = dimensions;
        this.compareToLikeness = compareToLikeness;
        this.comparisonOperator = comparisonOperator;
    }

    public ColorLikenessMyColorCondition() {
        dimensions = new ColorDimensions(true, true, true);
        compareToLikeness = 1f;
        comparisonOperator = NumberComparisonOperator.EQUAL;
    }

    public ColorLikenessMyColorCondition(ColorLikenessMyColorCondition colorLikenessConditionMyColor) {
        super(colorLikenessConditionMyColor);
        this.dimensions = colorLikenessConditionMyColor.dimensions;
        this.compareToLikeness = colorLikenessConditionMyColor.compareToLikeness;
        this.comparisonOperator = colorLikenessConditionMyColor.comparisonOperator;
    }

    public ColorLikenessMyColorCondition(IRandomNumberGenerator rng) {
        super(rng);
        this.dimensions = new ColorDimensions(rng);
        this.compareToLikeness = rng.nextFloat();
        this.comparisonOperator = NumberComparisonOperator.getRandom(rng);
    }

    public int getType() {
        return Condition.COLOR_LIKENESS_MY_COLOR;
    }

    @Override
    public int countGenes() {
        return super.countGenes() + dimensions.countGenes() + 2;
    }

    @Override
    public void mutate(int mutatedGene, IRandomNumberGenerator rng) {
        int numGenesSuper = super.countGenes();
        if (mutatedGene < numGenesSuper) {
            super.mutate(mutatedGene, rng);
            return;
        }
        mutatedGene -= numGenesSuper;

        int numGenesDimensions = dimensions.countGenes();
        if (mutatedGene < numGenesDimensions) {
            dimensions = new ColorDimensions(dimensions);
            dimensions.mutate(mutatedGene, rng);
            return;
        }
        mutatedGene -= numGenesDimensions;

        switch (mutatedGene) {
            case 0: comparisonOperator = NumberComparisonOperator.getRandomOtherThan(comparisonOperator, rng);
            return;
            case 1: compareToLikeness = rng.nextFloat();
            return;
        }

        assert false; // we have an error in the mutatedGene calculation
    }

    @Override
    public void mixWith(Condition theirCondition, float theirShare, IRandomNumberGenerator rng) {
        super.mixWith(theirCondition, theirShare, rng);
        ColorLikenessMyColorCondition c = (ColorLikenessMyColorCondition)theirCondition;
        if (rng.nextFloat() < theirShare) {
            dimensions = new ColorDimensions(dimensions);
            dimensions.mixWith(c.dimensions, theirShare, rng);
        }
        if (rng.nextFloat() < theirShare) {
            compareToLikeness = c.compareToLikeness;
        }
        if (rng.nextFloat() < theirShare) {
            comparisonOperator = c.comparisonOperator;
        }
    }

    public float getCompareToLikeness() {
        return compareToLikeness;
    }

    public void setCompareToLikeness(float compareToLikeness) {
        this.compareToLikeness = compareToLikeness;
    }

    public NumberComparisonOperator getComparisonOperator() {
        return comparisonOperator;
    }

    public void setComparisonOperator(NumberComparisonOperator comparisonOperator) {
        this.comparisonOperator = comparisonOperator;
    }

    public ColorDimensions getDimensions() {
        return dimensions;
    }

    public void setDimensions(ColorDimensions dimensions) {
        this.dimensions = dimensions;
    }

    public String getName() {
        return "color likeness (compared to me)";
    }

    public boolean isMet(RuleBasedPixel actor, RuleBasedPixel target) {
        if (target == null) { // never forget to skip empty spots
            return false;
        }
        double distance = target.getPixelColor().distanceTo(actor.getPixelColor(), dimensions);
        float likenessPercentage = (float)(1 - distance);
        return comparisonOperator.compare(likenessPercentage, compareToLikeness);
    }

    @Override
    public String toString() {
        String conditionString = new String();
        conditionString += "color of ";
        conditionString += getTarget().toString();
        conditionString += " ";
        conditionString += comparisonOperator.toString();
        conditionString += " ";
        conditionString += Math.round(compareToLikeness * 100);
        conditionString += "% like my color";
        conditionString += " (dimensions: ";
        conditionString += dimensions.toString();
        conditionString += ")";
        return conditionString;
    }

    public String toHTML() {
        String conditionString = new String();
        conditionString += "color of ";
        conditionString += getTarget().toHTML();
        conditionString += " ";
        conditionString += comparisonOperator.toHTML();
        conditionString += " ";
        conditionString += Math.round(compareToLikeness * 100);
        conditionString += "% like my color";
        conditionString += " <span style='color: #777777;'>(dimensions: ";
        conditionString += dimensions.toHTML();
        conditionString += ")</span>";
        return conditionString;
    }

    public LinkedHashMap<String,JComponent> addParametersGUI(LinkedHashMap<String,JComponent> parametersMap) {
        JConditionTargetButton JConditionTargetButton = new JConditionTargetButton(this);
        parametersMap.put("Target", JConditionTargetButton);

        JPanel dimensionsPanel = new JPanel();
        JToggleButton btnH = new JToggleButton("H");
        btnH.setToolTipText("Select to include the hue in color-comparison");
        JToggleButton btnS = new JToggleButton("S");
        btnS.setToolTipText("Select to include the saturation in color-comparison");
        JToggleButton btnB = new JToggleButton("B");
        btnB.setToolTipText("Select to include the brightness in color-comparison");
        DimensionsListener dimensionsListener = new DimensionsListener(dimensions, btnH, btnS, btnB);
        btnH.addActionListener(dimensionsListener);
        btnS.addActionListener(dimensionsListener);
        btnB.addActionListener(dimensionsListener);
        if (dimensions.hue) {
            btnH.setSelected(true);
        }
        if (dimensions.saturation) {
            btnS.setSelected(true);
        }
        if (dimensions.brightness) {
            btnB.setSelected(true);
        }
        dimensionsPanel.add(btnH);
        dimensionsPanel.add(btnS);
        dimensionsPanel.add(btnB);
        parametersMap.put("Dimensions", dimensionsPanel);

        JComboBox comparisonComboBox = new JComboBox(NumberComparisonOperator.createComboBoxModel());
        comparisonComboBox.setRenderer(new NamedObjectListCellRenderer());
        comparisonComboBox.setSelectedItem(comparisonOperator);
        comparisonComboBox.addActionListener(new ComparisonListener());
        parametersMap.put("Comparison", comparisonComboBox);

        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(Math.round(compareToLikeness * 100), 0, 100, 1);
        JSpinner likenessPercentageSpinner = new AutoSelectOnFocusSpinner(spinnerModel);
        likenessPercentageSpinner.addChangeListener(new PercentageListener());
        likenessPercentageSpinner.setToolTipText("<html>A likeness of 100% means \"exactly this color\".<br />" +
                "Note that 50% color-likeness will match a lot more colors than you might think.<br />" +
                "To test color matching, create a rule set like:<br />" +
                "<span style='font-style: italic;'>\"if my color is not 90% like blue, then change my energy by -100 (the starting energy)\"</span><br />" +
                "and see which pixels survive the test.");
        parametersMap.put("Likeness in %", likenessPercentageSpinner);

        return parametersMap;
    }

    private class ComparisonListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            setComparisonOperator((NumberComparisonOperator) ((JComboBox) (e.getSource())).getSelectedItem());
        }
    }

    private class PercentageListener implements ChangeListener {

        public void stateChanged(ChangeEvent e) {
            compareToLikeness = 
                    ((Integer)((JSpinner)e.getSource()).getValue()).floatValue() / 100;
        }
    }
}
