package org.cloudbus.cloudsim;
import java.awt.BasicStroke;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.Stroke;
import java.text.NumberFormat;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.labels.*;

/**
* A simple demonstration application showing how to create a line chart using data from a
* {@link CategoryDataset}.
*/
public class JFreeSeriesLineChart extends ApplicationFrame {

  /**
   * Creates a new demo.
   *
   * @param title  the frame title.
   */
  
  
  public JFreeSeriesLineChart(final String title) {
      super(title);
      
      final CategoryDataset dataset = createDataset();
      final JFreeChart chart = createChart(dataset);
      
      final ChartPanel chartPanel = new ChartPanel(chart);
      this.add(chartPanel, BorderLayout.CENTER);
      
      
      
      
  }
  /**
   * Returns a sample dataset.
   * 
   * @return The dataset.
   */
  private CategoryDataset createDataset() {
      
      // create the dataset...
      final DefaultCategoryDataset dataset = new DefaultCategoryDataset();

      dataset.addValue(0.04, "karan", "10");
      dataset.addValue(0.06, "karan", "15");
      dataset.addValue(0.07, "karan", "20");
      dataset.addValue(0.12, "karan", "25");
      dataset.addValue(0.19, "karan", "30");
      dataset.addValue(0.20, "karan", "35");
      dataset.addValue(0.19, "karan", "40");
      dataset.addValue(0.21, "karan", "45");
      dataset.addValue(0.25, "karan", "50");

      
      dataset.addValue(0.05, "anton", "10");
      dataset.addValue(0.07, "anton", "15");
      dataset.addValue(0.12, "anton", "20");
      dataset.addValue(0.15, "anton", "25");
      dataset.addValue(0.19, "anton", "30");
      dataset.addValue(0.22, "anton", "35");
      dataset.addValue(0.24, "anton", "40");
      dataset.addValue(0.28, "anton", "45");
      dataset.addValue(0.32, "anton", "50");
   
      
      return dataset;
      
  }
   /**
   * Creates a sample chart.
   * 
   * @param dataset  a dataset.
   * 
   * @return The chart.
   */
  private JFreeChart createChart(final CategoryDataset dataset) {
      
     final JFreeChart chart = ChartFactory.createLineChart(
          "",         // chart title
          "Number Of Cloudlets",                   // domain(x-axis) axis label
          "Energy Consumption (kWh)",                      // range(y-axis) axis label
          dataset,                      // data
          PlotOrientation.VERTICAL,     // orientation
          true,                         // include legend
          true,                         // tooltips
          false                         // urls
      );
      
      
      // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
      //final StandardLegend legend = (StandardLegend) chart.getLegend();
      //legend.setDisplaySeriesShapes(true);
      //legend.setShapeScaleX(1.5);
      //legend.setShapeScaleY(1.5);
      //legend.setDisplaySeriesLines(true);
      
      
      
      // set chart background
      chart.setBackgroundPaint(Color.white);
      

      //set plot specifications 
      final CategoryPlot plot = (CategoryPlot) chart.getPlot();
      plot.setBackgroundPaint(new Color(0xffffe0));
      plot.setDomainGridlinesVisible(true);
      plot.setDomainGridlinePaint(Color.lightGray);
      plot.setRangeGridlinePaint(Color.lightGray);
      
      //CUSTOMIZE DOMAIN AXIS
      final CategoryAxis domainAxis = (CategoryAxis) plot.getDomainAxis();
      
      //customize domain label position
      domainAxis.setCategoryLabelPositions(
          CategoryLabelPositions.createUpRotationLabelPositions(0)
      );

      
      //CUSTOMIZE RANGE AXIS
      final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
      rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
      rangeAxis.setAutoRangeIncludesZero(false);
      //rangeAxis.setVerticalTickLabels(true);
      //rangeAxis.setAutoTickUnitSelection(rootPaneCheckingEnabled);
      
      //CUSTOMIZE THE RENDERER
      final LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
      // renderer.setDrawOutlines(true);
      
      
      //You can also set series line colors for each line
      /*renderer.setSeriesPaint(0, Color.RED);
      renderer.setSeriesPaint(1, Color.BLUE);
      renderer.setSeriesPaint(2, Color.YELLOW);
      renderer.setSeriesPaint(3, Color.GREEN);
      renderer.setSeriesPaint(4, Color.ORANGE);
      renderer.setSeriesPaint(5, Color.white);*/
      
      
      
      //Working Code to show values along with dots
      NumberFormat format = NumberFormat.getNumberInstance();
      format.setMaximumFractionDigits(2);
      CategoryItemLabelGenerator generator =
          new StandardCategoryItemLabelGenerator(
              StandardCategoryItemLabelGenerator.DEFAULT_LABEL_FORMAT_STRING,format);
              
      renderer.setBaseItemLabelGenerator(generator);
      renderer.setBaseItemLabelsVisible(true);
      
      
      //set dots or you can say shapes at a point
      renderer.setBaseShapesFilled(true);
      renderer.setBaseShapesVisible(true);
      
      //DISPLAY LINES TYPE
      
      //It would show solid lines
      Stroke stroke = new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL);
      renderer.setBaseOutlineStroke(stroke);
      
      //You can also customize your lines like dotted one, dash-ed one etc.
      /* renderer.setSeriesStroke(
          0, new BasicStroke(
              2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
              1.0f, new float[] {10.0f, 6.0f}, 0.0f
          )
      );
      renderer.setSeriesStroke(
          1, new BasicStroke(
              2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
              1.0f, new float[] {6.0f, 6.0f}, 0.0f
          )
      );
      renderer.setSeriesStroke(
          2, new BasicStroke(
              2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
              1.0f, new float[] {2.0f, 6.0f}, 0.0f
          )
      );*/
      
      
      return chart;
  }
  
  /**
   * Starting point for the demonstration application.
   *
   * @param args  ignored.
   */
  public static void main(final String[] args) {

      final JFreeSeriesLineChart demo = new JFreeSeriesLineChart("Comparison Line Graph");
      demo.pack();
      RefineryUtilities.centerFrameOnScreen(demo);
      demo.setVisible(true);

  }

}
