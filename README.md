# VirtualMemoryManager
This Java code implements a Virtual Memory Interface using the Swing library for GUI. The application simulates virtual memory management, enabling users to create and manage virtual memories, add pages, perform page replacement, and calculate effective access time (EAT).

Components:
Page Class:

Represents a memory page with attributes like ID, loaded status, assigned memory size, and address.
Methods: load/unload the page, set/get address, and retrieve ID and assigned memory size.
MemoryManager Class:

Manages memory pages and virtual memories.
Attributes: page table (mapping page IDs to Page objects), memory capacity, and parameters for EAT calculation (alpha, m, E).
Methods: request/load/replace/remove pages, display page table/memory info, assign addresses, search in TLB, calculate EAT, manage virtual memories.
VirtualMemory Class:

Represents a virtual memory instance with an index and a reference to a MemoryManager.
VirtualMemoryInterface Class:

Extends JFrame to create the main GUI.
Allows users to enter physical memory size and create virtual memories.
Associates virtual memory instances with a shared MemoryManager.
GUI components: text fields, labels, and buttons for memory page operations (add pages, display page table, search TLB, calculate EAT, perform optimal page replacement).
Key Actions:
Create Virtual Memory: Prompts for virtual memory size, creates an instance if size is within the physical memory limit.
Add Page: Adds a page by entering page ID and memory size.
Display Page Table: Shows the current state of the page table.
Search Page in TLB: Checks for a page in the TLB, indicating a hit or miss.
Calculate EAT: Computes EAT based on user-provided values for alpha, m, and E.
Optimal Page Replacement: Opens a window for optimal page replacement, simulating the process and displaying results in a table.
Main Method:
Launches the application by creating an instance of VirtualMemoryInterface.
Optimal Page Replacement Logic:
Simulates optimal page replacement based on a reference string and frame count, displaying the memory frames' state after each access.
The code offers a comprehensive simulation of virtual memory management, demonstrating page loading, replacement policies, TLB search, and EAT calculation.
(NOTE:THIS CODE RUNS VERY EFFICIENTLY EXCEPT THE OPTIMAL PAGE REPLACEMENT SO THERE MAY BE ERRORS IN SORTING THE PAGES IN OPTIMAL REPLACEMENT)
