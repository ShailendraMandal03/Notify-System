import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

export interface UserMetadata {
  id: number;
  name: string;
  email: string;
}

export interface DepartmentMetadata {
  id: number;
  name: string;
}

@Injectable({ providedIn: 'root' })
export class MetadataService {

  private base = 'http://127.0.0.1:8080/api/metadata';

  constructor(private http: HttpClient) {}

  searchUsers(email: string) {
    return this.http.get<UserMetadata[]>(
      `${this.base}/users/search?email=${email}`,
      { withCredentials: true }
    );
  }

  getDepartments() {
    return this.http.get<DepartmentMetadata[]>(
      `${this.base}/departments`,
      { withCredentials: true }
    );
  }
}
