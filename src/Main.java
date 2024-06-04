import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Main {
    public static void main(String[] args) {

        // Initialize memory and queues
        Memory memory = new Memory(2048 - 512); // 2GB - 512MB for OS
        Queue<ProcessControlBlock> readyQueue = readQueueFromFile("src/queues/ready.txt");
        Queue<ProcessControlBlock> jobQueue = readQueueFromFile("src/queues/job.txt");

        List<ProcessControlBlock> activeProcesses = new ArrayList<>();

        while (!readyQueue.isEmpty() || !activeProcesses.isEmpty()) {

            // If there is a process in the ready queue, try to allocate it to memory
            if (!readyQueue.isEmpty()) {
                ProcessControlBlock process = readyQueue.peek();
                try {
                    memory.allocate(process);
                    readyQueue.poll(); // Remove the process from the ready queue only if allocation is successful
                    activeProcesses.add(process); // Add the process to activeProcesses
                    printQueueContents(readyQueue, jobQueue);
                } catch (OutOfMemoryError e) {
                    // If memory allocation fails and there are more than 3 holes, compact the
                    // memory
                    if (memory.getNumberOfHoles() > 3) {
                        memory.compact();
                        // Try to allocate the process again
                        memory.allocate(process);
                        readyQueue.poll(); // Remove the process from the ready queue only if allocation is successful
                        activeProcesses.add(process); // Add the process to activeProcesses

                    } else {
                        // If the size of the process is larger than the size of the largest free hole,
                        // move the process back to the job queue
                        if (process.getSize() > memory.getLargestFreeHoleSize()) {
                            jobQueue.add(readyQueue.poll());
                        } else {
                            throw e; // If there are 3 or fewer holes, rethrow the exception
                        }
                    }
                }
            }
            memory.printMemoryDiagram();

            // If a process has finished execution, deallocate its memory and bring a new
            // process from the job queue into memory
            ProcessControlBlock finishedProcess = checkForFinishedProcesses(activeProcesses);
            if (finishedProcess != null) {
                memory.deallocate(finishedProcess);
                System.out
                        .println("Job with Process ID: " + finishedProcess.getProcessId() + " has finished execution.");
                if (!jobQueue.isEmpty()) {
                    ProcessControlBlock newProcess = jobQueue.poll();
                    memory.allocate(newProcess);
                    System.out.println("Job with Process ID: " + newProcess.getProcessId()
                            + " is brought into memory to replace the finished job.");
                }
                memory.printMemoryDiagram();
            }

            // Decrement the time in memory for each active process
            for (ProcessControlBlock process : activeProcesses) {
                process.decrementTimeInMemory();
            }

        }

    }

    static Queue<ProcessControlBlock> readQueueFromFile(String filename) {
        Queue<ProcessControlBlock> queue = new LinkedList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                int processId = Integer.parseInt(parts[0]);
                int size = Integer.parseInt(parts[1]);
                int timeInMemory = Integer.parseInt(parts[2]);
                ProcessControlBlock pcb = new ProcessControlBlock(processId, size, timeInMemory);
                queue.add(pcb);
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }

        return queue;
    }

    static ProcessControlBlock checkForFinishedProcesses(List<ProcessControlBlock> activeProcesses) {
        Iterator<ProcessControlBlock> iterator = activeProcesses.iterator();
        while (iterator.hasNext()) {
            ProcessControlBlock process = iterator.next();
            if (process.getTimeInMemory() <= 0) {
                iterator.remove();
                return process;
            }
        }
        return null;
    }

    
    static void printQueueContents(Queue<ProcessControlBlock> readyQueue, Queue<ProcessControlBlock> jobQueue) {
        System.out.println("Ready Queue:");
        for (ProcessControlBlock pcb : readyQueue) {
            System.out.println("Process ID: " + pcb.getProcessId() + ", Size: " + pcb.getSize() + ", Time in Memory: "
                    + pcb.getTimeInMemory());
        }

        System.out.println("Job Queue:");
        for (ProcessControlBlock pcb : jobQueue) {
            System.out.println("Process ID: " + pcb.getProcessId() + ", Size: " + pcb.getSize() + ", Time in Memory: "
                    + pcb.getTimeInMemory());
        }
    }



}
