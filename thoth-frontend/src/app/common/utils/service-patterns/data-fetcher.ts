import {Page} from "../fetchUtils";

export interface DataFetcher<T>{
  findAll(page: number): Promise<Page<T>>
}
