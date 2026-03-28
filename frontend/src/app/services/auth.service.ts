import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { tap } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AuthService {

  private base = 'http://127.0.0.1:8080/api/auth';

  constructor(private http: HttpClient) {}

  login(data: any) {
    return this.http.post<any>
    (`${this.base}/login`, data,
      {withCredentials:true}
    );
  }

  setUser(user: any) {
    localStorage.setItem('user', JSON.stringify(user));
  }

  logout() {
    return this.http.post(`${this.base}/logout`, {}, {withCredentials:true, responseType: 'text'})
      .pipe(
        tap(() => localStorage.removeItem('user'))
      );
  }

  getUser() {
    return JSON.parse(localStorage.getItem('user')!);
  }
}