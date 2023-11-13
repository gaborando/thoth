import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-auth-hook',
  templateUrl: './auth-hook.page.html',
  styleUrls: ['./auth-hook.page.scss'],
})
export class AuthHookPage implements OnInit {

  constructor() { }

  ngOnInit() {
    const params = new URL(window.location.toString().replace("#","?"));
    if(params.searchParams.get("id_token")){
      window.opener.login(params.searchParams.get("id_token"));
    }
    window.close();
  }

}
