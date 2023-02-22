import {DataFetcher} from "../service-patterns/data-fetcher";

export abstract class ListPage<E>{

  public elements: E[] | undefined
  protected page = 0;

  protected constructor(protected fetcher: DataFetcher<E>) {
  }

  async ionViewWillEnter() {
    return this.loadPageData();
  }


  async loadPageData(){
    this.page = 0;
    this.elements = undefined;
    this.elements = (await this.fetcher.findAll(this.page)).content;
  }

  async loadMore(){
    this.page++;
    this.elements = this.elements?.concat((await this.fetcher.findAll(this.page)).content);
  }

  public async handleRefresh(event: any) {
    try {
      await this.loadPageData();
    } finally {
      event.target.complete();
    }
  }

  public async handleLoadMore(event: any) {
    try {
      await this.loadMore();
    } finally {
      event.target.complete();
    }
  }

}
