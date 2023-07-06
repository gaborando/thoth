import {DataFetcher} from "../service-patterns/data-fetcher";
import {SearchComponent} from "./search-component";

export abstract class ListPage<E> extends SearchComponent {

  public elements: E[] | undefined
  protected page = 0;

  protected constructor(protected fetcher: DataFetcher<E>) {
    super()
  }

  async ionViewWillEnter() {
    return this.loadPageData();
  }


  async loadPageData() {
    this.page = 0;
    this.elements = undefined;
    this.elements = (await this.findAll()).content;
  }

  async loadMore() {
    this.page++;
    this.elements = this.elements?.concat((await this.findAll()).content);
  }

  findAll() {
    return this.fetcher.findAll(this.page, this.composeSearchFilter())
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
