import { Component } from '@angular/core';

@Component({
  selector: 'app-main-layout',
  template: `
    <app-navbar></app-navbar>
    <main>
      <router-outlet></router-outlet>
    </main>`
})
export class MainLayoutComponent {}
