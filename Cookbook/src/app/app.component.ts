import {Component, OnInit} from '@angular/core';
import {ApplicationService} from './application.service';
import {Recipe} from './models/Recipe';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'app';
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
      this.results = this.recipes.filter(r => r.name.toLowerCase().indexOf(this.term.toLowerCase()) >= 0);
    }
  }

  clear() {
    this.term = null;
    this.search();
  }

  needsNearby(): boolean {
    return this.selectedRecipe.interactionGrp !== null
      || this.selectedRecipe.requiresConcreteMixer
      || this.selectedRecipe.requiresFire
      || this.selectedRecipe.requiresOcean;
  }
}
