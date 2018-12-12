import {Component, OnInit} from '@angular/core';
import {ApplicationService} from './application.service';
import {Recipe} from './models/Recipe';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  recipes: Recipe[];
  results: Recipe[];
  term: string;
  selectedRecipe: Recipe;

  constructor(private service: ApplicationService) {}

  ngOnInit(): void {
    this.service.loadData()
      .subscribe(
        (result) => this.recipes = this.results = result,
        (error) => console.error(error)
      );
  }

  search() {
    if (this.term == null || this.term === '') {
      this.results = this.recipes;
    } else {
      if (this.term.startsWith('returns:')) {
        const t = this.term.substr(8);
        this.results = this.recipes.filter(r =>
          r.returnedItems.filter(
            ri => ri.name.toLowerCase() === t.toLowerCase()
          ).length > 0
        );
        if (this.results.length === 1) {
          this.selectedRecipe = this.results[0];
        }
      } else {
        this.results = this.recipes.filter(r => r.name.toLowerCase().indexOf(this.term.toLowerCase()) >= 0);
      }
    }
  }

  clear() {
    this.term = null;
    this.search();
  }

  goto(name: string) {
    this.term = name;
    this.selectedRecipe = null;
    this.search();
  }

  needsNearby(): boolean {
    return this.selectedRecipe.interactionGrp !== null
      || this.selectedRecipe.requiresConcreteMixer
      || this.selectedRecipe.requiresFire
      || this.selectedRecipe.requiresOcean;
  }
}
