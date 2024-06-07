import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Queue;

public class MainFrame extends JFrame {
    private DefaultTableModel tableModel;
    private JTextArea queuAreaTextArea;
    private JTextArea memoryAreaTextArea;

    public MainFrame() {
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        String[] columnNames = { "Type", "ID/Size", "Size/Start Address", "Time in memory", "Base register",
                "Limit register" };

        // table
        tableModel = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(table);
        getContentPane().add(tableScrollPane, BorderLayout.CENTER);

        // Text Area
        queuAreaTextArea = new JTextArea();
        queuAreaTextArea.setEditable(false);
        queuAreaTextArea.setRows(10);
        JScrollPane textAreaScrollPane = new JScrollPane(queuAreaTextArea);
      

        memoryAreaTextArea = new JTextArea();
        memoryAreaTextArea.setEditable(false);
        memoryAreaTextArea.setRows(10);
        JScrollPane memoryAreaScrollPane = new JScrollPane(memoryAreaTextArea);
       
        // Create a new JPanel with a BoxLayout
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        // Add the JScrollPane for the JTextArea and the JScrollPane for the JTable to
        // the JPanel
        centerPanel.add(textAreaScrollPane);
        centerPanel.add(tableScrollPane);
        centerPanel.add(memoryAreaScrollPane);


        // Add the JPanel to the CENTER of the BorderLayout
        getContentPane().add(centerPanel, BorderLayout.CENTER);


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

    public void updateQueueContents(Queue<ProcessControlBlock> readyQueue, Queue<ProcessControlBlock> jobQueue) {
        StringBuilder sb = new StringBuilder();

        sb.append("Ready Queue:\n");
        for (ProcessControlBlock pcb : readyQueue) {
            sb.append("Process ID: ").append(pcb.getProcessId()).append(", Size: ").append(pcb.getSize())
                    .append(", Time in Memory: ").append(pcb.getTimeInMemory()).append("\n");
        }

        sb.append("Job Queue:\n");
        for (ProcessControlBlock pcb : jobQueue) {
            sb.append("Process ID: ").append(pcb.getProcessId()).append(", Size: ").append(pcb.getSize())
                    .append(", Time in Memory: ").append(pcb.getTimeInMemory()).append("\n");
        }

        queuAreaTextArea.setText(sb.toString());
    }

    public void updateMemoryAllocationAndDefragment(String processId, String operation) {
        StringBuilder sb = new StringBuilder();
        
        if(operation.equals("allocate")) {
            sb.append("Job with Process ID: ").append(processId).append(" is brought into memory to replace the finished job. Allocated memory..\n");
        } else {
            sb.append("Job with Process ID: ").append(processId).append(" has finished execution. deallocated memory..\n");
        }

      
        memoryAreaTextArea.append(sb.toString());
    }
}