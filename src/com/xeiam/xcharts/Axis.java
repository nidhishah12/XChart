/*******************************************************************************
 * Copyright (c) 2008-2011 SWTChart project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.xeiam.xcharts;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;

import com.xeiam.xcharts.interfaces.IChartPart;

/**
 * Axis
 * 
 * @author timmolter
 */
public class Axis implements IChartPart {

    /** the chart */
    private Chart chart;

    /** the axisPair */
    private AxisPair axisPair;

    /** the axis title */
    private AxisTitle axisTitle;

    /** the axis tick */
    private AxisTick axisTick;

    /** the grid */
    private AxisLine axisLine;

    /** the axis direction */
    private Direction direction;

    private double min = Double.MAX_VALUE;

    private double max = Double.MIN_VALUE;

    /** the bounds */
    private Rectangle bounds = new Rectangle(); // default all-zero rectangle

    /** the paint zone */
    private Rectangle paintZone = new Rectangle(); // default all-zero rectangle

    /** An axis direction */
    public enum Direction {

        /** the constant to represent X axis */
        X,

        /** the constant to represent Y axis */
        Y
    }

    /**
     * Constructor
     * 
     * @param direction the axis direction (X or Y)
     * @param chart the chart
     */
    public Axis(Chart chart, AxisPair axisPair, Direction direction) {

        this.chart = chart;
        this.axisPair = axisPair;
        this.direction = direction;

        axisTitle = new AxisTitle(this);
        axisTick = new AxisTick(this);
        axisLine = new AxisLine(this);
    }

    /**
     * @param data
     */
    public void addMinMax(double min, double max) {

        if (min < this.min) {
            this.min = min;
        }
        if (max > this.max) {
            this.max = max;
        }
    }

    public Direction getDirection() {
        return direction;
    }

    @Override
    public Rectangle getBounds() {
        return bounds;
    }

    public Rectangle getPaintZone() {
        return paintZone;
    }

    protected AxisTitle getAxisTitle() {
        return axisTitle;
    }

    public void setAxisTitle(String title) {
        this.axisTitle.setText(title);
    }

    public void setAxisTitle(AxisTitle axisTitle) {
        this.axisTitle = axisTitle;
    }

    public AxisTick getAxisTick() {
        return axisTick;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    /**
     * 
     */
    public int getSizeHint() {

        if (direction == Direction.X) { // X-Axis

            // Axis title
            double titleHeight = 0;
            if (axisTitle.isVisible) {
                TextLayout textLayout = new TextLayout(axisTitle.getText(), axisTitle.getFont(), new FontRenderContext(null, true, false));
                Rectangle rectangle = textLayout.getPixelBounds(null, 0, 0);
                titleHeight = rectangle.getHeight() + AxisTitle.AXIS_TITLE_PADDING;
            }

            // Axis tick labels
            TextLayout textLayout = new TextLayout("0", axisTick.getAxisTickLabels().getFont(), new FontRenderContext(null, true, false));
            Rectangle rectangle = textLayout.getPixelBounds(null, 0, 0);
            double axisTickLabelsHeight = rectangle.getHeight();

            double gridStrokeWidth = axisLine.getStroke().getLineWidth();
            return (int) (titleHeight + axisTickLabelsHeight + AxisTick.AXIS_TICK_PADDING + AxisTickMarks.TICK_LENGTH + gridStrokeWidth + Plot.PLOT_PADDING);
        } else { // Y-Axis
            return 0; // We layout the yAxis first depending in the xAxis height hint. We don't care about the yAxis height hint
        }
    }

    @Override
    public void paint(Graphics2D g) {

        // determine Axis bounds
        if (direction == Direction.Y) { // Y-Axis

            // calculate paint zone
            // ----
            // |
            // |
            // |
            // |
            // ----
            int xOffset = Chart.CHART_PADDING;
            int yOffset = (int) (axisPair.getChartTitleBounds().getY() + axisPair.getChartTitleBounds().getHeight() + Chart.CHART_PADDING);
            int width = 80; // arbitrary, final width depends on Axis tick labels
            int height = chart.getHeight() - yOffset - axisPair.getXAxis().getSizeHint() - Chart.CHART_PADDING;
            Rectangle yAxisRectangle = new Rectangle(xOffset, yOffset, width, height);
            this.paintZone = yAxisRectangle;
            // g.setColor(Color.green);
            // g.draw(yAxisRectangle);

            // fill in Axis with sub-components
            axisTitle.paint(g);
            axisTick.paint(g);
            axisLine.paint(g);

            xOffset = (int) paintZone.getX();
            yOffset = (int) paintZone.getY();
            width = (int) (axisTitle.isVisible ? axisTitle.getBounds().getWidth() : 0) + (int) axisTick.getBounds().getWidth() + (int) axisLine.getBounds().getWidth();
            height = (int) paintZone.getHeight();
            bounds = new Rectangle(xOffset, yOffset, width, height);
            // g.setColor(Color.yellow);
            // g.draw(bounds);

        } else { // X-Axis

            // calculate paint zone
            // |____________________|

            int xOffset = (int) (axisPair.getYAxis().getBounds().getWidth() + Plot.PLOT_PADDING + Chart.CHART_PADDING - 1);
            int yOffset = (int) (axisPair.getYAxis().getBounds().getY() + axisPair.getYAxis().getBounds().getHeight());
            int width = (int) (chart.getWidth() - axisPair.getYAxis().getBounds().getWidth() - axisPair.getChartLegendBounds().getWidth() - 3 * Chart.CHART_PADDING);
            int height = this.getSizeHint();
            Rectangle xAxisRectangle = new Rectangle(xOffset, yOffset, width, height);
            this.paintZone = xAxisRectangle;
            // g.setColor(Color.green);
            // g.draw(xAxisRectangle);

            axisTitle.paint(g);
            axisTick.paint(g);
            axisLine.paint(g);

            xOffset = (int) paintZone.getX();
            yOffset = (int) paintZone.getY();
            width = ((int) paintZone.getWidth());
            height = (int) (axisTitle.isVisible ? axisTitle.getBounds().getHeight() : 0 + axisTick.getBounds().getHeight() + axisLine.getBounds().getHeight());
            bounds = new Rectangle(xOffset, yOffset, width, height);
            bounds = new Rectangle(xOffset, yOffset, width, height);
            // g.setColor(Color.yellow);
            // g.draw(bounds);
        }

    }
}