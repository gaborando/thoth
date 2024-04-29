import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { SecretListPageRoutingModule } from './secret-list-routing.module';

import { SecretListPage } from './secret-list.page';
import {ComponentsModule} from "../../../components/components.module";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    SecretListPageRoutingModule,
    ComponentsModule
  ],
  declarations: [SecretListPage]
})
export class SecretListPageModule {}
