package dev.thorinwasher.blockutil.database;

public enum SQLQuery {
    CREATE_BLOCK_TABLE("createBlockTable"),
    FREE_BLOCK("freeBlock"),
    TRACK_BLOCK("trackBlock"),
    SELECT_ALL_BLOCKS("selectAllBlocks");

    private final String fileName;

    SQLQuery(String fileName){
        this.fileName = fileName;
    }

    public String getFileName(){
        return this.fileName;
    }
}
