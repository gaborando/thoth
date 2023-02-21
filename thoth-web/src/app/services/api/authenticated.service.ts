export class AuthenticatedService {

  public getHeaders(){
    return {
      "Authorization": "Bearer " + localStorage.getItem("access_token")
    }
  }

  public postHeaders(){
    return {
      'Content-Type':'application/json;charset=utf-8',
      "Authorization": "Bearer " + localStorage.getItem("access_token")
    }
  }
}
