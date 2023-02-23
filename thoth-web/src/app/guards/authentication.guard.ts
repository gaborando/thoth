import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, RouterStateSnapshot, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';
import {environment} from "../../environments/environment";
@Injectable({
  providedIn: 'root'
})
export class AuthenticationGuard implements CanActivate {


  constructor() {
  }

  parseJwt (token: string) {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(window.atob(base64).split('').map(function (c) {
      return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
    }).join(''));

    return JSON.parse(jsonPayload);
  }

  async canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Promise<boolean | UrlTree>  {
    const e = (await environment());
    if(!e.oauth){
      return true;
    }


    const params = new URL(window.location.toString().replace("#","?"));
    if(params.searchParams.get("access_token")){
      localStorage.setItem("access_token", params.searchParams.get("access_token") || "")
      window.history.pushState(null,
        "", window.location.pathname);
      const redirectTo = localStorage.getItem("redirect_to");
      if(redirectTo){
        localStorage.removeItem("redirect_to");
        window.location.href = redirectTo;
      }
    }

    const currentToken = localStorage.getItem("access_token");
    if(!currentToken || ((this.parseJwt(currentToken || "").exp * 1000) < new Date().getTime())){
      localStorage.setItem("redirect_to", window.location.href);
      window.location.replace(e.oauth?.loginUrl);
      return false;
    }
    return true;
  }

}
