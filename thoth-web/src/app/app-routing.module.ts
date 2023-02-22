import { NgModule } from '@angular/core';
import { PreloadAllModules, RouterModule, Routes } from '@angular/router';
import {AuthenticationGuard} from "./guards/authentication.guard";

const routes: Routes = [
  {
    path: '',
    redirectTo: 'template-list',
    pathMatch: 'full'
  },
  {
    path: 'token-redirect',
    redirectTo: 'template-list',
    pathMatch: 'full',
  },
  {
    path: 'logout',
    redirectTo: 'template-list',
    pathMatch: 'full'
  },
  {
    path: 'template-list',
    loadChildren: () => import('./pages/template/template-list/template-list.module').then(m => m.TemplateListPageModule),
    canActivate: [AuthenticationGuard]
  },
  {
    path: 'renderer-list',
    loadChildren: () => import('./pages/renderer/renderer-list/renderer-list.module').then(m => m.RenderListPageModule),
    canActivate: [AuthenticationGuard]
  },
  {
    path: 'renderer-new',
    loadChildren: () => import('./pages/renderer/renderer-new/renderer-new.module').then(m => m.RenderNewPageModule),
    canActivate: [AuthenticationGuard]
  },
  {
    path: 'datasource-list',
    loadChildren: () => import('./pages/datasource/datasource-list/datasource-list.module').then( m => m.DatasourceListPageModule),
    canActivate: [AuthenticationGuard]
  },
  {
    path: 'datasource-new',
    loadChildren: () => import('./pages/datasource/datasource-new/datasource-new.module').then( m => m.DatasourceNewPageModule),
    canActivate: [AuthenticationGuard]
  },
  {
    path: 'datasource-detail/:identifier',
    loadChildren: () => import('./pages/datasource/datasource-detail/datasource-detail.module').then( m => m.DatasourceDetailPageModule),
    canActivate: [AuthenticationGuard]
  },
  {
    path: 'renderer-detail/:identifier',
    loadChildren: () => import('./pages/renderer/renderer-detail/renderer-detail.module').then( m => m.RendererDetailPageModule),
    canActivate: [AuthenticationGuard]
  },
  {
    path: 'client-list',
    loadChildren: () => import('./pages/client/client-list/client-list.module').then( m => m.ClientListPageModule),
    canActivate: [AuthenticationGuard]
  },
  {
    path: 'template-detail/:identifier',
    loadChildren: () => import('./pages/template/template-detail/template-detail.module').then( m => m.TemplateDetailPageModule)
  },
  {
    path: 'api-key-list',
    loadChildren: () => import('./pages/api-key/api-key-list/api-key-list.module').then( m => m.ApiKeyListPageModule)
  }
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, { preloadingStrategy: PreloadAllModules })
  ],
  exports: [RouterModule]
})
export class AppRoutingModule { }
