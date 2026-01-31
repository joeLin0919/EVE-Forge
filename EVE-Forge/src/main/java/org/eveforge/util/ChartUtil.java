package org.eveforge.util;

import org.eveforge.repository.po.HistoricalOrderObj;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;

import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.List;

public class ChartUtil {
    
    /**
     * 生成价格趋势折线图
     * @param data 图表数据，包含时间、最高价、最低价
     * @param itemName 物品名称
     * @return 图片字节数组
     */
    public static byte[] generatePriceTrendChart(List<HistoricalOrderObj> data, String itemName) {
        // 创建时间序列
        TimeSeries highestPriceSeries = new TimeSeries("最高价格");
        TimeSeries lowestPriceSeries = new TimeSeries("最低价格");
        TimeSeries volumeSeries = new TimeSeries("成交量");

        // 添加价格数据点（每天取数据）
        for (HistoricalOrderObj historicalOrder : data) {
            Timestamp timestamp = historicalOrder.getDate();
            if (timestamp != null) {
                Instant instant = timestamp.toInstant();
                java.util.Date date = java.util.Date.from(instant);
                Day day = new Day(date);
                
                if (historicalOrder.getHighest() != null) {
                    highestPriceSeries.add(day, historicalOrder.getHighest());
                }
                if (historicalOrder.getLowest() != null) {
                    lowestPriceSeries.add(day, historicalOrder.getLowest());
                }
            }
        }
        
        // 添加成交量数据点（每5天取一个数据点）
        java.util.Date lastDate = null;
        for (int i = 0; i < data.size(); i++) {
            HistoricalOrderObj historicalOrder = data.get(i);
            Timestamp timestamp = historicalOrder.getDate();
            if (timestamp != null && historicalOrder.getVolume() != null) {
                Instant instant = timestamp.toInstant();
                java.util.Date currentDate = java.util.Date.from(instant);
                Day day = new Day(currentDate);
                
                // 检查是否是第5天或以上
                if (lastDate == null) {
                    // 第一个数据点
                    lastDate = currentDate;
                    volumeSeries.add(day, historicalOrder.getVolume());
                } else {
                    // 计算两个日期之间的天数差
                    long diffInMillies = Math.abs(currentDate.getTime() - lastDate.getTime());
                    long diffInDays = diffInMillies / (24 * 60 * 60 * 1000);
                    
                    if (diffInDays >= 5) {
                        // 达到5天，添加数据点
                        lastDate = currentDate;
                        volumeSeries.add(day, historicalOrder.getVolume());
                    }
                }
            }
        }
        
        // 创建价格数据集
        TimeSeriesCollection priceDataset = new TimeSeriesCollection();
        priceDataset.addSeries(highestPriceSeries);
        priceDataset.addSeries(lowestPriceSeries);
        
        // 创建图表
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                itemName,  // 图表标题
                "时间",      // x轴标签
                "价格",   // y轴标签
                priceDataset,    // 价格数据集
                true,       // 显示图例
                true,       // 使用工具提示
                false       // 生成URL
        );
        
        // 获取xyPlot
        XYPlot plot = chart.getXYPlot();
        
        // 自定义日期轴格式，避免显示中文月份
        org.jfree.chart.axis.DateAxis dateAxis = (org.jfree.chart.axis.DateAxis) plot.getDomainAxis();
        
        // 设置日期格式为 MM-yyyy 格式，例如 "06-2025"
        java.text.SimpleDateFormat dateFormat = new SimpleDateFormat("MM-yyyy");
        dateAxis.setDateFormatOverride(dateFormat);
        
        // 设置价格线的渲染器
        plot.getRenderer().setSeriesPaint(0, Color.RED); // 最高价线颜色
        plot.getRenderer().setSeriesPaint(1, Color.BLUE); // 最低价线颜色
        
        // 添加成交量数据系列到图表
        if (!volumeSeries.isEmpty()) {
            TimeSeriesCollection volumeDataset = new TimeSeriesCollection();
            volumeDataset.addSeries(volumeSeries);
            
            // 创建第二个Y轴（成交量）
            NumberAxis volumeAxis = new NumberAxis("成交量");
            volumeAxis.setLabelFont(new Font("SimHei", Font.PLAIN, 12));
            volumeAxis.setAutoRange(true);
            volumeAxis.setAutoRangeIncludesZero(false);
            
            // 将第二个Y轴添加到plot中
            plot.setRangeAxis(1, volumeAxis);
            
            // 将成交量数据集分配给第二个Y轴
            plot.setDataset(1, volumeDataset);
            
            // 创建并设置成交量线的渲染器
            XYLineAndShapeRenderer volumeRenderer = new XYLineAndShapeRenderer();
            volumeRenderer.setSeriesPaint(0, Color.GREEN); // 成交量线颜色
            volumeRenderer.setSeriesShapesVisible(0, false); // 不显示数据点标记
            plot.setRenderer(1, volumeRenderer);
            
            // 将成交量轴设置在右侧
            plot.mapDatasetToRangeAxis(1, 1);
            
            // 调整成交量Y轴范围，扩大一倍以将其保持在图的下方
            // 首先启用自动范围以计算数据范围
            volumeAxis.setAutoRange(true);
            // 获取当前范围的最大值
            double upperBound = volumeAxis.getRange().getUpperBound();
            // 设置范围为0到最大值的两倍
            volumeAxis.setRange(0, upperBound * 2);
            // 禁用自动范围以固定范围
            volumeAxis.setAutoRange(false);
        }
        
        // 设置图表样式
        chart.setBackgroundPaint(Color.WHITE);
        
        // 设置图表标题字体
        chart.getTitle().setFont(getChineseFont(20, Font.BOLD));
        
        // 设置图例字体
        chart.getLegend().setItemFont(getChineseFont(12));
        
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
        
        // 设置轴标签字体
        plot.getDomainAxis().setLabelFont(getChineseFont(12));
        plot.getRangeAxis(0).setLabelFont(getChineseFont(12)); // 价格Y轴
        
        // 设置时间轴刻度标签字体，确保月份等中文字符正确显示
        plot.getDomainAxis().setTickLabelFont(getChineseFont(10));
        
        // 同时设置第二个Y轴的字体（如果存在）
        if (plot.getRangeAxis(1) != null) {
            plot.getRangeAxis(1).setLabelFont(getChineseFont(12));
        }
        
        // 调整价格纵坐标范围
        NumberAxis priceAxis = (NumberAxis) plot.getRangeAxis(0);
        // 启用自动范围以计算数据范围
        priceAxis.setAutoRange(true);
        // 获取当前范围
        double lowerBound = priceAxis.getRange().getLowerBound();
        double upperBound = priceAxis.getRange().getUpperBound();
        // 计算范围扩展，向下扩展一定比例以将数据保持在图的上方
        double range = upperBound - lowerBound;
        double newLowerBound = lowerBound - range; // 向下扩展相等的距离
        // 设置扩展后的范围
        priceAxis.setRange(newLowerBound, upperBound);
        // 禁用自动范围以固定范围
        priceAxis.setAutoRange(false);
        
        // 将图表转换为字节数组
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ChartUtils.writeChartAsPNG(outputStream, chart, 800, 600);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("生成图表失败", e);
        }
    }
    
    /**
     * 获取适合显示中文的字体
     * @param size 字体大小
     * @return 适合的字体对象
     */
    private static Font getChineseFont(int size) {
        return getChineseFont(size, Font.PLAIN);
    }
    
    /**
     * 获取适合显示中文的字体
     * @param size 字体大小
     * @param style 字体样式
     * @return 适合的字体对象
     */
    private static Font getChineseFont(int size, int style) {
        // 常见的中文字体列表，按优先级排列
        String[] fontNames = {"Microsoft YaHei", "SimHei", "STSong", "SimSun", "KaiTi", "Arial Unicode MS"};
        
        for (String fontName : fontNames) {
            Font font = new Font(fontName, style, size);
            // 检查字体是否可用
            if (!font.getFontName().toLowerCase().contains("missing")) {
                return font;
            }
        }
        
        // 如果指定字体都不可用，则返回默认字体
        return new Font(Font.DIALOG, style, size);
    }
}