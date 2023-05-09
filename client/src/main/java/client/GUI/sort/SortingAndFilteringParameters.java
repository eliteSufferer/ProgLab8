package client.GUI.sort;

public class SortingAndFilteringParameters {
    private String sortingColumn;
    private boolean ascending;
    private String filteringColumn;
    private String filteringOperation;
    private String filteringValue;
    public SortingAndFilteringParameters(){
        reset();
    }


    public String getSortingColumn(){
        return this.sortingColumn;
    }
    public boolean getAscending(){
        return this.ascending;
    }
    public String getFilteringColumn(){
        return this.filteringColumn;
    }
    public String getFilteringOperation(){
        return this.filteringOperation;
    }
    public String getFilteringValue(){
        return this.filteringValue;
    }


    public void setSortingColumn(String str){
        this.sortingColumn = str;
    }
    public void setAscending(boolean bool){
        this.ascending = bool;
    }
    public void setFilteringColumn(String str){
        this.filteringColumn = str;
    }
    public void setFilteringOperation(String str){
        this.filteringOperation = str;
    }
    public void setFilteringValue(String str){
        this.filteringValue = str;
    }
    public void reset(){
        this.sortingColumn = null;
        this.ascending = true;
        this.filteringColumn = null;
        this.filteringOperation = null;
        this.filteringValue = null;
    }
}
