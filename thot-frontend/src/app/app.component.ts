import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: 'app.component.html',
  styleUrls: ['app.component.scss'],
})
export class AppComponent {

  public appPages = [
    { title: 'Templates', url: '/template-list', icon: 'create' },
    { title: 'Datasource', url: '/datasource-list', icon: 'server' },
    { title: 'Renderers', url: '/renderer-list', icon: 'document-text' },
    { title: 'Clients', url: '/client-list', icon: 'print' },
  ];
  constructor() {}
}
