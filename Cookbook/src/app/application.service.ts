import { Injectable } from '@angular/core';
import {Http} from '@angular/http';
import {Observable} from 'rxjs/Observable';
import 'rxjs/add/operator/map';
import {Recipe} from './models/Recipe';

@Injectable()
export class ApplicationService {

  constructor(private http: Http) { }

  public loadData(): Observable<Recipe[]> {
    return this.http.get('./assets/recipes.json')
      .map((res) => res.json());
  }
}
