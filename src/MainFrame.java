import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class MainFrame extends JFrame {
    private DefaultTableModel tableModel;

    public MainFrame() {
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        String[] columnNames = { "Type", "ID/Size", "Size/Start Address", "Time in memory", "Base register",
                "Limit register" };
        tableModel = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton simulateButton = new JButton("Simulate");
        simulateButton.setPreferredSize(new Dimension(150, 100));
        simulateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() {
                        Main.simulate(MainFrame.this);
                        return null;
                    }
                }.execute();
            }
        });

        topPanel.add(simulateButton);
        add(topPanel, BorderLayout.NORTH);
    }

    public void printMemoryDiagram(ProcessControlBlock[] allocatedRegions, Hole[] freeHoles) {
        tableModel.setRowCount(0);

        for (ProcessControlBlock pcb : allocatedRegions) {
            tableModel.addRow(new Object[] {
                    "Process",
                    pcb.getProcessId(),
                    pcb.getSize(),
                    pcb.getTimeInMemory(),
                    pcb.getBaseRegister(),
                    pcb.getLimitRegister()
            });
        }

        for (Hole hole : freeHoles) {
            tableModel.addRow(new Object[] {
                    "Hole",
                    hole.getSize(),
                    hole.getStart(),
                    "",
                    "",
                    hole.getStart() + hole.getSize()
            });
        }

        revalidate();
        repaint();
    }
}