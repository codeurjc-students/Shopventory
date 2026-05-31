import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { BaseChartDirective, provideCharts, withDefaultRegisterables } from 'ng2-charts';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { ErrorInterceptor } from './core/interceptors/error.interceptor';

import { NavbarComponent } from './shared/navbar/navbar.component';
import { MainLayoutComponent } from './shared/layout/main-layout/main-layout.component';
import { NotFoundComponent } from './shared/error-pages/not-found/not-found.component';
import { ForbiddenComponent } from './shared/error-pages/forbidden/forbidden.component';
import { ServerErrorComponent } from './shared/error-pages/server-error/server-error.component';
import { PendingApprovalComponent } from './shared/error-pages/pending-approval/pending-approval.component';

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
import { ProviderDetailComponent } from './features/providers/provider-detail/provider-detail.component';
import { UserListComponent } from './features/users/user-list/user-list.component';
import { UserDetailComponent } from './features/users/user-detail/user-detail.component';
import { EmployeeListComponent } from './features/employees/employee-list/employee-list.component';
import { EmployeeFormComponent } from './features/employees/employee-form/employee-form.component';
import { EmployeeDetailComponent } from './features/employees/employee-detail/employee-detail.component';

@NgModule({
  declarations: [
    AppComponent,
    NavbarComponent,
    MainLayoutComponent,
    NotFoundComponent,
    ForbiddenComponent,
    ServerErrorComponent,
    PendingApprovalComponent,
    LoginComponent,
    RegisterComponent,
    DashboardComponent,
    ProductListComponent,
    ProductFormComponent,
    ProductDetailComponent,
    OrderListComponent,
    OrderFormComponent,
    OrderDetailComponent,
    ProviderListComponent,
    ProviderFormComponent,
    ProviderDetailComponent,
    UserListComponent,
    UserDetailComponent,
    EmployeeListComponent,
    EmployeeFormComponent,
    EmployeeDetailComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    NgbModule,
    BaseChartDirective
  ],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true },
    provideCharts(withDefaultRegisterables())
  ],
  bootstrap: [AppComponent]
})
export class AppModule {}
