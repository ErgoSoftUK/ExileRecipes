import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppComponent } from './app.component';
import {ApplicationService} from './application.service';
import {HttpModule} from '@angular/http';
import {FormsModule} from '@angular/forms';
import { RootComponent } from './root/root.component';
import {Route, RouterModule} from '@angular/router';

const routes: Route[] = [
  {path: ':action/:term', component: AppComponent},
  {path: '**', redirectTo: 'view/', pathMatch: 'full'}
];

@NgModule({
  declarations: [
    AppComponent,
    RootComponent
  ],
  imports: [
    BrowserModule,
    HttpModule,
    FormsModule,
    RouterModule.forRoot(routes, {useHash: true})
  ],
  providers: [ApplicationService],
  bootstrap: [RootComponent]
})
export class AppModule { }
