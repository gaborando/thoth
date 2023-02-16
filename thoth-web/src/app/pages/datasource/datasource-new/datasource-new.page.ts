import {Component, OnInit} from '@angular/core';
import {
  JdbcDatasourceParametersComponent
} from "../../../components/datasource-parameters/jdbc-datasource-parameters/jdbc-datasource-parameters.component";
import {
  DatasourceParametersComponent
} from "../../../components/datasource-parameters/datasource-parameters-component";
import {ScreenMessageService} from "../../../services/screen-message.service";
import {NavController} from "@ionic/angular";

@Component({
  selector: 'app-datasource-new',
  templateUrl: './datasource-new.page.html',
  styleUrls: ['./datasource-new.page.scss'],
})
export class DatasourceNewPage implements OnInit {

  types = [{
    name: 'jdbc',
    type: 'jdbc'
  }, {
    name: 'rest',
    type: 'rest'
  }];
  dataSourceType: any;
  dsParameters: DatasourceParametersComponent | null = null;
  validParameters = false;
  datasourceName = '';

  constructor(private screenMessageService: ScreenMessageService,
              private navController: NavController) {
  }

  ngOnInit() {
  }

  async saveParameters(): Promise<any> {
    if (!this.dsParameters) {
      return
    }
    try {
      const params = await this.dsParameters.saveParameters(this.datasourceName);
      await this.screenMessageService.showDone();
      return this.navController.navigateForward('/datasource-detail/' + params.id);
    } catch (e) {

    }

  }
}
