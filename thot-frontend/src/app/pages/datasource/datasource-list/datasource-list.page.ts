import { Component, OnInit } from '@angular/core';
import {DataSourceService} from "../../../services/api/data-source.service";
import {Datasource} from "../../../common/types/datasource";

@Component({
  selector: 'app-datasource-list',
  templateUrl: './datasource-list.page.html',
  styleUrls: ['./datasource-list.page.scss'],
})
export class DatasourceListPage implements OnInit {
  datasource: Datasource[] = [];

  constructor(private datasourceService: DataSourceService) { }

  async ionViewWillEnter(){
    this.datasource = (await this.datasourceService.findAll()).content;
  }
  async ngOnInit() {

  }

  async delete(ds: Datasource) {
    this.datasource = this.datasource.filter(d => d.id !== ds.id)
    await this.datasourceService.deleteById(ds.id);

  }
}
