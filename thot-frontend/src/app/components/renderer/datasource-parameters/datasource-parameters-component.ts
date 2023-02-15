import {Datasource} from "../../../common/types/datasource";

export interface DatasourceParametersComponent {
  checkQuery(): void
  saveParameters(name: string): Promise<Datasource>

  updateParameters(): Promise<Datasource>
}
