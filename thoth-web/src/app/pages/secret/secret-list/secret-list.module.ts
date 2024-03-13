import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { SecretListPageRoutingModule } from './secret-list-routing.module';

import { SecretListPage } from './secret-list.page';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    SecretListPageRoutingModule
  ],
  declarations: [SecretListPage]
})
export class SecretListPageModule {}
