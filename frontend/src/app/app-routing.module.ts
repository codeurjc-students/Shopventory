import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { AuthGuard } from './core/guards/auth.guard';
import { AdminGuard } from './core/guards/admin.guard';

import { MainLayoutComponent } from './shared/layout/main-layout/main-layout.component';
import { LoginComponent } from './features/auth/login/login.component';
import { RegisterComponent } from './features/auth/register/register.component';
import { DashboardComponent } from './features/dashboard/dashboard.component';
import { ProductListComponent } from './features/products/product-list/product-list.component';
import { ProductFormComponent } from './features/products/product-form/product-form.component';
import { ProductDetailComponent } from './features/products/product-detail/product-detail.component';
import { OrderListComponent } from './features/orders/order-list/order-list.component';
import { OrderFormComponent } from './features/orders/order-form/order-form.component';
import { OrderDetailComponent } from './features/orders/order-detail/order-detail.component';
import { ProviderListComponent } from './features/providers/provider-list/provider-list.component';
import { ProviderFormComponent } from './features/providers/provider-form/provider-form.component';
import { UserListComponent } from './features/users/user-list/user-list.component';
import { EmployeeListComponent } from './features/employees/employee-list/employee-list.component';
import { EmployeeFormComponent } from './features/employees/employee-form/employee-form.component';
import { NotFoundComponent } from './shared/error-pages/not-found/not-found.component';
import { ForbiddenComponent } from './shared/error-pages/forbidden/forbidden.component';
import { ServerErrorComponent } from './shared/error-pages/server-error/server-error.component';
import { PendingApprovalComponent } from './shared/error-pages/pending-approval/pending-approval.component';

const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'pending-approval', component: PendingApprovalComponent },
  { path: '403', component: ForbiddenComponent },
  { path: '404', component: NotFoundComponent },
  { path: '500', component: ServerErrorComponent },
  {
    path: '',
    component: MainLayoutComponent,
    canActivate: [AuthGuard],
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', component: DashboardComponent },
      { path: 'products', component: ProductListComponent },
      { path: 'products/new', component: ProductFormComponent, canActivate: [AdminGuard] },
      { path: 'products/:id', component: ProductDetailComponent },
      { path: 'products/:id/edit', component: ProductFormComponent, canActivate: [AdminGuard] },
      { path: 'orders', component: OrderListComponent },
      { path: 'orders/new', component: OrderFormComponent },
      { path: 'orders/:id', component: OrderDetailComponent },
      { path: 'providers', component: ProviderListComponent },
      { path: 'providers/new', component: ProviderFormComponent, canActivate: [AdminGuard] },
      { path: 'providers/:id/edit', component: ProviderFormComponent, canActivate: [AdminGuard] },
      { path: 'employees', component: EmployeeListComponent, canActivate: [AdminGuard] },
      { path: 'employees/new', component: EmployeeFormComponent, canActivate: [AdminGuard] },
      { path: 'employees/:id/edit', component: EmployeeFormComponent, canActivate: [AdminGuard] },
      { path: 'users', component: UserListComponent, canActivate: [AdminGuard] },
    ]
  },
  { path: '**', component: NotFoundComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
