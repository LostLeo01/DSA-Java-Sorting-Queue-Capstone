import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class Main {
    // Each position across these lists represents one help desk ticket.
    private static final ArrayList<Integer> ticketIds = new ArrayList<Integer>();
    private static final ArrayList<String> requesterNames = new ArrayList<String>();
    private static final ArrayList<Integer> priorities = new ArrayList<Integer>();
    private static final ArrayList<String> arrivalTimes = new ArrayList<String>();
    private static final ArrayList<String> statuses = new ArrayList<String>();

    // Ticket IDs stay the same when records move during sorting.
    private static final Queue<Integer> ticketQueue = new LinkedList<Integer>();
    private static final Scanner scanner = new Scanner(System.in);
    private static int nextTicketId = 1001;

    public static void main(String[] args) {
        boolean running = true;

        System.out.println("IT Help Desk Ticket Manager");
        while (running) {
            showMainMenu();
            Integer choice = readInteger("Choose an option: ", 1, 9);
            if (choice == null) {
                break;
            }

            switch (choice) {
                case 1:
                    addTicket();
                    break;
                case 2:
                    displayRecords();
                    break;
                case 3:
                    bubbleSortByPriority();
                    break;
                case 4:
                    insertionSortByName();
                    break;
                case 5:
                    queueMenu();
                    break;
                case 6:
                    searchTicket();
                    break;
                case 7:
                    updateTicket();
                    break;
                case 8:
                    removeTicket();
                    break;
                case 9:
                    running = false;
                    System.out.println("Goodbye.");
                    break;
                default:
                    // readInteger already checks this range; this is a safe fallback.
                    System.out.println("That menu choice is not available.");
            }
        }
        scanner.close();
    }

    private static void showMainMenu() {
        System.out.println("\n--- Main Menu ---");
        System.out.println("1. Add ticket (automatically joins the queue)");
        System.out.println("2. Display all tickets");
        System.out.println("3. Bubble Sort by priority, then name");
        System.out.println("4. Insertion Sort by requester name");
        System.out.println("5. Queue operations");
        System.out.println("6. Search tickets by ID or requester name");
        System.out.println("7. Update a ticket");
        System.out.println("8. Remove a ticket");
        System.out.println("9. Exit");
    }

    private static void addTicket() {
        System.out.println("\n--- Add Help Desk Ticket ---");
        String name = readRequiredText("Requester name: ");
        if (name == null) {
            return;
        }
        Integer priority = readInteger("Priority (1 = highest, 5 = lowest): ", 1, 5);
        if (priority == null) {
            return;
        }

        int id = nextTicketId;
        nextTicketId++;
        ticketIds.add(id);
        requesterNames.add(name);
        priorities.add(priority);
        arrivalTimes.add(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        statuses.add("Waiting");

        // New tickets join the queue immediately so it drives the service workflow.
        ticketQueue.offer(id);
        System.out.println("Ticket " + id + " was added and placed at the end of the queue.");
    }

    private static void displayRecords() {
        System.out.println("\n--- Stored Tickets ---");
        if (ticketIds.isEmpty()) {
            System.out.println("There are no tickets to display.");
            return;
        }
        printTableHeader();
        for (int i = 0; i < ticketIds.size(); i++) {
            printTicket(i);
        }
    }

    private static void printTableHeader() {
        System.out.printf("%-8s %-22s %-10s %-20s %-14s%n", "ID", "Requester", "Priority", "Arrival", "Status");
        System.out.println("--------------------------------------------------------------------------");
    }

    private static void printTicket(int index) {
        System.out.printf("%-8d %-22s %-10d %-20s %-14s%n",
                ticketIds.get(index), shorten(requesterNames.get(index), 21), priorities.get(index),
                arrivalTimes.get(index), statuses.get(index));
    }

    private static String shorten(String value, int maxLength) {
        if (value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength - 3) + "...";
    }

    private static void bubbleSortByPriority() {
        System.out.println("\n--- Bubble Sort: priority (1 highest) then requester name ---");
        if (ticketIds.isEmpty()) {
            System.out.println("There are no tickets to sort.");
            return;
        }
        System.out.println("Tickets before sorting:");
        displayRecords();
        System.out.println("Queue before sorting (FIFO order):");
        displayQueue();

        // Bubble Sort compares neighboring tickets and moves the later one right.
        // A lower priority number comes first; names break ties for a predictable display.
        for (int end = ticketIds.size() - 1; end > 0; end--) {
            boolean changed = false;
            for (int i = 0; i < end; i++) {
                boolean priorityOutOfOrder = priorities.get(i) > priorities.get(i + 1);
                boolean samePriorityWrongNameOrder = priorities.get(i).equals(priorities.get(i + 1))
                        && requesterNames.get(i).compareToIgnoreCase(requesterNames.get(i + 1)) > 0;
                if (priorityOutOfOrder || samePriorityWrongNameOrder) {
                    swapRecords(i, i + 1);
                    changed = true;
                }
            }
            // A pass with no swaps means the records are already in order.
            if (!changed) {
                break;
            }
        }

        System.out.println("Tickets after sorting:");
        displayRecords();
        System.out.println("Queue after sorting (unchanged FIFO order):");
        displayQueue();
    }

    private static void insertionSortByName() {
        System.out.println("\n--- Insertion Sort: requester name A to Z ---");
        if (ticketIds.isEmpty()) {
            System.out.println("There are no tickets to sort.");
            return;
        }
        System.out.println("Tickets before sorting:");
        displayRecords();
        System.out.println("Queue before sorting (FIFO order):");
        displayQueue();

        // Insertion Sort grows a sorted section from left to right.
        // Adjacent swaps move the current name left while keeping each ticket's fields together.
        for (int i = 1; i < ticketIds.size(); i++) {
            int position = i;
            while (position > 0 && requesterNames.get(position - 1)
                    .compareToIgnoreCase(requesterNames.get(position)) > 0) {
                swapRecords(position - 1, position);
                position--;
            }
        }

        System.out.println("Tickets after sorting:");
        displayRecords();
        System.out.println("Queue after sorting (unchanged FIFO order):");
        displayQueue();
    }

    // Every field list must move together or values would belong to different tickets.
    private static void swapRecords(int first, int second) {
        Integer savedId = ticketIds.get(first);
        ticketIds.set(first, ticketIds.get(second));
        ticketIds.set(second, savedId);

        String savedName = requesterNames.get(first);
        requesterNames.set(first, requesterNames.get(second));
        requesterNames.set(second, savedName);

        Integer savedPriority = priorities.get(first);
        priorities.set(first, priorities.get(second));
        priorities.set(second, savedPriority);

        String savedArrival = arrivalTimes.get(first);
        arrivalTimes.set(first, arrivalTimes.get(second));
        arrivalTimes.set(second, savedArrival);

        String savedStatus = statuses.get(first);
        statuses.set(first, statuses.get(second));
        statuses.set(second, savedStatus);
    }

    private static void queueMenu() {
        boolean inQueueMenu = true;
        while (inQueueMenu) {
            System.out.println("\n--- Ticket Queue ---");
            System.out.println("1. Enqueue a ticket by ID");
            System.out.println("2. Dequeue next ticket (start service)");
            System.out.println("3. Peek at next ticket");
            System.out.println("4. Display queue");
            System.out.println("5. Check whether queue is empty");
            System.out.println("6. Return to main menu");
            Integer choice = readInteger("Choose an option: ", 1, 6);
            if (choice == null) {
                return;
            }
            switch (choice) {
                case 1:
                    enqueueTicket();
                    break;
                case 2:
                    dequeueTicket();
                    break;
                case 3:
                    peekTicket();
                    break;
                case 4:
                    displayQueue();
                    break;
                case 5:
                    checkQueueEmpty();
                    break;
                case 6:
                    inQueueMenu = false;
                    break;
                default:
                    System.out.println("That queue choice is not available.");
            }
        }
    }

    private static void enqueueTicket() {
        if (ticketIds.isEmpty()) {
            System.out.println("There are no tickets to enqueue.");
            return;
        }
        Integer id = readInteger("Ticket ID to enqueue: ", 1, Integer.MAX_VALUE);
        if (id == null) {
            return;
        }
        int index = findTicketIndex(id);
        if (index == -1) {
            System.out.println("Ticket " + id + " was not found.");
            return;
        }
        if ("Resolved".equals(statuses.get(index))) {
            System.out.println("A resolved ticket cannot be placed back in the queue.");
            return;
        }
        if (ticketQueue.contains(id)) {
            System.out.println("Ticket " + id + " is already in the queue.");
            return;
        }
        ticketQueue.offer(id);
        statuses.set(index, "Waiting");
        System.out.println("Ticket " + id + " was added to the end of the queue.");
    }

    private static void dequeueTicket() {
        Integer id = ticketQueue.poll();
        if (id == null) {
            System.out.println("The queue is empty; there is no ticket to dequeue.");
            return;
        }
        int index = findTicketIndex(id);
        if (index == -1) {
            // This should not occur because removing a record also removes its queued ID.
            System.out.println("Ticket " + id + " is missing from the records; queue data may be inconsistent.");
            return;
        }
        statuses.set(index, "In Progress");
        System.out.println("Next in FIFO order: ticket " + id + " for " + requesterNames.get(index) + ".");
        System.out.println("Ticket " + id + " has been dequeued and marked In Progress.");
    }

    private static void peekTicket() {
        Integer id = ticketQueue.peek();
        if (id == null) {
            System.out.println("The queue is empty; there is no next ticket.");
            return;
        }
        int index = findTicketIndex(id);
        if (index == -1) {
            System.out.println("Ticket " + id + " is missing from the records.");
            return;
        }
        System.out.println("Next ticket (not removed):");
        printTableHeader();
        printTicket(index);
    }

    private static void displayQueue() {
        if (ticketQueue.isEmpty()) {
            System.out.println("The queue is empty.");
            return;
        }
        System.out.println("Queue order, first to be served first:");
        printTableHeader();
        for (Integer id : ticketQueue) {
            int index = findTicketIndex(id);
            if (index >= 0) {
                printTicket(index);
            } else {
                System.out.println("Ticket " + id + " has no matching record.");
            }
        }
    }

    private static void checkQueueEmpty() {
        if (ticketQueue.isEmpty()) {
            System.out.println("The queue is empty.");
        } else {
            System.out.println("The queue has " + ticketQueue.size() + " ticket(s) waiting.");
        }
    }

    private static void searchTicket() {
        if (ticketIds.isEmpty()) {
            System.out.println("There are no tickets to search.");
            return;
        }
        System.out.println("\nSearch by: 1. Ticket ID  2. Requester name");
        Integer mode = readInteger("Choose a search type: ", 1, 2);
        if (mode == null) {
            return;
        }

        boolean found = false;
        if (mode == 1) {
            Integer id = readInteger("Ticket ID: ", 1, Integer.MAX_VALUE);
            if (id == null) {
                return;
            }
            int index = findTicketIndex(id);
            if (index >= 0) {
                printTableHeader();
                printTicket(index);
                found = true;
            }
        } else {
            String query = readRequiredText("Requester name (or part of a name): ");
            if (query == null) {
                return;
            }
            printTableHeader();
            for (int i = 0; i < requesterNames.size(); i++) {
                if (requesterNames.get(i).toLowerCase().contains(query.toLowerCase())) {
                    printTicket(i);
                    found = true;
                }
            }
        }
        if (!found) {
            System.out.println("No matching ticket was found.");
        }
    }

    private static void updateTicket() {
        if (ticketIds.isEmpty()) {
            System.out.println("There are no tickets to update.");
            return;
        }
        Integer id = readInteger("Ticket ID to update: ", 1, Integer.MAX_VALUE);
        if (id == null) {
            return;
        }
        int index = findTicketIndex(id);
        if (index == -1) {
            System.out.println("Ticket " + id + " was not found.");
            return;
        }

        String newName = readRequiredText("New requester name: ");
        if (newName == null) {
            return;
        }
        Integer newPriority = readInteger("New priority (1 = highest, 5 = lowest): ", 1, 5);
        if (newPriority == null) {
            return;
        }
        requesterNames.set(index, newName);
        priorities.set(index, newPriority);
        System.out.println("Ticket " + id + " was updated. Its queue position was kept unchanged.");
    }

    private static void removeTicket() {
        if (ticketIds.isEmpty()) {
            System.out.println("There are no tickets to remove.");
            return;
        }
        Integer id = readInteger("Ticket ID to remove: ", 1, Integer.MAX_VALUE);
        if (id == null) {
            return;
        }
        int index = findTicketIndex(id);
        if (index == -1) {
            System.out.println("Ticket " + id + " was not found.");
            return;
        }

        // Removing the stable ID prevents a deleted ticket from remaining in the FIFO queue.
        ticketQueue.remove(id);
        ticketIds.remove(index);
        requesterNames.remove(index);
        priorities.remove(index);
        arrivalTimes.remove(index);
        statuses.remove(index);
        System.out.println("Ticket " + id + " was removed from the records and, if waiting, from the queue.");
    }

    private static int findTicketIndex(int id) {
        for (int i = 0; i < ticketIds.size(); i++) {
            if (ticketIds.get(i) == id) {
                return i;
            }
        }
        return -1;
    }

    private static String readRequiredText(String prompt) {
        while (true) {
            String value = readLine(prompt);
            if (value == null) {
                return null;
            }
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println("Input cannot be empty. Please try again.");
        }
    }

    private static Integer readInteger(String prompt, int minimum, int maximum) {
        while (true) {
            String value = readLine(prompt);
            if (value == null) {
                return null;
            }
            if (value.isEmpty()) {
                System.out.println("Input cannot be empty. Enter a whole number.");
                continue;
            }
            try {
                int number = Integer.parseInt(value);
                if (number < minimum || number > maximum) {
                    System.out.println("Enter a number from " + minimum + " to " + maximum + ".");
                    continue;
                }
                return number;
            } catch (NumberFormatException exception) {
                System.out.println("Invalid number. Please enter a whole number.");
            }
        }
    }

    private static String readLine(String prompt) {
        System.out.print(prompt);
        if (!scanner.hasNextLine()) {
            return null;
        }
        return scanner.nextLine().trim();
    }
}
