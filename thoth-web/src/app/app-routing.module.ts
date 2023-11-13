import {NgModule} from '@angular/core';
import {PreloadAllModules, RouterModule, Routes} from '@angular/router';

const routes: Routes = [
  {
    path: '',
    redirectTo: 'template-list',
    pathMatch: 'full'
  },
  {
    path: 'token-redirect',
    redirectTo: 'auth-hook',
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
  },
  {
    path: 'renderer-list',
    loadChildren: () => import('./pages/renderer/renderer-list/renderer-list.module').then(m => m.RenderListPageModule),
  },
  {
    path: 'datasource-list',
    loadChildren: () => import('./pages/datasource/datasource-list/datasource-list.module').then(m => m.DatasourceListPageModule),
  },
  {
    path: 'datasource-new',
    loadChildren: () => import('./pages/datasource/datasource-new/datasource-new.module').then(m => m.DatasourceNewPageModule),
  },
  {
    path: 'datasource-detail/:identifier',
    loadChildren: () => import('./pages/datasource/datasource-detail/datasource-detail.module').then(m => m.DatasourceDetailPageModule),
  },
  {
    path: 'renderer-detail/:identifier',
    loadChildren: () => import('./pages/renderer/renderer-detail/renderer-detail.module').then(m => m.RendererDetailPageModule),
  },
  {
    path: 'client-list',
    loadChildren: () => import('./pages/client/client-list/client-list.module').then(m => m.ClientListPageModule),
  },
  {
    path: 'template-detail/:identifier',
    loadChildren: () => import('./pages/template/template-detail/template-detail.module').then(m => m.TemplateDetailPageModule),
  },
  {
    path: 'api-key-list',
    loadChildren: () => import('./pages/api-key/api-key-list/api-key-list.module').then(m => m.ApiKeyListPageModule),
  },
  {
    path: 'template-jinja-editor/:identifier',
    loadChildren: () => import('./pages/template/template-jinja-editor/template-jinja-editor.module').then(m => m.TemplateJinjaEditorPageModule)
  },
  {
    path: 'auth-hook',
    loadChildren: () => import('./pages/auth-hook/auth-hook.module').then( m => m.AuthHookPageModule)
  }

];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, {preloadingStrategy: PreloadAllModules})
  ],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
