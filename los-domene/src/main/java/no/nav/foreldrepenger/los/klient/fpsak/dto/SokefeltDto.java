package no.nav.foreldrepenger.los.klient.fpsak.dto;

public class SokefeltDto {

    private String searchString;

    public SokefeltDto(){

    }

    public SokefeltDto(String searchString){
        this.searchString = searchString;
    }

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }
}
