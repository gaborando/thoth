import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { ApiKeyListPageRoutingModule } from './api-key-list-routing.module';

import { ApiKeyListPage } from './api-key-list.page';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    ApiKeyListPageRoutingModule
  ],
  declarations: [ApiKeyListPage]
})
export class ApiKeyListPageModule {}
