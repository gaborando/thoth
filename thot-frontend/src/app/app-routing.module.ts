import { NgModule } from '@angular/core';
import { PreloadAllModules, RouterModule, Routes } from '@angular/router';

const routes: Routes = [
  {
    path: '',
    redirectTo: 'template-list',
    pathMatch: 'full'
  },
  {
    path: 'template-list',
    loadChildren: () => import('./pages/template-list/template-list.module').then( m => m.TemplateListPageModule)
  },
  {
    path: 'renderer-list',
    loadChildren: () => import('./pages/renderer/renderer-list/renderer-list.module').then(m => m.RenderListPageModule)
  },
  {
    path: 'renderer-new',
    loadChildren: () => import('./pages/renderer/renderer-new/renderer-new.module').then(m => m.RenderNewPageModule)
  },
  {
    path: 'datasource-list',
    loadChildren: () => import('./pages/datasource/datasource-list/datasource-list.module').then( m => m.DatasourceListPageModule)
  },
  {
    path: 'datasource-new',
    loadChildren: () => import('./pages/datasource/datasource-new/datasource-new.module').then( m => m.DatasourceNewPageModule)
  },
  {
    path: 'datasource-detail/:identifier',
    loadChildren: () => import('./pages/datasource/datasource-detail/datasource-detail.module').then( m => m.DatasourceDetailPageModule)
  },
  {
    path: 'renderer-detail/:identifier',
    loadChildren: () => import('./pages/renderer/renderer-detail/renderer-detail.module').then( m => m.RendererDetailPageModule)
  },
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, { preloadingStrategy: PreloadAllModules })
  ],
  exports: [RouterModule]
})
export class AppRoutingModule { }
