import { Injectable } from '@angular/core';
import {Http} from '@angular/http';
import {Observable} from 'rxjs/Observable';
import 'rxjs/add/operator/map';
import {Recipe} from './models/Recipe';

@Injectable()
export class ApplicationService {
  private recipes: Recipe[] = null;

  constructor(private http: Http) { }

  public loadData(): Observable<Recipe[]> {
    if (this.recipes == null) {
      return Observable.create((o) => {
        this.http.get('./assets/recipes.json')
          .map((res) => res.json())
          .subscribe(
            (result) => {
              this.recipes = result;
              o.next(result);
            },
            (error) => o.reject(error)
          );
      });
    } else {
      return Observable.create((o) => o.next(this.recipes));
    }
  }
}
