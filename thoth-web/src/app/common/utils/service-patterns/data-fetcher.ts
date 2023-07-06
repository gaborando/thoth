import {Page} from "../fetchUtils";

export interface DataFetcher<T>{
  findAll(page: number, filter: string): Promise<Page<T>>
}
