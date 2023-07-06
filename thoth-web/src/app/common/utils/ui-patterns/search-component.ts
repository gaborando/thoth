export abstract class SearchComponent {


  private searchTimeout: any | undefined;
  public search: string | undefined;
  protected debounce = 500;

  public composeSearchFilter() {
    return '';
  }

  public abstract loadPageData(): Promise<any>;

  public doSearch(e: any) {
    if(this.searchTimeout){
      clearTimeout(this.searchTimeout);
    }
    this.searchTimeout = setTimeout(() => {
      const src = e.target.value
      if(src && src.length > 0){
        this.search = src;
      }else{
        this.search = undefined;
      }
      return this.loadPageData();
    }, this.debounce);
  }


}
