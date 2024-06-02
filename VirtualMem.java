import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

class Page {
    private int id;
    private boolean isLoaded;
    private int assignedMemory; 
    private int address; 

    public Page(int id, int assignedMemoryInMB) {
        this.id = id;
        this.isLoaded = false;
        this.assignedMemory = assignedMemoryInMB * 1024 * 1024; 
    }

    public int getId() {
        return id;
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public void load() {
        isLoaded = true;
    }

    public void unload() {
        isLoaded = false;
    }

    public int getAssignedMemory() {
        return assignedMemory;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public int getAddress() {
        return address;
    }
}

class MemoryManager {
    private Map<Integer, Page> pageTable;
    private int capacity;
    private double alpha;
    private double m;
    private double E;
    private List<VirtualMemory> virtualMemories;

    public MemoryManager(int capacity, double alpha, double m, double E) {
        this.capacity = capacity;
        this.pageTable = new HashMap<>();
        this.alpha = alpha;
        this.m = m;
        this.E = E;
        this.virtualMemories = new ArrayList<>();
    }

    public void requestPage(int pageId) {
        if (!pageTable.containsKey(pageId)) {
            System.out.println("Page " + pageId + " does not exist.");
            return;
        }

        Page page = pageTable.get(pageId);

        if (!page.isLoaded()) {
            if (pageTable.size() >= capacity) {
                replacePage(pageId);
            } else {
                loadPage(page);
            }
        }

        System.out.println("Page " + pageId + " accessed.");
    }

    private void loadPage(Page page) {
        page.load();
        System.out.println("Page " + page.getId() + " loaded into memory.");
    }

    private void replacePage(int newPageId) {
        Page victim = null;
        for (Page page : pageTable.values()) {
            if (!page.isLoaded()) {
                victim = page;
                break;
            }
        }
        if (victim != null) {
            victim.unload();
            loadPage(pageTable.get(newPageId));
            System.out.println("Page " + victim.getId() + " replaced by page " + newPageId);
        } else {
            System.out.println("Memory is full. Cannot replace any page.");
        }
    }

    public void addPage(Page page) {
        pageTable.put(page.getId(), page);
    }

    public void removePage(int pageId) {
        if (pageTable.containsKey(pageId)) {
            pageTable.remove(pageId);
            System.out.println("Page " + pageId + " removed from memory.");
        } else {
            System.out.println("Page " + pageId + " does not exist in memory.");
        }
    }

    public void displayPageTable() {
        System.out.println("Page Table:");
        for (Map.Entry<Integer, Page> entry : pageTable.entrySet()) {
            System.out.println("Page ID: " + entry.getKey() + ", Loaded: " + entry.getValue().isLoaded() + ", Assigned Memory: " + entry.getValue().getAssignedMemory() / (1024 * 1024) + " MB, Address: " + entry.getValue().getAddress() / (1024 * 1024) + " MB");
        }
    }

    public void displayMemoryInfo() {
        System.out.println("Total number of pages in memory: " + pageTable.size());
        System.out.println("Page IDs and their assigned memory:");
        for (Map.Entry<Integer, Page> entry : pageTable.entrySet()) {
            System.out.println("Page ID: " + entry.getKey() + ", Assigned Memory: " + entry.getValue().getAssignedMemory() / (1024 * 1024) + " MB");
        }
    }

public void assignAddresses() {
    Random rand = new Random();

    for (Page page : pageTable.values()) {
        int randomAddress = rand.nextInt(page.getAssignedMemory());
        page.setAddress(randomAddress);
    }
}


    public boolean searchPageInTLB(int pageId) {
        return pageTable.containsKey(pageId);
    }

    public long calculateEAT(double alpha, double m, double E) {
        long mNano = (long) (m * 1e6);

        long ENano = (long) (E * 1e6);

        alpha /= 100.0;

        double alphaM = alpha * mNano;
        int EAT = (int) (long) (2 * mNano +  ENano - alphaM);

        return EAT;
    }

    public List<VirtualMemory> getVirtualMemories() {
        return virtualMemories;
    }

    public void addVirtualMemory(VirtualMemory virtualMemory) {
        virtualMemories.add(virtualMemory);
    }
}

class VirtualMemory {
    private int index;
    private MemoryManager memoryManager;

    public VirtualMemory(int index, MemoryManager memoryManager) {
        this.index = index;
        this.memoryManager = memoryManager;
    }

    public int getIndex() {
        return index;
    }

    public MemoryManager getMemoryManager() {
        return memoryManager;
    }
}


public class VirtualMemoryInterface extends JFrame {
    private int numVirtualMemories = 0;
    private MemoryManager sharedMemoryManager; // Shared MemoryManager instance for all virtual memories

    public VirtualMemoryInterface() {
        setTitle("Virtual Memory Interface");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1));

        JLabel memorySizeLabel = new JLabel("Enter Total Size of Physical Memory (MB):");
        JTextField memorySizeField = new JTextField(10);
      panel.add(memorySizeLabel);
        panel.add(memorySizeField);

        JButton createVirtualMemoryButton = new JButton("Create Virtual Memory");
        createVirtualMemoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int physicalMemorySize = Integer.parseInt(memorySizeField.getText());
                int virtualMemorySize = Integer.parseInt(JOptionPane.showInputDialog("Enter Size of Virtual Memory (MB):"));

                if (virtualMemorySize <= physicalMemorySize) {
                    numVirtualMemories++;
                    if (numVirtualMemories == 1) {
                        // Create a shared MemoryManager instance for the first virtual memory
                        sharedMemoryManager = new MemoryManager(physicalMemorySize, 0, 0, 0);
                    }
                    VirtualMemory virtualMemory = new VirtualMemory(numVirtualMemories, sharedMemoryManager);
                    new VirtualMemoryInterface().openMemoryManager(virtualMemory);
                    dispose(); // Close the VirtualMemoryInterface window after creating virtual memory
                } else {
                    JOptionPane.showMessageDialog(null, "Virtual memory size exceeds physical memory size.");
                }
            }
        });
        panel.add(createVirtualMemoryButton);

        getContentPane().add(panel, BorderLayout.CENTER);

        setVisible(true);
    }

    private void openMemoryManager(VirtualMemory virtualMemory) {
        MemoryManager memoryManager = virtualMemory.getMemoryManager();
        memoryManager.addVirtualMemory(virtualMemory);

        JFrame frame = new JFrame("Memory Manager for Virtual Memory " + virtualMemory.getIndex());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setLocationRelativeTo(null);

        // Rest of the code remains the same...


        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(8, 1));

        JLabel pageIdLabel = new JLabel("Enter Page ID:");
        JTextField pageIdField = new JTextField(10);
        panel.add(pageIdLabel);
        panel.add(pageIdField);

        JLabel pageMemoryLabel = new JLabel("Enter Page Memory (MB):");
        JTextField pageMemoryField = new JTextField(10);
        panel.add(pageMemoryLabel);
        panel.add(pageMemoryField);

        JButton addPageButton = new JButton("Add Page");
        addPageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int pageId = Integer.parseInt(pageIdField.getText());
                int pageMemory = Integer.parseInt(pageMemoryField.getText());
                memoryManager.addPage(new Page(pageId, pageMemory));
                JOptionPane.showMessageDialog(null, "Page added successfully!");
            }
        });
        panel.add(addPageButton);

        JButton pageTableButton = new JButton("Display Page Table");
        pageTableButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                memoryManager.displayPageTable();
            }
        });
        panel.add(pageTableButton);

        JButton tlbButton = new JButton("Search Page in TLB");
        tlbButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int pageId = Integer.parseInt(pageIdField.getText());
                if (memoryManager.searchPageInTLB(pageId)) {
                    JOptionPane.showMessageDialog(null, "TLB Hit: Page " + pageId + " found in TLB.");
                } else {
                    JOptionPane.showMessageDialog(null, "TLB Miss: Page " + pageId + " not found in TLB.");
                }
            }
        });
        panel.add(tlbButton);

        JButton calculateEATButton = new JButton("Calculate EAT");
        calculateEATButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                double alpha = Double.parseDouble(JOptionPane.showInputDialog("Enter alpha (as percentage):"));
                double m = Double.parseDouble(JOptionPane.showInputDialog("Enter m:"));
                double E = Double.parseDouble(JOptionPane.showInputDialog("Enter E:"));
                long eat = memoryManager.calculateEAT(alpha, m, E);
                JOptionPane.showMessageDialog(null, "Effective Access Time (EAT): " + eat + " ns");
            }
        });
        panel.add(calculateEATButton);

        JButton optimalReplacementButton = new JButton("Optimal Page Replacement");
        optimalReplacementButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame optimalFrame = new JFrame("Optimal Page Replacement");
                optimalFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                optimalFrame.setSize(400, 400);
                optimalFrame.setLocationRelativeTo(frame);

                JPanel optimalPanel = new JPanel();
                optimalPanel.setLayout(new GridLayout(4, 1));

                JLabel referenceStringLabel = new JLabel("Enter Reference String:");
                JTextField referenceStringField = new JTextField(10);
                optimalPanel.add(referenceStringLabel);
                optimalPanel.add(referenceStringField);

                JLabel framesLabel = new JLabel("Enter Number of Frames:");
                JTextField framesField = new JTextField(10);
                optimalPanel.add(framesLabel);
                optimalPanel.add(framesField);

                JButton submitButton = new JButton("Submit");
                submitButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String referenceString = referenceStringField.getText();
                        int frames = Integer.parseInt(framesField.getText());
                        performOptimalPageReplacement(referenceString, frames, memoryManager);
                    }
                });
                optimalPanel.add(submitButton);

                optimalFrame.getContentPane().add(optimalPanel, BorderLayout.CENTER);

                optimalFrame.setVisible(true);
            }
        });
        panel.add(optimalReplacementButton);

        JButton addVirtualMemoryButton = new JButton("Add New Virtual Memory");
        addVirtualMemoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new VirtualMemoryInterface();
                frame.dispose(); // Close the current Memory Manager window
            }
        });
        panel.add(addVirtualMemoryButton);

        JLabel eatLabel = new JLabel(" ");
        panel.add(eatLabel);

        frame.getContentPane().add(panel, BorderLayout.CENTER);

        frame.setVisible(true);
    }

private void performOptimalPageReplacement(String referenceString, int frames, MemoryManager memoryManager) {
    // Remove all non-digit characters from the reference string
    referenceString = referenceString.replaceAll("[^\\d]", "");

    // Convert reference string to an array of integers
    int[] referenceArray = new int[referenceString.length()];
    for (int i = 0; i < referenceString.length(); i++) {
        referenceArray[i] = Character.getNumericValue(referenceString.charAt(i));
    }

    // Initialize an array to keep track of the index of the next occurrence of each page
    int[] nextOccurrence = new int[10]; // Assuming pages are numbered from 0 to 9
    Arrays.fill(nextOccurrence, -1);

    // Initialize a list to represent the frames
    List<Integer> framesList = new ArrayList<>();

    // Initialize the data array for the table
    Object[][] data = new Object[referenceArray.length][frames + 1];

    // Iterate through the reference string and update the index of the next occurrence of each page
    for (int i = 0; i < referenceArray.length; i++) {
        nextOccurrence[referenceArray[i]] = i;
    }

    // Iterate through the reference string again and perform page replacement logic
    for (int i = 0; i < referenceArray.length; i++) {
        int page = referenceArray[i];
        if (!framesList.contains(page)) {
            if (framesList.size() < frames) {
                framesList.add(page);
            } else {
                int farthestIndex = -1;
                int farthestPage = -1;
                for (int j = 0; j < framesList.size(); j++) {
                    int nextPageOccurrence = nextOccurrence[framesList.get(j)];
                    if (nextPageOccurrence == -1) {
                        farthestPage = framesList.get(j);
                        break;
                    }
                    if (nextPageOccurrence > farthestIndex) {
                        farthestIndex = nextPageOccurrence;
                        farthestPage = framesList.get(j);
                    }
                }
                framesList.remove((Integer) farthestPage);
                framesList.add(page);
            }
        }

        // Update the data array for the table
        data[i][0] = page;
        for (int j = 0; j < framesList.size(); j++) {
            data[i][j + 1] = framesList.get(j);
        }
    }

    // Create and display the table
    JFrame tableFrame = new JFrame("Optimal Page Replacement Table");
    tableFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    tableFrame.setSize(600, 400);
    tableFrame.setLocationRelativeTo(null);

    JPanel tablePanel = new JPanel(new BorderLayout());

    String[] columnNames = new String[frames + 1];
    columnNames[0] = "Reference String";
    for (int i = 1; i <= frames; i++) {
        columnNames[i] = "Frame " + i;
    }

    JTable table = new JTable(data, columnNames);
    table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    JScrollPane scrollPane = new JScrollPane(table);
    tablePanel.add(scrollPane, BorderLayout.CENTER);

    tableFrame.getContentPane().add(tablePanel);
    tableFrame.setVisible(true);
}



    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new VirtualMemoryInterface();
            }
        });
    }
}
