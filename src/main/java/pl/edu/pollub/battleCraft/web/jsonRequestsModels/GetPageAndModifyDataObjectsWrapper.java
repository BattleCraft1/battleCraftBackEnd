package pl.edu.pollub.battleCraft.web.jsonRequestsModels;

public class GetPageAndModifyDataObjectsWrapper {
    private String[] namesOfObjectsToModify;

    private GetPageObjectsWrapper getPageObjectsWrapper;

    public void setNamesOfObjectsToModify(String... namesOfObjectsToModify){
        this.namesOfObjectsToModify=namesOfObjectsToModify;
    }

    public String[] getNamesOfObjectsToModify(){
        return namesOfObjectsToModify;
    }

    public GetPageObjectsWrapper getGetPageObjectsWrapper() {
        return getPageObjectsWrapper;
    }

    public void setGetPageObjectsWrapper(GetPageObjectsWrapper getPageObjectsWrapper) {
        this.getPageObjectsWrapper = getPageObjectsWrapper;
    }
}
