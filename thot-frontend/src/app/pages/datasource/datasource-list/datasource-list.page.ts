import {Component, OnInit} from '@angular/core';
import {DataSourceService} from "../../../services/api/data-source.service";
import {Datasource} from "../../../common/types/datasource";
import {ListPage} from "../../../common/utils/ui-patterns/list-page";

@Component({
  selector: 'app-datasource-list',
  templateUrl: './datasource-list.page.html',
  styleUrls: ['./datasource-list.page.scss'],
})
export class DatasourceListPage extends ListPage<Datasource> implements OnInit {
  constructor(private datasourceService: DataSourceService) {
    super(datasourceService)
  }

  async ionViewWillEnter() {
    return super.loadPageData();
  }

  async ngOnInit() {

  }
}
