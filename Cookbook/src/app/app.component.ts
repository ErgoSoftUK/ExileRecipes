import {AfterViewInit, Component, OnInit} from '@angular/core';
import {ApplicationService} from './application.service';
import {Recipe} from './models/Recipe';
import {ActivatedRoute, Router} from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements AfterViewInit {
  results: Recipe[];
  selectedRecipe: Recipe;
  term: string;

  constructor(private router: Router,
              private route: ActivatedRoute,
              private service: ApplicationService) {}

  ngAfterViewInit(): void {
    this.route.params.subscribe(
      (params) => this.refresh(params['action'], params['term'])
    );
  }

  refresh(action: string, term?: string) {
    this.service.loadData()
      .subscribe(
        (result) => this.search(result, action, term),
        (error) => console.error(error)
      );
  }

  search(recipes: Recipe[], action: string, term?: string) {
    this.term = term;
    if (action === 'view') {
      const items = recipes.filter(r => r.name === term);
      this.selectedRecipe = items[0];
    }
    if (term == null || term === '') {
      this.results = recipes;
    } else {
      if (term.startsWith('returns:')) {
        const t = term.substr(8);
        this.results = recipes.filter(r =>
          r.returnedItems.filter(
            ri => ri.name.toLowerCase() === t.toLowerCase()
          ).length > 0
        );
        if (this.results.length === 1) {
          this.selectedRecipe = this.results[0];
        }
      } else {
        this.results = recipes.filter(r => r.name.toLowerCase().indexOf(term.toLowerCase()) >= 0);
      }
    }
  }

  clear() {
    this.router.navigate(['view', '']);
  }

  view(name: string) {
    this.router.navigate(['view', name]);
  }

  needsNearby(): boolean {
    return this.selectedRecipe.interactionGrp !== null
      || this.selectedRecipe.requiresConcreteMixer
      || this.selectedRecipe.requiresFire
      || this.selectedRecipe.requiresOcean;
  }
}
