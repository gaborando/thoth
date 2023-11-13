import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { AuthHookPageRoutingModule } from './auth-hook-routing.module';

import { AuthHookPage } from './auth-hook.page';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    AuthHookPageRoutingModule
  ],
  declarations: [AuthHookPage]
})
export class AuthHookPageModule {}
