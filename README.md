# IT Help Desk Ticket Manager

## 1. Project Title

**IT Help Desk Ticket Manager: Sorting and FIFO Queue Capstone**

## 2. Project Description

A help desk receives support requests that need to be tracked and handled in a fair order. This console program stores tickets, adds new tickets to a first-in, first-out (FIFO) service queue, and lets staff inspect, search, update, or remove tickets. Manual sorting makes it easier to review urgent tickets or find a requester without changing who is next in the queue.

Priority is numbered from **1 (highest)** to **5 (lowest)**. A new ticket starts with status `Waiting` and is immediately placed at the end of the queue. Dequeuing begins service and changes its status to `In Progress`.

## 3. Objectives

- Apply parallel `ArrayList`s to store related fields for each ticket.
- Implement Bubble Sort and Insertion Sort by hand.
- Use stable ticket IDs in a Java FIFO queue.
- Keep queue processing order independent from display sorting.
- Validate console input and explain errors clearly.
- Organize the program with static methods and straightforward procedural logic.

## 4. Use Cases and Features

- Add a help desk ticket; it is automatically queued.
- Display all tickets and their ID, requester, priority, arrival time, and status.
- Sort tickets by priority (then name) or by requester name.
- Enqueue, dequeue, peek, display the queue, and check whether it is empty.
- Search by ticket ID or partial requester name (case-insensitive).
- Update a requester's name and priority while preserving a queued ticket's position.
- Remove a ticket from the records and from the queue if it is waiting there.
- Handle invalid numbers, blank input, invalid menu choices, empty lists/queues, and missing IDs.

## 5. Data Structure Explanation

Five parallel `ArrayList`s store ticket IDs, requester names, priorities, arrival times, and statuses. An index identifies the same ticket in each list. The `swapRecords(i, j)` method swaps all five fields together, and add, update, and remove operations also keep the lists synchronized.

A `Queue<Integer>` backed by `LinkedList` contains stable ticket IDs, rather than list positions. New tickets join the tail; `poll()` serves the head and marks that ticket `In Progress`. Because sorts only reorder the parallel record lists, the queue keeps the same FIFO order. Queue display finds each ticket by ID, so it still shows the correct record after a sort.

## 6. Sorting Algorithm Explanation

- **Bubble Sort by priority, then name:** Repeatedly compares neighboring records and swaps them when the left record belongs later. Priority 1 comes before priority 2, and so on; requester name breaks priority ties. This helps staff review urgent tickets first. A pass with no swaps ends the work early.
- **Insertion Sort by requester name:** Builds a sorted section from left to right, moving each new name left across adjacent records until it fits alphabetically. This makes requester names easier to scan. Adjacent swaps call `swapRecords`, keeping every field attached to its ticket.

Both sorts show the records and queue before and after. They sort the records for display; they do not modify queue order.

## 7. How to Run the Program

From the project folder, compile and run with a Java Development Kit:

```text
javac src/Main.java
java -cp src Main
```

Use the numbered main menu. New tickets are automatically added to the FIFO queue. In the queue menu, choose **Dequeue next ticket** to start service for the ticket that has waited longest. Choose **Exit** from the main menu to close the program.

## 8. Screenshots of the System

The following are genuine screenshots from the running program, saved in `screenshots/`.

| Filename | Caption and what to show |
|---|---|
| `main-menu.png` | Main menu with all options and the exit option visible. |
| `add-and-display-records-1.png`, `add-and-display-records-2.png`, `add-and-display-records.png` | First ticket added, second ticket added, then both tickets displayed. |
| `queue-operations.png`, `queue-operations-2.png`, `queue-operations-3.png`, `queue-operations-4.png` | Queue display, peek/dequeue, enqueue and display, then queue check. |
| `fifo-behavior.png` | Add two tickets in a known order, display the queue, then dequeue twice so the first added ticket is served first. |
| `sorting-algorithm-1.png` | Bubble Sort output showing records before and after sorting by priority. |
| `sorting-algorithm-2.png` | Insertion Sort output showing records before and after sorting by requester name. |
| `input-validation.png` | Show a rejected letter, blank entry, out-of-range menu choice, and a clear empty-queue or missing-ticket message. |

## Verification Checklist

- [x] Code compiles and runs.
- [x] No custom classes or OOP constructs are used.
- [x] No built-in sorting calls are used.
- [x] Parallel `ArrayList`s stay synchronized after add, update, remove, and sort.
- [x] Queue is FIFO, drives ticket service, and is unaffected by sorting.
- [x] Bad input, empty lists/queues, and missing IDs are handled.
- [x] All eight README sections and the screenshot filename list are present.

## Suggested Git Commits

- `Add procedural help desk ticket records and menu`
- `Implement FIFO ticket queue operations`
- `Add manual ticket sorting and search`
- `Document capstone and screenshot checklist`
