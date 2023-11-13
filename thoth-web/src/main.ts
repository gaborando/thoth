import {enableProdMode} from '@angular/core';
import {platformBrowserDynamic} from '@angular/platform-browser-dynamic';

import {AppModule} from './app/app.module';
import {environment} from './environments/environment';

import 'codemirror/mode/sql/sql';
import 'codemirror/addon/display/autorefresh';

let loginListener: any;

// @ts-ignore
window['login'] = (access_token) =>{
  localStorage.setItem("access_token", access_token || "")
   if(loginListener){
    loginListener(access_token);
  }
}

const features = {
  popup: "yes",
  width: 600,
  height: 700,
  top: "auto",
  left: "auto",
  toolbar: "no",
  menubar: "no",
};

const strWindowsFeatures = Object.entries(features)
  .reduce((str, [key, value]) => {
    if (value == "auto") {
      if (key === "top") {
        const v = Math.round(
          window.innerHeight / 2 - features.height / 2
        );
        str += `top=${v},`;
      } else if (key === "left") {
        const v = Math.round(
          window.innerWidth / 2 - features.width / 2
        );
        str += `left=${v},`;
      }
      return str;
    }

    str += `${key}=${value},`;
    return str;
  }, "")
  .slice(0, -1); // remove last ',' (comma)

window.fetch = new Proxy(window.fetch, {
  apply: function (target, that, args) {
    // @ts-ignore
    let temp = target.apply(that, args);
    return temp.then((res) => {
      // After completion of request
      if (res.status === 401) {
        return environment().then((e: any) => {
          return new Promise((resolve, reject) => {
            const w = window.open(e.oauth?.loginUrl, 'popup', strWindowsFeatures);
            if(w) {
              loginListener = (m: any) => {
                args[1].headers["Authorization"] = "Bearer " + m;
                w?.close();
                resolve(null);
              }
              window.addEventListener("beforeunload", function(e){
                console.log(e);
              });

            }
          }).then(r => {
            // @ts-ignore
            return target.apply(that, args);
          })
        })
      }else{
        return res;
      }
    });
  },
});
environment().then((e: any) => {
  if (e.production) {
    enableProdMode();
  }
})
platformBrowserDynamic().bootstrapModule(AppModule)
  .catch(err => console.log(err));
