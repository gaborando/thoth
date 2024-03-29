import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {DataSourceService} from "../../../services/api/data-source.service";
import {Datasource} from "../../../common/types/datasource";
import {
  JdbcDatasourceParametersComponent
} from "../../../components/datasource-parameters/jdbc-datasource-parameters/jdbc-datasource-parameters.component";
import {
  DatasourceParametersComponent
} from "../../../components/datasource-parameters/datasource-parameters-component";
import {ScreenMessageService} from "../../../services/screen-message.service";
import {NavController} from "@ionic/angular";

@Component({
  selector: 'app-datasource-detail',
  templateUrl: './datasource-detail.page.html',
  styleUrls: ['./datasource-detail.page.scss'],
})
export class DatasourceDetailPage implements OnInit {
  datasource?: Datasource;
  dsParameters: DatasourceParametersComponent | null = null;
  validParameters = true;

  constructor(private route: ActivatedRoute,
              private datasourceService: DataSourceService,
              private screenMessageService: ScreenMessageService,
              private navController: NavController) {
  }

  async ngOnInit() {
    this.datasource = await this.datasourceService.findById(this.route.snapshot.paramMap.get('identifier'))
  }

  async saveParameters(): Promise<any> {
    return this.screenMessageService.loadingWrapper(async () => {
      this.datasource = await this.dsParameters?.updateParameters();
      await this.screenMessageService.showDone();
    })

  }

  delete() {
    return this.screenMessageService.showDeleteAlert(async () => {
      await this.screenMessageService.loadingWrapper(async  () => {
        await this.datasourceService.deleteById(this.datasource!.id);
        return this.navController.navigateBack('/datasource-list');
      })
    })
  }

  clone() {
    if(!this.dsParameters){
      return;
    }
    return this.screenMessageService.loadingWrapper(async () => {
      const params = await this.dsParameters?.cloneParameters(this.datasource!.name);
      await this.screenMessageService.showDone();
      return this.navController.navigateForward('/datasource-detail/' + params?.id);
    })
  }
}
