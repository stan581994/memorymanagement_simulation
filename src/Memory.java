import java.io.*;
import java.util.*;

import java.util.ArrayList;
import java.util.List;

public class Memory {
    private int size;
    private List<ProcessControlBlock> allocatedRegions;
    private List<Hole> freeHoles;

    public Memory(int size) {
        this.size = size;
        this.allocatedRegions = new ArrayList<>();
        this.freeHoles = new ArrayList<>();
        this.freeHoles.add(new Hole(0, size)); // Initially, all memory is free
    }

    public void allocate(ProcessControlBlock pcb) {
        // Find the first hole that is big enough
        for (Hole hole : freeHoles) {
            if (hole.getSize() >= pcb.getSize()) {
                // Allocate the process to this hole
                pcb.setBaseRegister(hole.getStart());
                pcb.setLimitRegister(hole.getStart() + pcb.getSize());
                allocatedRegions.add(pcb);

                // Update the hole
                hole.setStart(hole.getStart() + pcb.getSize());
                hole.setSize(hole.getSize() - pcb.getSize());

                // If the hole is now empty, remove it
                if (hole.getSize() == 0) {
                    freeHoles.remove(hole);
                }
                checkAndCompact();

                return;
            }
        }

        // If no suitable hole was found, throw an exception
        throw new OutOfMemoryError("Not enough memory to allocate process");
    }

    public void deallocate(ProcessControlBlock pcb) {
        // Remove the process from the list of allocated regions
        allocatedRegions.remove(pcb);

        // Add a new hole where the process was
        freeHoles.add(new Hole(pcb.getBaseRegister(), pcb.getSize()));

        checkAndCompact();
    }

    public void compact() {
        int nextFreeAddress = 0;

        // Move all allocated regions to the start of the memory
        for (ProcessControlBlock pcb : allocatedRegions) {
            pcb.setBaseRegister(nextFreeAddress);
            pcb.setLimitRegister(nextFreeAddress + pcb.getSize());
            nextFreeAddress += pcb.getSize();
        }

        // Create a single free hole at the end
        freeHoles.clear();
        freeHoles.add(new Hole(nextFreeAddress, size - nextFreeAddress));
    }

    public List<ProcessControlBlock> getAllocatedRegions() {
        return allocatedRegions;
    }

    public List<Hole> getFreeHoles() {
        return freeHoles;
    }


    public void checkAndCompact() {
        if (getNumberOfHoles() > 3) {
            compact();
        }
    }

    public int getNumberOfHoles() {
        return freeHoles.size();
    }

    public int getLargestFreeHoleSize() {
        int largestSize = 0;
        for (Hole hole : freeHoles) {
            if (hole.getSize() > largestSize) {
                largestSize = hole.getSize();
            }
        }
        return largestSize;
    }

    public void printMemoryDiagram() {
        System.out.println("Memory contents:");
    
        for (ProcessControlBlock pcb : allocatedRegions) {
            System.out.println("Process ID: " + pcb.getProcessId());
            System.out.println("Size: " + pcb.getSize());
            System.out.println("Time in memory: " + pcb.getTimeInMemory());
            System.out.println("Base register: " + pcb.getBaseRegister());
            System.out.println("Limit register: " + pcb.getLimitRegister());
            System.out.println();
        }
    
        for (Hole hole : freeHoles) {
            System.out.println("Hole size: " + hole.getSize());
            System.out.println("Start address: " + hole.getStart());
            System.out.println("Limit address: " + (hole.getStart() + hole.getSize()));
            System.out.println();
        }
    }
}