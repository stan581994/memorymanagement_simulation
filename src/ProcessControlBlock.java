public class ProcessControlBlock {
    private int processId;
    private int size;
    private int timeInMemory;
    private int baseRegister;
    private int limitRegister;

    public ProcessControlBlock(int processId, int size, int timeInMemory) {
        this.processId = processId;
        this.size = size;
        this.timeInMemory = timeInMemory;
        // Default values for baseRegister and limitRegister
        this.baseRegister = 0;
        this.limitRegister = 0;
    }

    // Getter methods
    public int getProcessId() {
        return processId;
    }

    public int getSize() {
        return size;
    }

    public int getTimeInMemory() {
        return timeInMemory;
    }

    public int getBaseRegister() {
        return baseRegister;
    }

    public int getLimitRegister() {
        return limitRegister;
    }

    // Setter methods
    public void setProcessId(int processId) {
        this.processId = processId;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setTimeInMemory(int timeInMemory) {
        this.timeInMemory = timeInMemory;
    }

    public void setBaseRegister(int baseRegister) {
        this.baseRegister = baseRegister;
    }

    public void setLimitRegister(int limitRegister) {
        this.limitRegister = limitRegister;
    }

    public void decrementTimeInMemory() {
        this.timeInMemory--;
    }
}
