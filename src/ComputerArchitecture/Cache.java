// Jisoo Kim 07/06/2022
package ComputerArchitecture;

public class Cache {
    private int[] mainMem  = new int[2048]; //2K memory
    private int[][] cache = new int[16][20]; //row(0-F), column(lotNum, validNum, tag, dirtyBit, data, slot number)
    private int blockOffset;
    private int slot;
    private int tag;
    private int validNum = 0;
    private String answer1;
    private int address;
    private String input;
    private String data;
    private int dirtyBit;
    private int blockStart;
    private final int validColumn = 1;
    private final int tagColumn = 2;

    public int getBlockStart() {
        return blockStart;
    }

    public void setBlockStart(int blockStart) {
        this.blockStart = blockStart;
    }

    public int[] getMainMem() {
        return mainMem;
    }

    public void setMainMem(int[] mainMem) {
        this.mainMem = mainMem;
    }

    public int[][] getCache() {
        return cache;
    }

    public void setCache(int[][] cache) {
        this.cache = cache;
    }

    public int getBlockOffset() {
        return this.blockOffset;
    }

    public void setBlockOffset(int blockOffset) {
        this.blockOffset = blockOffset;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public int getValidNum() {
        return validNum;
    }

    public void setValidNum(int validNum) {
        this.validNum = validNum;
    }

    public String getAnswer1() {
        return answer1;
    }

    public void setAnswer1(String answer1) {
        this.answer1 = answer1;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getDirtyBit() {
        dirtyBit = cache[slot][3];
        return dirtyBit;
    }

    public void setDirtyBit(int dirtyBit) {
        this.dirtyBit = dirtyBit;
    }

    public int getAddress() {
        if (address > 0x7FF) {
            System.err.println("Address is too big.");
        }
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public void copyMM() {
        //Copy the entire block from MM
        for (short i = 0; i <16; i++){
            cache[slot][i + 4] = mainMem[blockStart + i];
        }
    }
    public void copyCache() {
        //Copy cache data to MM
        for (int i=0; i < 16; i++) {
            mainMem[blockStart + i] = cache[slot][i + 4];
        }
    }
    public void copyCacheToPrevAdd(){
        int prevAddress = (cache[slot][tagColumn] << 8) + (slot << 4); //Get the previous address
        //Copy cache to previous address MM
        for (int i=0; i < 16; i++) {
            mainMem[prevAddress + i] = cache[slot][i + 4];
        }
    }

    public void writeData() {
        //write user's data input to cache
            cache[slot][4 + blockOffset] = Integer.parseInt(data, 16);
    }

    public void updateValid() { //Update valid # to 1
        cache[slot][1] =1;
    }

    public void updateDirtyBit() {
        if (cache[slot][3] == 0) {
            cache[slot][3] = 1;
        }
        else{
            cache[slot][3] = 0;
        }
    }
    public void updateAddress() {
        //Get each value from the input address using bitwise & and shift
        setSlot((address & 0x0F0) >>> 4);
        setTag((address & 0xF00) >>> 8);
        setBlockOffset(address & 0x00F);
        setBlockStart(address & 0xFF0);
    }

    public void updateSlotNum() {
        //Update slot number in cache
        for (int i = 0; i < 16; i++) {
            cache[i][0] = i;
        }
    }

    public void updateTag() {
        //Updating the new address tag number (tag mismatch miss, compulsory miss)
        cache[slot][tagColumn] = tag;
    }

    public void displayCache() {
        //Displaying cache at that slot address
        for (int i = 0; i < 16; i++) {
            System.out.print(Integer.toHexString(cache[slot][i + 4]).toUpperCase() +" ");
        }
        System.out.println("");
    }
    public boolean cacheHit() {
        //Hit: Tag match && valid # = 1
        return cache[slot][validColumn] == 1 && cache[slot][tagColumn] == tag;
    }

    public boolean cacheMiss() {
        //Miss: Tag mismatch && valid# != 1
        return cache[slot][validColumn] == 1 && cache[slot][tagColumn] != tag;
    }

}


