package net.swofty.experimental.ui;

import java.awt.*;
import javax.swing.*;

import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.data.xy.*;
import com.sun.management.OperatingSystemMXBean;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.util.Timer;
import java.util.TimerTask;

public class ConsoleInterface extends JFrame {
    public JTextArea outputArea;
    public static SwoftyDBOutputHandler out;
    private XYSeries cpuSeries;
    private XYSeries ramSeries;

    private JButton stop;
    private JButton start;
    private JButton fs;
    private JButton stopAndExit;

    public ConsoleInterface() {
        setTitle("Console Interface");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 650);
        setLayout(new BorderLayout());

        // Left panel for system info
        JPanel systemPanel = new JPanel();
        systemPanel.setLayout(new BorderLayout());
        systemPanel.setPreferredSize(new Dimension(400, getHeight()));
        systemPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // CPU usage chart
        XYSeriesCollection cpuDataset = new XYSeriesCollection();
        cpuSeries = new XYSeries("CPU Usage");
        cpuDataset.addSeries(cpuSeries);
        JFreeChart cpuChart = ChartFactory.createXYLineChart(
                "CPU Usage", "Time (s)", "Usage (%)", cpuDataset, PlotOrientation.VERTICAL, false, true, false);
        ChartPanel cpuChartPanel = new ChartPanel(cpuChart);
        cpuChartPanel.setPreferredSize(new Dimension(200, 300));
        systemPanel.add(cpuChartPanel, BorderLayout.NORTH);

        // RAM usage chart
        XYSeriesCollection ramDataset = new XYSeriesCollection();
        ramSeries = new XYSeries("RAM Usage");
        ramDataset.addSeries(ramSeries);
        JFreeChart ramChart = ChartFactory.createXYLineChart(
                "RAM Usage", "Time (s)", "Usage (%)", ramDataset, PlotOrientation.VERTICAL, false, true, false);
        ChartPanel ramChartPanel = new ChartPanel(ramChart);
        ramChartPanel.setPreferredSize(new Dimension(200, 300));
        systemPanel.add(ramChartPanel, BorderLayout.SOUTH);

        add(systemPanel, BorderLayout.WEST);

        // Right panel for output
        JPanel outputPanel = new JPanel();
        outputPanel.setLayout(new BorderLayout());
        outputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Output label
        JLabel outputLabel = new JLabel("Console Output:");
        outputPanel.add(outputLabel, BorderLayout.NORTH);

        JPanel controlsPanel = new JPanel();
        controlsPanel.setLayout(new FlowLayout());
        outputLabel.add(controlsPanel, BorderLayout.SOUTH);

        stop = new JButton("Stop");
        controlsPanel.add(stop, BorderLayout.WEST);
        fs = new JButton("Force Stop");
        controlsPanel.add(fs, BorderLayout.CENTER);
        start = new JButton("Start");
        controlsPanel.add(start, BorderLayout.EAST);
        stopAndExit = new JButton("Stop & Exit");
        controlsPanel.add(stopAndExit);
        outputPanel.add(controlsPanel, BorderLayout.PAGE_END);

        // Output text area
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        outputPanel.add(scrollPane, BorderLayout.CENTER);

        add(outputPanel, BorderLayout.CENTER);

        buttonsLoad();
        out = new SwoftyDBOutputHandler(this);

        // Update CPU and RAM usage periodically
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                double cpuUsage = getCPUUsage();
                double ramUsage = getRAMUsage();

                SwingUtilities.invokeLater(() -> {
                    cpuSeries.addOrUpdate(System.currentTimeMillis() / 1000, cpuUsage);
                    ramSeries.addOrUpdate(System.currentTimeMillis() / 1000, ramUsage);
                });
            }
        }, 0, 1000);
    }

    private void buttonsLoad() {
        stopAndExit.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.exit(0);
            }
        });
    }

    public void hook() {
        System.setOut(new PrintStream(System.out) {
            @Override
            public void println(String x) {
                ConsoleInterface.out.println(x);
            }
        });
    }

    private double getCPUUsage() {
        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
        return osBean.getProcessCpuLoad() * 100;
    }

    private double getRAMUsage() {
        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
        long totalMemory = osBean.getTotalPhysicalMemorySize();
        long freeMemory = osBean.getFreePhysicalMemorySize();
        long usedMemory = totalMemory - freeMemory;
        return (double) usedMemory / totalMemory * 100;
    }
}
