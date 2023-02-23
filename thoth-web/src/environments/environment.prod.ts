
let e: any = null;
export const environment: any = async () => {
  if(!e){
    e = await fetch('/assets/env/environment.json').then(r => r.json())
  }
  return e;
}
