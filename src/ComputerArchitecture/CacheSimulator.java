// Jisoo Kim 07/06/2022
package ComputerArchitecture;
import java.util.Scanner;

public class CacheSimulator {
    Cache ca = new Cache();
    private final int dirtyColumn = 3;
     public void initializeArray() {
         //Initializing Main memory (set all data in MM to 0)
         for (short i = 0; i <= 0x7FF; i++) {
             ca.getMainMem()[i] = i;
             if (i > 0xFF) {
                 ca.getMainMem()[i] = (i & 0x0FF);
             }
         }
         //Initializing 0 in Cache
         for (int i = 0; i < 16; i++) {
             for(int j = 0; j < 20; j++){
                 ca.getCache()[i][j] = 0;
             }
         }
         ca.updateSlotNum();
     }

     public void readInput() {
         //Reading inputs from the user (R/W/D, address, data)
         boolean exit = false;
         while (!exit){
             Scanner scan = new Scanner(System.in);
             //Getting input(Answer) from the user.
             System.out.println("\n1) (R)ead, (W)rite, or (D)isplay Cache? (press (E) to exit program)");
             ca.setAnswer1(scan.next());

             //Reading Cache
             if (ca.getAnswer1().equalsIgnoreCase("R")) {
                 System.out.println("2) What address would you like to read?");
                 //Get address input (integer)
                 ca.setInput(scan.next());
                 ca.setAddress(Integer.parseInt(ca.getInput(), 16));
                 //set Slot, Tag, Block offset value from the address input.
                 ca.updateAddress();

                 //Check if it is a Hit. (valid# == 1 && Tag match)
                 if (ca.cacheHit()) {
                     System.out.println("At "+ Integer.toHexString(ca.getAddress()) + ", there is the value " + Integer.toHexString(ca.getCache()[ca.getSlot()][ca.getBlockOffset() + 4]) + " (Cache Hit)");
                     ca.updateTag();
                 }
                 //Check if it is a Miss.
                 else {
                     //Check if it is a Miss. (valid# == 1 && Tag mismatch)
                     if (ca.cacheMiss()) {
                         System.out.println("At that byte there is the value  " + Integer.toHexString(ca.getCache()[ca.getSlot()][ca.getBlockOffset() + 4]) + " (Cache Miss)");

                         //Dirty bit == 0 --> get entire block from MM
                         if (ca.getCache()[ca.getSlot()][dirtyColumn] == 0) {
                             System.out.println("Copying entire block from main memory...");
                             ca.copyMM(); //Copy current address block from MM
                             ca.updateTag(); //Current Tag #
                             System.out.println("Cache update in address "+ Integer.toHexString(ca.getAddress())+ ":");
                             ca.displayCache();
                         }
                         //Dirty bit == 1 --> update MM from cache, and then get entire block from MM
                         else {
                             System.out.println("Update main memory..");
                             ca.copyCacheToPrevAdd(); //Copy cache to previous address MM
                             System.out.println("Copy entire block from main memory...");
                             ca.copyMM(); //Copy current address block from MM
                             ca.updateTag(); //Current Tag #
                             ca.updateDirtyBit(); //set dirty bit to 0
                             System.out.println("Cache update in address "+ Integer.toHexString(ca.getAddress())+ ":");
                             ca.displayCache();
                         }

                     } else {
                         //Miss (compulsory miss)
                         System.out.println("At that byte there is the value "+ Integer.toHexString(ca.getCache()[ca.getSlot()][ca.getBlockOffset() + 4]) + " (Cache Miss)");
                         ca.updateValid(); //valid number to 1
                         System.out.println("Copy entire block from main memory...");
                         ca.copyMM(); //Copy current address block from MM
                         ca.updateTag(); //Current Tag #
                         System.out.println("Cache update in address "+ Integer.toHexString(ca.getAddress())+ ":");
                         ca.displayCache();
                     }
                 }
             }
             //Writing Cache
             if (ca.getAnswer1().equalsIgnoreCase("W")) {
                 System.out.println("2) What address would you like to write to?");
                 ca.setInput(scan.next());
                 ca.setAddress(Integer.parseInt(ca.getInput(), 16));
                 //set Slot, Tag, Block offset value from the address input.
                 ca.updateAddress();

                 System.out.println("What data would you like to write at that address?");
                 ca.setData(scan.next());

                 //Check if it is a Hit. (valid# == 1 && Tag match) write data in cache
                 if (ca.cacheHit()) {
                     System.out.println("Value " + ca.getData() + " has been written to address " + Integer.toHexString(ca.getAddress()) + " (Cache Hit)");
                     ca.writeData(); //Write the user's data input at the address
                     ca.updateDirtyBit(); //Set dirty bit to 1
                     ca.updateTag(); //Current Tag #
                 }
                 //Check if it is a Miss.
                 else {
                     //Check if it is a Miss. (valid# == 1 && Tag mismatch)
                     if (ca.cacheMiss()) {
                         System.out.println("Cache Miss (Tag mismatch)");

                         //Dirty bit == 0 --> get entire block from MM
                         if (ca.getCache()[ca.getSlot()][dirtyColumn] == 0) {
                             System.out.println("Copying entire block from main memory...");
                             ca.copyMM(); //Copy current address block from MM
                             System.out.println("Writing data in cache...");
                             ca.writeData(); //Write the user's data input at the address
                             ca.updateDirtyBit(); //dirty bit to 1
                             ca.updateTag(); //Current Tag #
                             System.out.println("Value " + ca.getData() + " has been written to address " + Integer.toHexString(ca.getAddress()));
                             System.out.println("Cache update in address "+ Integer.toHexString(ca.getAddress())+ ":");
                             ca.displayCache();
                         }
                         //Dirty bit == 1 --> update MM from cache & get entire block from MM
                         else {
                             System.out.println("Update main memory...");
                             ca.copyCacheToPrevAdd(); //Copy cache to previous address MM
                             System.out.println("Copy entire block from main memory...");
                             ca.copyMM(); //Copy current address block from MM
                             ca.updateDirtyBit(); //valid bit to 0
                             System.out.println("Writing data in cache...");
                             ca.writeData(); //Write the user's data input at the address
                             ca.updateDirtyBit(); //valid bit to 1
                             ca.updateTag(); //Current Tag #
                             System.out.println("Value " + ca.getData() + " has been written to address " + Integer.toHexString(ca.getAddress()));
                             System.out.println("Cache update in address "+ Integer.toHexString(ca.getAddress())+ ":");
                             ca.displayCache();
                         }
                     } else { //Compulsory miss
                         System.out.println("Cache Miss (Compulsory)");
                         ca.updateValid(); //valid # to 1
                         System.out.println("Copy entire block from main memory...");
                         ca.copyMM(); //Copy current address block from MM
                         System.out.println("Writing data in cache...");
                         ca.writeData(); //Write the user's data input at the address
                         ca.updateDirtyBit(); //Set dirty bit to 1
                         ca.updateTag(); //Current Tag #
                         System.out.println("Value " + ca.getData() + " has been written to address " + Integer.toHexString(ca.getAddress()));
                         System.out.println("Cache update in address "+ Integer.toHexString(ca.getAddress())+ ":");
                         ca.displayCache();
                     }
                 }
             }
             //If the user's input is D (display)
             if(ca.getAnswer1().equalsIgnoreCase("D")) {
                 //Show 2D cache array (in hexadecimal)
                 System.out.println("   S: Slot number, V: Valid number, T: Tag number, D: Dirty bit\n");
                 System.out.print("   S   V   T   D   -----------------------------Data-----------------------------\n");
                 for (int row = 0; row < 16; row++) {
                     for (int col = 0; col < 20; col++) {
                         System.out.printf("%4X", ca.getCache()[row][col]);
                     }
                 System.out.println();
                 }
             }
             //If the user's input is E (exit)
             if(ca.getAnswer1().equalsIgnoreCase("E")) {
                 exit = true;
             }
         }
     }
}
